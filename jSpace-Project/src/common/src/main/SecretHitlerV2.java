package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import common.src.main.Types.CommandType;
import common.src.main.Types.VoteType;

public class SecretHitlerV2 {

    public static Space _chatSpace;
    public static Space _userSpace;
    public static Space _gameSpace;
    public static BufferedReader _reader;

    public static User _user;
    public static int nextUserId;
    public static int chatId = 0;
    public static boolean running;
    public static GameController controller;
    
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
            _gameSpace.put("lock", 0);


            //create and start game coordinator
            controller = new GameController(_chatSpace, _userSpace, _gameSpace);
            new Thread(controller).start();
            gameInit();
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
    }

    public void gameJoin(String IP_Port) {
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
            gameInit();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO: ERROR handling - wrong written ip fx

    }

    public static void gameInit() {
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
            //TODO: handle exception
        }
        try {
            int playerCount = -1;
            while(running){
                //event getCard
                //listen for command
                Object[] commands = _gameSpace.query(new ActualField(CommandType.class));
                CommandType cmd = (CommandType) commands[0];
                if (playerCount == -1) {
                    playerCount = (int) _userSpace.query(new ActualField("lock"), new FormalField(Integer.class))[1];
                }
                switch (cmd) {
                    case Election:
                        election(playerCount);
                        break;
                    case LegislativeSession:
                        System.out.println("L_session has happened");
                    case ExecutiveAction:
                        System.out.println("Executive action has happened");
                    default:
                        break;
                }
                _gameSpace.put(new ActualField("lock"));
            }
        } catch (Exception e) {}
    }

    private static void election(int playerCount) throws InterruptedException {
        // Init election, and suggest chancellor if player is president.
        Boolean electionDone;
        Object[] newElect = _gameSpace.query(new ActualField("suggest"), new FormalField(Integer.class), new FormalField(ArrayList.class));
        int pres = (int) newElect[1];
        ArrayList<Integer> eligibleCands = (ArrayList<Integer>) newElect[2];
        int suggestion = -1;
        if (_user.Id() == pres) {
            suggestion = Menu.suggest(eligibleCands);
            _gameSpace.get(new ActualField("lock"));
            _gameSpace.put("suggestion", suggestion);
            _gameSpace.put("lock");
        } else {
            suggestion = (int) _gameSpace.query(new ActualField("suggestion"), new FormalField(Integer.class))[1];
        }

        // Vote in GUI
        Boolean Boolvote = Menu.vote(suggestion);
        VoteType vote;
        if (Boolvote) {
            vote = VoteType.Ja;
        } else {
            vote = VoteType.Nein;
        }
        // Put vote in the gameSpace tuple space.
        // Also update GUI with incoming votes until vote is complete.
        _gameSpace.query(new ActualField("startVote"));
        _gameSpace.get(new ActualField("lock"));
        // BIG CHANCE OF ERROR HERE! What the fuck is an Array.class?
        Object[] voteObj = _gameSpace.get(new ActualField("votes"), new FormalField(Array.class), new FormalField(Integer.class));
        VoteType[] votes = (VoteType[]) voteObj[1];
        int voterId = (int) voteObj[2] + 1;
        votes[_user.Id()] = vote;
        _gameSpace.put("votes", votes, voterId);
        _gameSpace.put("lock");
        Menu.updateVotes(votes);
        int deadPlayers = ((ArrayList<?>) _gameSpace.query(new ActualField("deadPlayers"), new FormalField(ArrayList.class))[1]).size();
        electionDone = (voterId == (playerCount - deadPlayers - 1));
        while(!electionDone) {
            voteObj = _gameSpace.query(new ActualField("votes"), new FormalField(Array.class), new FormalField(Integer.class));
            votes = (VoteType[]) voteObj[1];
            Menu.updateVotes(votes);
            electionDone = ((int) voteObj[2] == (playerCount - deadPlayers - 1));
        }
    }

    public void sendMessage(String msg, ChatHandler chatHandler) {
        try {
            Object[] newChat = _chatSpace.get(new ActualField("lock"), new FormalField(Integer.class));
            int newChatId = (int)newChat[1];
            chatHandler.incChatId();
            _chatSpace.put(_user.Name(), msg, newChatId);
            System.out.println(_user.Name() + ": " + msg);
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

    public void leaveGame() throws InterruptedException {
        _userSpace.put("leave", _user.Name(), _user.Id());
    }

    public void setUser(String name) {
        _user = new User(name);
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

}
