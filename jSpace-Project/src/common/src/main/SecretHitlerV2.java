package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToggleButton.ToggleButtonModel;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import common.src.main.Types.ActionType;
import common.src.main.Types.CommandType;
import common.src.main.Types.LegislativeType;
import common.src.main.Types.RoleType;
import common.src.main.Types.ErrorType;
import common.src.main.Types.VoteType;

public class SecretHitlerV2 implements Runnable {

    public static Space _chatSpace;
    public static Space _userSpace;
    public static Space _gameSpace;
    public static BufferedReader _reader;

    public static User _user;
    public static int nextUserId;
    public static int chatId = 0;
    public static boolean running;
    public static GameController controller;
    public static String IP_Port;
    
    public void gameCreate(String IP_Port) {
        try {
            // Create a repository 
			SpaceRepository repository = new SpaceRepository();

			// Create a local space for the chat messages
            _chatSpace = new SequentialSpace();
            _userSpace = new SequentialSpace();
            _gameSpace = new SequentialSpace();

			// Add the space to the repository
            repository.add("chat", _chatSpace);
            repository.add("users", _userSpace);
            repository.add("game", _gameSpace);
			
			// Set the URI of the chat space
            String protocol = "tcp://";

            // Default value
            // stationær intern ip: "tcp://192.168.68.112:9001/chat?keep"
            // localhost: "tcp://127.0.0.1:9001/?keep"

			// Open a gate
			String gateURI = protocol + IP_Port + "/?keep";
			System.out.println("Opening repository gate at " + gateURI + "...");
            repository.addGate(gateURI);
            System.out.println("Gate added");

            _chatSpace.put("lock", 0);
            _userSpace.put("lock", 0);
            _gameSpace.put("lock");


            //create and start game coordinator
            controller = new GameController(_chatSpace, _userSpace, _gameSpace);
            new Thread(controller).start();
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
    }

    public Object[] gameJoin() {
        Object[] returnTriple = {null, null, null};
        try {
			// Set the URI of the chat space
			// Default value
			// Set the URI of the chat space
            String protocol = "tcp://";
            String chatSpace = "/chat?keep";
            String userSpace = "/users?keep";
            String gameSpace = "/game?keep";
            // Default value
            // localhost: "tcp://127.0.0.1:9001/?keep"
            // router extern port forwarded IP: "tcp://212.237.106.43:9001/chat?keep"
            
            String chatURI = protocol + IP_Port + chatSpace;
            String userURI = protocol + IP_Port + userSpace;
            String gameURI = protocol + IP_Port + gameSpace;

			// Connect to the remote chat space 
			System.out.println("Connecting to chat space " + chatURI + "...");
            _chatSpace = new RemoteSpace(chatURI);
            _userSpace = new RemoteSpace(userURI);
            _gameSpace = new RemoteSpace(gameURI);

            List<Object[]> players;
            Object[] gameState = _gameSpace.queryp(new ActualField("start"), new FormalField(Integer.class));
            if (gameState != null) {
                returnTriple[0] = ErrorType.GameStarted;
                return returnTriple;
            }

            players = _userSpace.queryAll(new ActualField("join"), new FormalField(String.class), new FormalField(Integer.class));
            for (Object[] player : players) if (player[1].equals(_user.Name())) {returnTriple[0] = ErrorType.NameTaken; return returnTriple;}
            if (players.size() == 10) {
                returnTriple[0] = ErrorType.GameFull;
                return returnTriple;
            }
            returnTriple[1] = players.size();
            returnTriple[2] = players.get(0)[1];

            //gameInit();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO: ERROR handling - wrong written ip fx
        returnTriple[0] = ErrorType.NoError;
        return returnTriple;

    }

    public void chatSetup() {
        // Keep sending whatever the user types
        try {
            Object[] user = _userSpace.get(new ActualField("lock"), new FormalField(Integer.class));
            Object[] initId = _chatSpace.query(new ActualField("lock"), new FormalField(Integer.class));
            chatId = (int)initId[1];
            nextUserId = (int)user[1];
            _userSpace.put("join", _user.Name(), nextUserId);
            _user.setId(nextUserId);
            _userSpace.put("lock", nextUserId+1);
            
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: handle exception
        }
    }

    public void run() {
        System.out.println("Hello mein friends!");
        boolean gameStarted = true;
        boolean imDead = false;
        try {
            int president;
            int chancellor; 
            int playerCount = -1;
            playerCount = (int) _gameSpace.query(new ActualField("start"), new FormalField(Integer.class))[1];
            MenuComponents.setGameBoard(playerCount);
            printDebug("Seen start!");
            Role[] roles = (Role[]) _gameSpace.query(new ActualField("roles"), new FormalField(Role[].class))[1];
            User[] users = (User[]) _gameSpace.query(new ActualField("users"), new FormalField(User[].class))[1];

            PlayerRole[] knownAllies = GetKnownAllies(playerCount, _user.Id(), roles, users);
            RoleType secretRole = (roles[_user.Id()].getSecretRole());
            MenuComponents.showRole(secretRole, knownAllies);
            MenuComponents.setCards(secretRole);
            _gameSpace.get(new ActualField("checkedMyRole"), new ActualField(_user.Id()));
            readAndPassRoleCheck(playerCount);
            while(gameStarted){
                //event getCard
                //listen for command
                //TODO: How do we make sure all have read the command, as it must be removed
                //  otherwise we risk someone reading an old command and getting stuck somewhere
                // Object[] commands = _gameSpace.query(new ActualField(CommandType.class));
                // CommandType cmd = (CommandType) commands[0];
                CommandType cmd = readAndPassCommand(playerCount);
                printDebug("Read command: " + cmd.toString());

                switch (cmd) {
                    case Election:
                        System.out.println("Handler starting election for :" + _user.Id() + ", " + _user.Name());
                        if (!imDead)
                            election(playerCount);
                        gameStarted = checkGameState(playerCount);
                        break;
                    case LegislativeSession:
                        //check locks in this switch
                        president = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
                        chancellor = (int) _gameSpace.query(new ActualField("chancellor"), new FormalField(Integer.class))[1];
                        readAndPassKeyWord("startLegislate", playerCount);

                        if (_user.Id() == president) {
                            System.out.println("Im president in legislative session!");
                            
                            _gameSpace.get(new ActualField("lock"));
                            Object[] cardsTuple = _gameSpace.get(new ActualField("president"), new FormalField(ArrayList.class), new FormalField(Boolean.class)); //maybe send veto bool here
                            // ArrayList<LegislativeType> cards = (ArrayList<LegislativeType>) cardsTuple[1];
                            ArrayList<LegislativeType> cards = Helper.castLegislate(cardsTuple[1]);
                            // String isString;
                            // Object card1 = cards.get(0);
                            // if ( card1 instanceof String) {
                            //     isString = "Yes";
                            // } else {
                            //     isString = "No";
                            // }
                            // System.out.println("The card was a string?: " + isString);
                            MenuComponents.chooseCards(cards.get(0), cards.get(1), cards.get(2));
                            Object[] legiChoices = _gameSpace.get(new ActualField("legiChoices"), new FormalField(ArrayList.class));
                            cards = Helper.castLegislate(legiChoices[1]);
                            System.out.println("#I have chosen cards!");
                            //Game.ChooseLegislate(cards);
                            boolean veto = (boolean) cardsTuple[2];
                            _gameSpace.put("chancellor", cards, veto);
                            _gameSpace.put("lock");
                            boolean vetoRes = (boolean) _gameSpace.get(new ActualField("veto"), new FormalField(Boolean.class))[1];
                            /**
                             * Get veto response from chancellor
                             * if/else on this
                             */
                            if (vetoRes) {
                                boolean presVeto = Game.GetVetoResponseFromPres();
                                _gameSpace.put("presVeto", presVeto);
                                //TODO: update board here
                            } else {
                                //TODO: update board here
                            }
                        } else if (_user.Id() == chancellor) {
                            _gameSpace.query(new ActualField("chancellor"), new FormalField(ArrayList.class), new FormalField(Boolean.class)); //maybe send veto bool here
                            _gameSpace.get(new ActualField("lock"));
                            Object[] cardsTuple = _gameSpace.get(new ActualField("chancellor"), new FormalField(ArrayList.class), new FormalField(Boolean.class)); //maybe send veto bool here
                            ArrayList<LegislativeType> cards = Helper.castLegislate(cardsTuple[1]);
                            boolean veto = (boolean) cardsTuple[2];
                            MenuComponents.chooseCards(cards.get(0), cards.get(1));
                            Object[] legiChoices = _gameSpace.get(new ActualField("legiChoices"), new FormalField(ArrayList.class));
                            ArrayList<LegislativeType> tempCards = Helper.castLegislate(legiChoices[1]);
                            //Game.ChooseLegislate(cards, veto);  //veto should make it possible to return 0 cards
                            /**
                             * if/else on veto
                             * send veto to pres
                             * if/else on pres answer
                             *      Get new choice with 'cards = Menu.ChanChooseLegislate(cards, false);'
                             */
                            if (tempCards.size() == 1) {
                                _gameSpace.put("veto", false);
                                cards = tempCards;
                            } else {
                                _gameSpace.put("veto", true);
                                boolean presVeto = (boolean) _gameSpace.get(new ActualField("presVeto"), new FormalField(Boolean.class))[1];
                                if (presVeto) {
                                    cards = tempCards;
                                } else {
                                    cards = Game.ChooseLegislate(cards, false);
                                }
                            }
                            
                            _gameSpace.put("chancellorReturn", cards);
                            _gameSpace.put("lock");
                        }
                        gameStarted = checkGameState(playerCount);
                        readAndPassKeyWord("endLegislate", playerCount);
                        System.out.println("L_session has happened");
                        break;
                    case ExecutiveAction:
                        president = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
                        chancellor = (int) _gameSpace.query(new ActualField("chancellor"), new FormalField(Integer.class))[1];
                        printDebug("Before readAndPassAction");
                        // ActionType executivePower = (ActionType) _gameSpace.query(new ActualField("executivePower"), new FormalField(ActionType.class))[1];
                        printDebug("Before readAndPassAction");
                        ActionType executivePower = readAndPassAction(playerCount); //maybe only pass to president
                        printDebug("After readAndPassAction");
                        
                        if (president == _user.Id()) {
                            int[] cands = Helper.castIntArray(_gameSpace.get(new ActualField("allCands"), new FormalField(ArrayList.class))[1]);
                            // System.out.print("CANDS in pres: [");
                            // for (int i = 0; i < cands.length; ++i) {
                            //     System.out.print(cands[i] + ", ");
                            // }
                            // System.out.println("]");
                            int choice = -1;
                            String suggestMsg = "";
                            switch (executivePower) {
                                case Peek:
                                    Object[] deckTuple = _gameSpace.get(new ActualField("peek"), new FormalField(ArrayList.class));
                                    ArrayList<LegislativeType> deckTop = Helper.castLegislate(deckTuple[1]);
                                    MenuComponents.showTopCards(deckTop.get(0), deckTop.get(1), deckTop.get(2));
                                    _gameSpace.get(new ActualField("iPeekedCards"));
                                    _gameSpace.put("presPeekedDeck");
                                    //get 3 cards on top
                                    //pass to president
                                    break;
                                case Investigate:
                                    /**
                                     * pass list to president
                                     * president return person
                                     * controller return info
                                     * 
                                     * alternatively:
                                     * pass list to president
                                     * president looks in 'roles' tuple for info
                                     */
                                    suggestMsg = "Who do you want to investigate?";
                                    choice = Game.suggest(cands, suggestMsg);
                                    //TODO: get name and party
                                    
                                    MenuComponents.investigatePlayer(users[choice].Name(), roles[choice].getPartyMembership());
                                    _gameSpace.get(new ActualField("investigated"));
                                    _gameSpace.put("investigatedReturn");

                                    break;
                                case Kill:
                                    /** a player is killed
                                     *      - pass list to president
                                     *      - president return person to kill
                                     */
                                    suggestMsg = "Who do you want to kill?";
                                    choice = Game.suggest(cands, suggestMsg);
                                    _gameSpace.put("dead", choice);
                                    break;
                                    
                                case S_Election:
                                    /** pass list to president
                                     *  president returns person
                                     *  use rotatePresident to choose new president
                                     *  TODO should prevent normal election of president somehow  
                                     * 
                                     */
                                    suggestMsg = "Who do you want to elect as the next president?";
                                    choice = Game.suggest(cands, suggestMsg);
                                    _gameSpace.put("specialPres", choice);
                                    break;
                                    
                                case Veto:
                                    /** a player is killed
                                     *      - pass list to president
                                     *      - president return person to kill
                                     * veto = true
                                    */
                                    suggestMsg = "Who do you want to kill?";
                                    choice = Game.suggest(cands, suggestMsg);
                                    _gameSpace.put("dead", choice);

                                    break;
                                default:    //default to None?
        
                                    break;
                            }
                        }
                        readAndPassWithController("endExecutive", playerCount);
                        imDead = CheckDeathStatus();
                        //TODO: if dead show on screen
                        System.out.println("Executive action has happened");
                        break;
                    default:
                        break;
                }
                //_gameSpace.put(new ActualField("lock")); //TODO: determine why this lock is not breaking the game
                //maybe OUTCOMMENT
            }
            System.out.println("Haha! I am a weapon of mass destruction: Spaghetti code!");
        } catch (Exception e) {
            //TODO: Player was disconnected, handle this
            e.printStackTrace();
        }
    }

    private boolean CheckDeathStatus() throws Exception {
        ArrayList<Integer> deads = Helper.castIntArrayList(_gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]);
        return deads.contains(_user.Id());
    }

    private PlayerRole[] GetKnownAllies(int playerCount, int id, Role[] roles, User[] users) {
        if (roles[id].getSecretRole() == RoleType.Liberal) return null;
        else if (6 < playerCount && roles[id].getSecretRole() == RoleType.Hitler) return null;
        else {
            int length = playerCount < 7? 1 : playerCount < 9? 2: 3;
            PlayerRole[] res = new PlayerRole[length];
            int j = 0;
            for (int i = 0; i < playerCount; ++i) {
                if (roles[i].getPartyMembership() == RoleType.Fascist && i != id) {
                    PlayerRole pr = new PlayerRole(users[i].Name(), roles[i].getSecretRole());
                    res[j++] = pr;
                }
            }
            return res;
        }
    }

    private boolean checkGameState(int playerCount) throws Exception {
        /*Function to check the incoming game state. Returns false if the game is over, and true otherwise
        0 = liberal law passed - game continues
        1 = fascist law passed - game continues
        2 = liberal win (either by passing law, or assassinating Hitler)
        3 = fascist win (either by passing law, or hitler elected chancellor)
        4 = game continues (not hitler elected, someone else executed)
        5 = election tracker is incremented
        */
        int gameState = readAndPassGameState(playerCount);
        switch (gameState) {
            case 0:
                MenuComponents.incLibArticles();
                break;
            case 1:
                MenuComponents.incFascArticles();
                break;
            case 2:
                MenuComponents.incLibArticles();
                return false;
            case 3:
                MenuComponents.incFascArticles();
                return false;

            case 4:
                break;
            case 5:
                MenuComponents.incLibFails();
                break;

            default:
                throw new RuntimeException("Something went wrong checking gamestate. Gamestate is: " + gameState);
        }
        return true;
    }

    private static void election(int playerCount) throws InterruptedException {
        // Init election, and suggest chancellor if player is president.
        Boolean electionDone;
        Object[] newElect = _gameSpace.query(new ActualField("suggest"), new FormalField(Integer.class), new FormalField(ArrayList.class));
        int pres = (int) newElect[1];
        
        int suggestion = -1;
        if (_user.Id() == pres) {
            // ArrayList<Integer> eligibleCands = (ArrayList<Integer>) ((ArrayList<Integer>) newElect[2]).clone();
            //ArrayList<Integer> eligibleCands = Helper.cleanCast(newElect[2]);
            int[] eliCands = Helper.castIntArray(newElect[2]);
            
            Helper.appendAndSend(_user.Name() + " is President in this round");
            Helper.appendAndSend("The president is suggesting a chancellor");
            // ArrayList<Integer> eligibleCands = Helper.cleanCast(newElect[2]);
            // Helper.printArray("Cands", eligibleCands.toArray());
            String suggestMsg = "Who should be suggested chancellor?";
            suggestion = Game.suggest(eliCands, suggestMsg);
            _gameSpace.get(new ActualField("lock"));
            _gameSpace.put("suggestion", suggestion);
            _gameSpace.put("lock");
        } else {
            suggestion = (int) _gameSpace.query(new ActualField("suggestion"), new FormalField(Integer.class))[1];
        }

        // Vote in GUI
        VoteType vote = Game.vote(suggestion, pres == _user.Id());
        // Put vote in the gameSpace tuple space.
        // Also update GUI with incoming votes until vote is complete.
        _gameSpace.query(new ActualField("startVote"));
        _gameSpace.get(new ActualField("lock"));
        Object[] voteObj = _gameSpace.get(new ActualField("votes"), new FormalField(VoteType[].class), new FormalField(Integer.class));
        VoteType[] votes = (VoteType[]) voteObj[1];
        int voterId = (int) voteObj[2] + 1;
        System.out.println("I read voterId:"+((int) voteObj[2]));
        votes[_user.Id()] = vote;
        _gameSpace.put("votes", votes, voterId);
        _gameSpace.put("lock");
        System.out.println("I voted with voterId: " + voterId);
        // Game.updateVotes(votes);
        int deadPlayers = ((ArrayList<?>) _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]).size();
        System.out.println("Player count: " + playerCount + "\n deadPlayers: " + deadPlayers);
        electionDone = (voterId == (playerCount - deadPlayers));
        while(!electionDone) {
            voteObj = _gameSpace.query(new ActualField("votes"), new FormalField(VoteType[].class), new FormalField(Integer.class));
            votes = (VoteType[]) voteObj[1];
            // Game.updateVotes(votes);
            electionDone = ((int) voteObj[2] == (playerCount - deadPlayers));
        }
        System.out.println("Election is done!");
    }

    public void sendMessage(String msg, ChatHandler chatHandler, boolean chatBot) {
        try {
            Object[] newChat = _chatSpace.get(new ActualField("lock"), new FormalField(Integer.class));
            int newChatId = (int)newChat[1];
            chatHandler.incChatId();
            String sender = chatBot ? "ChatBot" : _user.Name();
            _chatSpace.put(sender, msg, newChatId);
            System.out.println(sender + ": " + msg);
            newChatId++;
            _chatSpace.put("lock", newChatId);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }
    
    /*
    public static void chatHandler() throws Exception{
        String message;
        Object[] newUser = _userSpace.queryp(new ActualField("join"), new FormalField(String.class), new ActualField(nextUserId));
        Object[] newChat = _chatSpace.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField(chatId));
        if (newChat != null) {
            System.out.println(newChat[0] + ": " + newChat[1]);
            chatId++;
        }
        if (newUser != null) {
            System.out.println(newUser[1] + " has joined the game!");
            nextUserId++;
        }
        if (_reader.ready() && (message = _reader.readLine()) != null) {
            newChat = _chatSpace.get(new ActualField("lock"), new FormalField(Integer.class));
            // 
            chatId = (int)newChat[1];
            _chatSpace.put(_user.Name(), message, chatId);
            System.out.println(_user.Name() + ": " + message);
            chatId++;
            _chatSpace.put("lock", chatId);
        }
    } */

    private void readAndPassKeyWord(String string, int playerCount) throws Exception {
        _gameSpace.get(new ActualField(string), new ActualField(_user.Id()));
        if (_user.Id() != playerCount-1) {
            _gameSpace.put(string, _user.Id()+1);
        }
        System.out.println("Read keyword:" + string);
    }

    private void readAndPassRoleCheck(int playerCount) throws Exception {
        _gameSpace.get(new ActualField("checkRoles"), new ActualField(_user.Id()));
        if (_user.Id() < playerCount - 1) {
            _gameSpace.put("checkRoles", _user.Id()+1);
        } else {
            _gameSpace.put("rolesChecked");
        }
        System.out.println("Read keyword: checkRoles");
    }

    private void readAndPassWithController(String string,int playerCount) throws Exception {
        _gameSpace.get(new ActualField(string), new ActualField(_user.Id()));
        if (_user.Id() < playerCount - 1) {
            _gameSpace.put(string, _user.Id()+1);
        } else {
            _gameSpace.put(string + "ReturnToCon");
        }
        System.out.println("Read keyword: " + string);
    }

    private CommandType readAndPassCommand(int playerCount) throws Exception {
        //TODO: maybe handle dead players
        CommandType cmd = (CommandType) _gameSpace.get(new FormalField(CommandType.class), new ActualField(_user.Id()))[0];
        if (_user.Id() != playerCount-1) {
            _gameSpace.put(cmd, _user.Id()+1);
        }
        return cmd;
    }

    private ActionType readAndPassAction(int playerCount) throws Exception {
        //TODO: maybe handle dead players
        ActionType power = (ActionType) _gameSpace.get(new ActualField("executivePower"), new FormalField(ActionType.class), new ActualField(_user.Id()))[1];
        if (_user.Id() != playerCount-1) {
            _gameSpace.put("executivePower", power, _user.Id()+1);
        }
        return power;
    }

    private int readAndPassGameState(int playerCount) throws Exception {
        //TODO: maybe handle dead players
        int gameState = (int) _gameSpace.get(new ActualField("gameState"), new FormalField(Integer.class), new ActualField(_user.Id()))[1];
        if (_user.Id() != playerCount-1) {
            _gameSpace.put("gameState", gameState, _user.Id()+1);
        }
        return gameState;
    }

    private void printDebug(String string) {
        if (true) {
            System.out.println(string);
        }
    }

    //  POSSIBLE REAFACTORING
    // private void readAndPass(Object obj, int playerCount) throws Exception {
    //     _gameSpace.get(new ActualField(obj), new ActualField(_user.Id()));
    //     if (_user.Id() != playerCount-1) {
    //         _gameSpace.put(obj, _user.Id());
    //     } 
    // }

    public void leaveGame() throws InterruptedException {
        _userSpace.put("leave", _user.Name(), _user.Id());
    }

    public void setUser(String name) {
        _user = new User(name);
    }

    public void setIP_Port(String address) {
        IP_Port = address;
    }

    public Space getUserSpace() {
        return _userSpace;
    }

    public Space getChatSpace() {
        return _chatSpace;
    }

    public int getChatId() {
        return chatId;
    }

    public User getUser() {
        return _user;
    }

    public Space getGameSpace() {
        return _gameSpace;
    }

    public SecretHitlerV2() {
    }

}
