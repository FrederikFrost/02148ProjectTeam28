package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class SecretHitler {

    public static void main(String[] args) {
		try {
			
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            // Read user name from the console			
			System.out.print("Enter your name: ");
			String name = input.readLine();

            System.out.print("Do you wish to create a game, or join existing game? [.create/.join]: ");
            String cmd = input.readLine();
            Boolean started = false;

            while(!started) {
                switch(cmd) {
                    case ".create":
                        started = true;
                        gameCreate(input, name);
                        break;
                    case ".join":
                        started = true;
                        gameJoin(input, name);
                        break;
                    default:
                        System.out.println("Do you wish to create a game, or join existing game? [.create/.join]: ");
                        break;
                }
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void gameCreate(BufferedReader in, String name) {
        String userName = name;
        try {
            // Create a repository 
			SpaceRepository repository = new SpaceRepository();

			// Create a local space for the chat messages
            SequentialSpace chat = new SequentialSpace();
            SequentialSpace users = new SequentialSpace();

			// Add the space to the repository
            repository.add("chat", chat);
            repository.add("users", users);
			
			// Set the URI of the chat space
			System.out.print("Enter IP and port of the game server or press enter for default [IP:port]: ");
            String IP_Port = in.readLine();
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

            chat.put("lock", 0);
            users.put("lock", 0);

            chatController(chat, users, in, userName);
        } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void gameJoin(BufferedReader in, String name) {
        String userName = name;
        try {
			// Set the URI of the chat space
			// Default value
			// Set the URI of the chat space
			System.out.print("Enter IP and port of the game server or press enter for default [IP:port]: ");
            String IP_Port = in.readLine();
            String protocol = "tcp://";
            String chatSpace = "/chat?keep";
            String userSpace = "/users?keep";
            // Default value
            // localhost: "tcp://127.0.0.1:9001/?keep"
            // router extern port forwarded IP: "tcp://212.237.106.43:9001/chat?keep"
			if (IP_Port.isEmpty()) { 
                IP_Port = "212.237.106.43:9001";
            }
            
            String chatURI = protocol + IP_Port + chatSpace;
            String userURI = protocol + IP_Port + userSpace;

			// Connect to the remote chat space 
			System.out.println("Connecting to chat space " + chatURI + "...");
            RemoteSpace chat = new RemoteSpace(chatURI);
            RemoteSpace users = new RemoteSpace(userURI);
            chatController(chat, users, in, userName);		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    public static void chatController(Space chat, Space users, BufferedReader in, String name) {
        String userName = name;
        // Keep sending whatever the user types
        int chatId = 0;
        int userId = 0;
        String message;
        System.out.println("Start chatting...");
        try {
            Object[] user = users.get(new ActualField("lock"), new FormalField(Integer.class));
            Object[] initId = chat.query(new ActualField("lock"), new FormalField(Integer.class));
            chatId = (int)initId[1];
            userId = (int)user[1];
            users.put("join", userName, userId);
            userId++;
            users.put("lock", userId);
        } catch (Exception e) {
            //TODO: handle exception
        }
        try {
		    while(true) {
                Object[] newUser = users.queryp(new ActualField("join"), new FormalField(String.class), new ActualField(userId));
                Object[] newChat = chat.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField(chatId));
                if (newChat != null) {
                    System.out.println(newChat[0] + ": " + newChat[1] + ": " + newChat[2]);
                    chatId++;
                }
                if (newUser != null) {
                    System.out.println(newUser[1] + " has joined the game!");
                    userId++;
                }
                if (in.ready() && (message = in.readLine()) != null) {
                    newChat = chat.get(new ActualField("lock"), new FormalField(Integer.class));
                    chatId = (int)newChat[1];
                    chat.put(userName, message, chatId);
                    System.out.println(userName + ": " + message);
                    chatId++;
                    chat.put("lock", chatId);
                }
            } 
        } catch (Exception e) {
        //TODO: handle exception
        }
    }
    
}
