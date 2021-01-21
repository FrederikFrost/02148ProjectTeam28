package common.src.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Types.ActionType;
import common.src.main.Types.CommandType;
import common.src.main.Types.LegislativeType;
import common.src.main.Types.RoleType;
import common.src.main.Types.VoteType;

public class GameController implements Runnable {
    public Space _chatSpace;
    public Space _userSpace;
    public Space _gameSpace;
    private int playerCount;
    private int lastPres;
    private int lastChancellor;
    private ArrayList<LegislativeType> deck;
    private boolean veto = false;
    private boolean debug = true;
    private LegislativeType[] liberalBoard;
    private LegislativeType[] fascistBoard;
    ActionType[] executivePowers;
    Role[] roles;

    public GameController(Space _chatSpace, Space _userSpace,Space _gameSpace) {
        this._chatSpace = _chatSpace;
        this._userSpace = _userSpace;
        this._gameSpace = _gameSpace;
    }

    //only used for test!
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    //only used for test!
    public void setLastPres(int lastPres) {
        this.lastPres = lastPres;
    }

    //only used for test!
    public void setLastChancellor(int lastChancellor) {
        this.lastChancellor = lastChancellor;
    }

    //only used for test!
    public void setDeck(ArrayList<LegislativeType> deck){
        this.deck = deck;
    }

    public ArrayList<LegislativeType> getDeck() {
        return deck;
    }

    @Override
    public void run() {
        // boolean enoughPlayers = false;
        // boolean gameStarted = false;
        playerCount = -1;
        boolean gameStarted = true;
        try {
            // while (!gameStarted) {
            //     if (!enoughPlayers) {
            //         Object[] userLock = _userSpace.query(new ActualField("lock"), new FormalField(Integer.class));
            //         if ((int) userLock[1] >= 5) {
            //             enoughPlayers = true;
            //             _gameSpace.get(new ActualField("lock"));
            //             _gameSpace.put("readyToStart");
            //             _gameSpace.put("lock");
            //         }
            //     } else {
            //         _gameSpace.get(new ActualField("start"));
            //         playerCount = (int) _userSpace.query(new ActualField("lock"), new FormalField(Integer.class))[1];
            //         //TODO: maybe grap userSpace lock to prevent players from joining a started game
            //         gameStarted = true;
            //     }
            // }

            Object[] startTuple = _gameSpace.query(new ActualField("start"), new FormalField(Integer.class));
            playerCount = (int) startTuple[1];
            if (playerCount < 5 || 10 < playerCount) throw new IllegalArgumentException("Too few or too many players!");
            printDebug("Game started! PlayerCount: " + playerCount);
            //TODO: maybe grap userSpace lock to prevent players from joining a started game

            _gameSpace.get(new ActualField("lock"));
            
            SetupGame();
            AssignRoles();
            _gameSpace.put("checkRoles", 0);
            _gameSpace.get(new ActualField("rolesChecked"));
            
            _gameSpace.put("lock");
            printDebug("Starting game loop");
            boolean useOldPres = false;
            int electionTracker = 0;
            while(gameStarted) {
                /**
                 * Suggest chancellor
                 * Vote chancellor
                 * loop if chancellor isn't chosen
                 *  - counter for inactive 
                 * 
                 * choose legislate
                 *  - show president 3 cards
                 *  - president discards 1, show chancellor remaining cards
                 *  - chancellor picks legislate
                 *     - veto logic should be here
                 * 
                 * executive action
                 * 
                 * rotate president (except if executive action: Special Election)
                 * 
                */

                //game started
                Boolean elected = false;
                int suggestedChancellor = -1;
                while(elected == false && electionTracker < 3) {
                    if (electionTracker != 0) useOldPres = rotatePresident(useOldPres);
                    printDebug("Where is my money!?");
                    suggestedChancellor = SuggestChancellor();
                    printDebug("Got suggestion");
                    elected = Election(suggestedChancellor);
                    electionTracker++;
                    if (!elected && electionTracker < 3) {
                        // Putting gamestate = 4 to signify game continue.
                        _gameSpace.put("gameState", 5, 0);
                    }  
                }

                ArrayList<LegislativeType> cards;
                if (!elected) {
                    electionTracker = 0;
                    // skip choose legislate - boolean is probably the easiest
                    cards = GetCardsFromDeck(1);
                    //get 1 card, no preview (no preview means the cards are removed from the deck)
                    //directly update boards
                    //term-limit forgotten
                    ResetTermLimits();
                    //executive power is ignored
                    UpdateBoards(cards.get(0)); //return ActionType, but it is ignored here!
                    //win check
                    useOldPres = rotatePresident(useOldPres);
                } else {
                    //win check (chancellor has been chosen)
                    /*Gamestate is 3 if fascist win by electing hitler, otherwise 4 to continue game*/
                    int gameState = (fascistBoard[2] == LegislativeType.Fascist && roles[suggestedChancellor].getSecretRole() == RoleType.Hitler) ? 3 : 4;
                    _gameSpace.put("gameState", gameState, 0);
                    if (gameState == 3) {
                        Helper.appendAndSend("Hitler was elected chancellor with 3 fascist laws passed. Fascists win!");
                        gameStarted = false;
                        break;
                    }     

                    _gameSpace.get(new ActualField("lock"));
                    _gameSpace.put(CommandType.LegislativeSession, 0);
                    _gameSpace.put("lock");
                    
                    electionTracker = 0;
                    //check win
                    cards = GetCardsFromDeck(3);

                    _gameSpace.get(new ActualField("lock"));
                    _gameSpace.put("president", cards, veto); //maybe send veto bool here
                    _gameSpace.put("startLegislate", 0);
                    _gameSpace.put("lock");


                    cards = Helper.castLegislate(_gameSpace.get(new ActualField("chancellorReturn"), new FormalField(ArrayList.class))[1]);
                    if (1 < cards.size()) throw new IllegalArgumentException("Too many legislatives left"); 

                    ActionType executivePower;
                    if (cards.size() == 1) {
                        LegislativeType finalCard = cards.get(0); //take the chosen card
                        executivePower = UpdateBoards(finalCard);
                        //win check
                    } else {    //in case of veto
                        electionTracker++;
                        executivePower = ActionType.None;
                    }

                    _gameSpace.put("endLegislate", 0);
                    printDebug("Finished legislative session");

                    /** legislative session
                     * Get 3 cards, no preview
                     * send to president and chancellor
                     * veto logic should be here
                     * else update board with returned legislate
                    */

                    _gameSpace.get(new ActualField("lock"));
                    _gameSpace.put(CommandType.ExecutiveAction, 0);
                    _gameSpace.put("lock");

                    _gameSpace.put("executivePower", executivePower, 0);
                    ArrayList<Integer> cands = GetAllCands();
                    Helper.printArray("cands", cands.toArray());
                    _gameSpace.put("allCands", cands);

                    //TODO: put executetive power up in gamespace
                    int killedPlayer = -1;
                    switch (executivePower) {
                        case Peek:
                            printDebug("Controller started peeking!");
                            cards = GetCardsFromDeck(3, true);
                            printDebug("Controller got 3 cards!");
                            _gameSpace.get(new ActualField("lock"));
                            _gameSpace.put("peek", cards);
                            _gameSpace.put("lock");
                            _gameSpace.get(new ActualField("presPeekedDeck"));
                            // _gameSpace.query("")
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
                            _gameSpace.get(new ActualField("investigatedReturn"));

                            // _gameSpace.query(new ActualField("finishInvestigate"));
                            break;
                        case Kill:
                            /** a player is killed
                             *      - pass list to president
                             *      - president return person to kill
                             */
                            killedPlayer = (int) _gameSpace.get(new ActualField("dead"), new FormalField(Integer.class))[1];
                            ExecutePlayer(killedPlayer);
                            

                            break;
                            
                        case S_Election:
                            /** pass list to president
                             *  president returns person
                             *  use rotatePresident to choose new president
                             *  TODO should prevent normal election of president somehow  
                             * 
                             */
                            int specialPres = (int) _gameSpace.get(new ActualField("specialPres"), new FormalField(Integer.class))[1];
                            useOldPres = rotatePresident(useOldPres, specialPres);

                            break;
                            
                        case Veto:
                            /** a player is killed
                             *      - pass list to president
                             *      - president return person to kill
                             * veto = true
                            */
                            killedPlayer = (int) _gameSpace.get(new ActualField("dead"), new FormalField(Integer.class))[1];
                            ExecutePlayer(killedPlayer);
                            veto = true;
                            break;
                        default:    //default to None?

                            break;
                    }
                    if (killedPlayer != -1 && roles[killedPlayer].getSecretRole() == RoleType.Hitler) {
                        _gameSpace.put("gameState", 2, 0);
                        gameStarted = false;
                        Helper.appendAndSend("Hitler was executed. Liberals win!");
                    } else {
                        _gameSpace.put("gameState", 4, 0);
                    }

                    passAndWaitForReturn("endExecutive");
                    if (executivePower != ActionType.S_Election) useOldPres = rotatePresident(useOldPres);
                    //executive power is in else statement as this is the case where it is NOT ignored
                    /** executive power
                     * check for executive power
                     * use if one is apparent 
                    */
                }
                
                //rotatePresident here - what about executive power?
            }
            printDebug("Haha! I am stopped ;) ;)");
            
        } catch (Exception e) {
            //possibly put tuble in game space to let players know an error occured
            e.printStackTrace();
        }
    }

    private void ExecutePlayer(int killedPlayer) throws Exception {
        _gameSpace.get(new ActualField("lock"));
        ArrayList<Integer> deads = Helper.castIntArrayList(_gameSpace.get(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]);
        deads.add(killedPlayer);
        _gameSpace.put("deadPlayers", deads);
        _gameSpace.put("lock");
    }

    private void passAndWaitForReturn(String string) throws Exception {
        _gameSpace.put(string, 0);
        _gameSpace.get(new ActualField(string + "ReturnToCon"));
    }

    private ArrayList<Integer> GetAllCands() throws Exception, InterruptedException {
        int president = getPresident();
        ArrayList<Integer> cands = new ArrayList<>();
        ArrayList<Integer> deads = Helper.castIntArrayList(_gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]);
        Helper.printArray("In GetAllCands: deads", deads.toArray());
        for (int i = 0; i < playerCount; ++i) {
            if (!deads.contains((Integer) i) && i != president)
                cands.add(i);
        }
        return cands;
    }

    public ActionType UpdateBoards(LegislativeType legislativeType) throws Exception { // TODO make test
        if (legislativeType == LegislativeType.None) throw new IllegalArgumentException("Card cannot be none!");
        // Object[] boards = _gameSpace.get(new ActualField("boards"), new FormalField(LegislativeType[].class), new FormalField(LegislativeType[].class), new FormalField(ActionType[].class));
        // liberalBoard = (LegislativeType[]) boards[1];
        // fascistBoard = (LegislativeType[]) boards[2];
        // ActionType[] executivePowers = (ActionType[]) boards[3];
        
        ActionType res;
        int gameState = -1; // -1 = error, 0 = liberal law - continue, 1 = fascist law - continue, 2 = liberal win, 3 = fascist win.
        int index = -1;
        if (legislativeType == LegislativeType.Liberal) {
            index = GetEmptyIndex(liberalBoard);
            gameState = (index == 4 ? 2 : 0);
            liberalBoard[index] = LegislativeType.Liberal;
            res = ActionType.None;
        } else {
            index = GetEmptyIndex(fascistBoard);
            gameState = (index == 5 ? 3 : 1);
            fascistBoard[index] = LegislativeType.Fascist;
            res = executivePowers[index];
        }
        Helper.appendAndSend("A " + legislativeType.toString() + " law was passed! \n "
            + (index + 1) + " of these laws were passed! \n gameState is: " + gameState + "!\n\n");
        // _gameSpace.put("boards", liberalBoard, fascistBoard, executivePowers);
        _gameSpace.get(new ActualField("lock"));
        switch (gameState) {
            case -1:
                throw new RuntimeException("Inconsistent game state = -1!");
            case 2:
                Helper.appendAndSend("Liberals won by passing 5 laws!");
                break;
            case 3:
                Helper.appendAndSend("Fascists won by passing 6 laws!");
                break;
            default:
                break;
        }
        _gameSpace.put("gameState", gameState, 0);
        _gameSpace.put("lock");
        return res;
    }

    private int GetEmptyIndex(LegislativeType[] board) throws Exception {
        //6 is max length for array
        for (int i = 0; i < 6; ++i) {
            if (board[i] == LegislativeType.None) {
                return i;
            }
        }
        throw new IllegalArgumentException("Board is full or has wrong length");
    }

    public ArrayList<LegislativeType> GetCardsFromDeck(int numberOfCards, boolean preview) throws Exception {
        //logic depending on the remaining cards in deck
        if (deck.size() < numberOfCards) {
            deck = GetShuffledDeck();
        }

        ArrayList<LegislativeType> res = new ArrayList<LegislativeType>(numberOfCards);
        for (int i = 0; i < numberOfCards; ++i) {
            if (preview) {
                res.add(deck.get(deck.size()-1));
            } else {
                res.add(deck.remove(deck.size()-1));
            }
        }

        _gameSpace.get(new ActualField("lock"));

        _gameSpace.get(new ActualField("drawPile"), new FormalField(Integer.class));
        _gameSpace.get(new ActualField("discardPile"), new FormalField(Integer.class));
        _gameSpace.put("drawPile", deck.size());
        _gameSpace.put("discardPile", 17-deck.size());

        _gameSpace.put("lock");
        
        return res;
    }

    public ArrayList<LegislativeType> GetCardsFromDeck(int numberOfCards) throws Exception { 
        return GetCardsFromDeck(numberOfCards, false);
    }

    public boolean rotatePresident(boolean useOldPres, int newPres) throws Exception {
        int oldPres = getOldPresident();
        int currPres = getPresident();
        setOldPresident(currPres);
  
        if (newPres != -1) {
            setPresident(newPres);
            return true;
        } else if (useOldPres) {
            setPresident(getNextPresident(oldPres));
            return false;
        } else {
            setPresident(getNextPresident(currPres));
            return false;
        }
        
    }

    public boolean rotatePresident(boolean useOldPres) throws Exception {
        return rotatePresident(useOldPres, -1);
    }

    public int getNextPresident(int pres) throws Exception {
        // ArrayList<Integer> deads = new ArrayList<Integer>();
        // for (Object obj : ((ArrayList<?>) _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1])) {
        //     deads.add((int) obj);    
        // }

        // ArrayList<Integer> deads = Helper.cleanCast(_gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]);
        ArrayList<Integer> deads = Helper.castIntArrayList(_gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]);
        int newPres = pres;
        do {
            newPres = (newPres+1) % playerCount;
        } while(deads.contains(newPres));

        return newPres;
    }

    public int SuggestChancellor() throws Exception {
        
        int pres = getPresident();
        ArrayList<Integer> suggestions = GetEligibleCandidates();
        printDebug("Got candidates");

        _gameSpace.get(new ActualField("lock"));
        _gameSpace.put(CommandType.Election, 0);
        _gameSpace.put("suggest", pres, suggestions);
        _gameSpace.put("lock");
        printDebug("Put suggest thingy");
        //TODO: should a lock be here aswell?
        return (int) _gameSpace.query(new ActualField("suggestion"), new FormalField(Integer.class)) [1];
    }

    public ArrayList<Integer> GetEligibleCandidates() throws Exception {
        // ArrayList<Integer> deads = Helper.cleanCast(_gameSpace.query(new ActualField("deadPlayers"),
        //     new FormalField(ArrayList.class))[1]);

        int pres = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
        Object[] deadsTuple = _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class));
        ArrayList<Integer> deads = Helper.castIntArrayList(deadsTuple[1]);
        ArrayList<Integer> ids = new ArrayList<Integer>(playerCount);
        for(int i = 0; i < playerCount; i++){
            ids.add(i);
        }

        //gather all none-eligible in list
        if (!deads.contains(lastPres)) deads.add(lastPres);
        if (!deads.contains(lastChancellor)) deads.add(lastChancellor);
        if (!deads.contains(pres)) deads.add(pres);

        //as 'list.remove(int)' removes index, the list must be in descending order
        Collections.sort(deads, Collections.reverseOrder());    //might not be needed

        for (Integer nonEligible : deads) { //TODO: test when lastPres = lastChancellor = -1 
            if (nonEligible != -1)
                ids.remove(nonEligible.intValue());
        }

        return ids;
    }

    public Boolean Election(int newChancellor) throws Exception {
        //maybe change to ArrayList<int[]>
        _gameSpace.get(new ActualField("lock"));

        VoteType[] votes = new VoteType[playerCount];  //should account for dead players
        Arrays.fill(votes, VoteType.None);
        _gameSpace.getp(new ActualField("votes"), new FormalField(VoteType[].class), new ActualField(playerCount)); 
        // should also account for votes

        _gameSpace.put("votes", votes, 0); 
        _gameSpace.put("startVote");
        _gameSpace.put("lock");

        Object[] deads = _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class));
        int deadPlayers = (Helper.castIntArrayList(deads[1])).size();
        
        //int deadPlayers = ((ArrayList<?>) _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]).size();
        Helper.printArray("dead players", (Helper.castIntArrayList(deads[1])).toArray());
        printDebug("Player count: " + playerCount + "\n deadPlayers: " + deadPlayers);
        Object[] votesReturn = _gameSpace.query(new ActualField("votes"), new FormalField(VoteType[].class), new ActualField(playerCount-deadPlayers));    //should also account for votes

        _gameSpace.getp(new ActualField("suggest"), new FormalField(Integer.class), new FormalField(ArrayList.class));
        _gameSpace.getp(new ActualField("suggestion"), new FormalField(Integer.class));

        int numToPass = (playerCount-deadPlayers)/2+1;
        int votesChancellor = 0;
        for (VoteType vote : (VoteType[]) votesReturn[1]) {
            if (vote == VoteType.Ja) {
                votesChancellor++;
            }
        }
        if (votesChancellor >= numToPass) {
            //set term-limit
            updateTermLimit(newChancellor);
            setChancellor(newChancellor);
            printDebug("We won the war!");

            return true;
        }
        printDebug("We lost the war!");
        return false;
    }

    private void updateTermLimit(int chancellor) throws Exception {
        int pres = getPresident();
        lastPres = playerCount > 5 ? pres : -1;
        lastChancellor = chancellor;
    }

    private void ResetTermLimits() {
        int lastPres = -1;
        int lastChancellor = -1;
    }

    // #region setup
    public void AssignRoles() throws Exception {

        Role liberal = new Role(RoleType.Liberal, RoleType.Liberal);
        Role fascist = new Role(RoleType.Fascist, RoleType.Fascist);
        Role hitler = new Role(RoleType.Fascist, RoleType.Hitler);

        switch (playerCount) {
            case 5:
                roles = new Role[] {liberal, liberal, liberal, fascist, hitler};
                break;
            case 6:
                roles = new Role[] {liberal, liberal, liberal, liberal, fascist, hitler};
                break;     
            case 7:
                roles = new Role[] {liberal, liberal, liberal, liberal, fascist, fascist, hitler};
                break;
            case 8:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, fascist, fascist, hitler};
                break;
            case 9:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, fascist, fascist, fascist, hitler};
                break;
            case 10:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, liberal, fascist, fascist, fascist, hitler};
                break;
            default:
                throw new IllegalArgumentException("Player size is wrong");   //maybe refactor this to if statement above
        }

        CreateAndUploadUserArray();

        Collections.shuffle(Arrays.asList(roles));
        _gameSpace.put("roles", roles);
        printDebug("Assigned roles!");
        //TODO show fascist who they are working with
        //  5-6 hitler and fascist know eachother
        //  7-10 fascist know eachother and hitler, hitler doesn't know anything
        // possible solutions: 2 queries, 1 for fascist and 1 for hitler
        //               else: 1 query with all info, hitler checks playerCount to see if he can look at query 


        Random rand = new Random();
        int president = rand.nextInt(playerCount);
        _gameSpace.put("lock");
        setPresident(president);
        setOldPresident(-1);
        _gameSpace.get(new ActualField("lock"));


        //make dead player list
        ArrayList<Integer> deadPlayers = new ArrayList<Integer>();
        _gameSpace.put("deadPlayers", deadPlayers);

        lastPres = -1;
        lastChancellor = -1;
        printDebug("Assigned president and reset values");

    }

    private void CreateAndUploadUserArray() throws Exception {
        User[] users = new User[playerCount];
        for (int i = 0; i < playerCount; ++i) {
            Object[] userTuple = _userSpace.query(new ActualField("join"), new FormalField(String.class), new ActualField(i));
            users[i] = new User((String) userTuple[1], (int) userTuple[2]);
        }

        _gameSpace.getp(new ActualField("users"), new FormalField(User[].class));
        _gameSpace.put("users", users);
        printDebug("uploaded user array");
    }

    public void SetupGame() throws Exception {
        if (playerCount < 5) throw new IllegalArgumentException("Player count cannot be less than 5." + (playerCount==-1? "PlayerCount was not set":""));

        liberalBoard = new LegislativeType[5];
        fascistBoard = new LegislativeType[6];
        Arrays.fill(liberalBoard, LegislativeType.None);
        Arrays.fill(fascistBoard, LegislativeType.None);

        //set presedential powers
        switch (playerCount) {
            case 5:
            case 6:
                executivePowers = new ActionType[] {ActionType.None, ActionType.None, ActionType.Peek, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;     
            case 7:
            case 8:
                executivePowers = new ActionType[] {ActionType.None, ActionType.Investigate, ActionType.S_Election, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;
            case 9:
            case 10:
                executivePowers = new ActionType[] {ActionType.Investigate, ActionType.Investigate, ActionType.S_Election, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;
            default:
                executivePowers = new ActionType[6];
                throw new IllegalArgumentException("Cannot be more than 10 players");   //maybe refactor this to if statement above
        }

        deck = GetShuffledDeck();

        // _gameSpace.put("boards", liberalBoard, fascistBoard, executivePowers);
        // _gameSpace.put("deck", deck);   //possibly shouldn't be there, depends how the users gameLoop's logic is implemented
        _gameSpace.put("drawPile", 17);
        _gameSpace.put("discardPile", 0);
        printDebug("Created boards and deck - Uploaded boards to _gameSpace");

    }
    //#endregion

    public ArrayList<LegislativeType> GetShuffledDeck() {
        int liberalCards = 6;
        int fascistCards = 11;

        LegislativeType[] deck = new LegislativeType[17];
        Random rand = new Random();
        for (int i = 0; i < 17; ++i) {
            if ((liberalCards > 0 && rand.nextInt(3) == 0) || fascistCards == 0) {
                deck[i] = LegislativeType.Liberal;
                liberalCards--;
            } else {
                deck[i] = LegislativeType.Fascist;
                fascistCards--;
            }
        }
        return new ArrayList<LegislativeType>(Arrays.asList(deck));
    }

    //#region getters and setters

    private int getPresident() throws Exception {
        return (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
    }

    private void setPresident(int president) throws Exception {
        _gameSpace.get(new ActualField("lock"));
        _gameSpace.getp(new ActualField("president"), new FormalField(Integer.class));
        _gameSpace.put("president", president);
        _gameSpace.put("lock");
    }

    private int getOldPresident() throws Exception {
        return (int) _gameSpace.query(new ActualField("oldPresident"), new FormalField(Integer.class))[1];
    }

    private void setOldPresident(int president) throws Exception {
        _gameSpace.get(new ActualField("lock"));
        _gameSpace.getp(new ActualField("oldPresident"), new FormalField(Integer.class));
        _gameSpace.put("oldPresident", president);
        _gameSpace.put("lock");
    }

    private int getChancellor() throws Exception {
        return (int) _gameSpace.query(new ActualField("chancellor"), new FormalField(Integer.class))[1];
    }

    private void setChancellor(int chancellor) throws Exception {
        _gameSpace.get(new ActualField("lock"));
        _gameSpace.getp(new ActualField("chancellor"), new FormalField(Integer.class));
        _gameSpace.put("chancellor", chancellor);
        _gameSpace.put("lock");
    }

    private int getOldChancellor() throws Exception {
        return (int) _gameSpace.query(new ActualField("OldChancellor"), new FormalField(Integer.class))[1];
    }

    private void setOldChancellor(int chancellor) throws Exception {
        _gameSpace.get(new ActualField("lock"));
        _gameSpace.getp(new ActualField("OldChancellor"), new FormalField(Integer.class));
        _gameSpace.put("OldChancellor", chancellor);
        _gameSpace.put("lock");
    }

    //#endregion

    private void printDebug(String string) {
        if (debug) {
            System.out.println(string);
        }
    }
}
