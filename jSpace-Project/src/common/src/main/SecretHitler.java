package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class SecretHitler {

    public static Space _chatSpace;
    public static Space _userSpace;
    public static Space _gameSpace;
    public static BufferedReader _reader;

    public static User _user;
    public static int nextUserId;
    public static int chatId = 0;

    public static void main(String[] args) {
		try {
			
            _reader = new BufferedReader(new InputStreamReader(System.in));

            // Read user name from the console			
			System.out.print("Enter your name: ");
            String name = _reader.readLine();
            _user = new User(name);

            System.out.print("Do you wish to create a game, or join existing game? [.create/.join]: ");
            Boolean started = false;

            while(!started) {
                String cmd = _reader.readLine();
                switch(cmd) {
                    case ".create":
                        started = true;
                        gameCreate();
                        break;
                    case ".join":
                        started = true;
                        gameJoin();
                        break;
                    default:
                        System.out.print("Do you wish to create a game, or join existing game? [.create/.join]: ");
                        break;
                }
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void gameCreate() {
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
			System.out.print("Enter IP and port of the game server or press enter for default [IP:port]: ");
            String IP_Port = _reader.readLine();
            String protocol = "tcp://";

            // Default value
            // stationær intern ip: "tcp://192.168.68.112:9001/chat?keep"
            // localhost: "tcp://127.0.0.1:9001/?keep"
			if (IP_Port.isEmpty()) { 
                IP_Port = "192.168.68.112:9001";
            }

			// Open a gate
			String gateURI = protocol + IP_Port + "/?keep";
			System.out.println("Opening repository gate at " + gateURI + "...");
            repository.addGate(gateURI);
            System.out.println("Gate added");

            _chatSpace.put("lock", 0);
            _userSpace.put("lock", 0);
            _gameSpace.put("lock", 0);


            //create and start game coordinator
            GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
            new Thread(controller).start();

            gameInit();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void gameJoin() {
        try {
			// Set the URI of the chat space
			// Default value
			// Set the URI of the chat space
			System.out.print("Enter IP and port of the game server or press enter for default [IP:port]: ");
            String IP_Port = _reader.readLine();
            String protocol = "tcp://";
            String chatSpace = "/chat?keep";
            String userSpace = "/users?keep";
            String gameSpace = "/game?keep";
            // Default value
            // localhost: "tcp://127.0.0.1:9001/?keep"
            // router extern port forwarded IP: "tcp://212.237.106.43:9001/chat?keep"
			if (IP_Port.isEmpty()) { 
                IP_Port = "212.237.106.43:9001";
            }
            
            String chatURI = protocol + IP_Port + chatSpace;
            String userURI = protocol + IP_Port + userSpace;
            String gameURI = protocol + IP_Port + gameSpace;

			// Connect to the remote chat space 
			System.out.println("Connecting to chat space " + chatURI + "...");
            _chatSpace =  new RemoteSpace(chatURI);
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
        //TODO: ERROR handling - wrong written ip fx

    }

    public static void gameInit() {
        // Keep sending whatever the user types
        System.out.println("Start chatting...");
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
		    while(true) {

                //event getCard


                //handle chat
                chatHandler();

            } 
        } catch (Exception e) {
        //TODO: handle exception
        }
    }
    
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
    }
}
