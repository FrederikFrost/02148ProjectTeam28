package common.src.main;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class SecretHitler {

    public static void main(String[] args) {
		try {
			
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            Console cnsl = System.console();

            System.out.print("Do you wish to create a game, or join existing game? [.create/.join]: ");
            String cmd = input.readLine();

            switch(cmd) {
                case ".create":
                    gameCreate(input, cnsl);

                    break;
                case ".join":
                    gameJoin(input, cnsl);

                    break;
            }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void gameCreate(BufferedReader in, Console console) {
        try {
            // Create a repository 
			SpaceRepository repository = new SpaceRepository();

			// Create a local space for the chat messages
			SequentialSpace chat = new SequentialSpace();

			// Add the space to the repository
			repository.add("chat", chat);
			
			// Set the URI of the chat space
			System.out.print("Enter URI of the chat server or press enter for default: ");
			String uri = in.readLine();
            // Default value
            // stationær intern ip: "tcp://192.168.68.112:9001/chat?keep"
            // localhost: "tcp://127.0.0.1:9001/?keep"
			if (uri.isEmpty()) { 
				uri = "tcp://192.168.68.112:9001/chat?keep";
			}

			// Open a gate
			URI myUri = new URI(uri);
			String gateUri = "tcp://" + myUri.getHost() + ":" + myUri.getPort() +  "?keep" ;
			System.out.println("Opening repository gate at " + gateUri + "...");
            repository.addGate(gateUri);
            System.out.println("Gate added");

            // Read user name from the console			
			System.out.print("Enter your name: ");
			String name = in.readLine();

            // Keep sending whatever the user types
            int i = 0;
            String message;
            System.out.println("Start chatting...");
            chat.put("lock", i);
			while(true) {
                try {
                    console.flush();
                    Object[] t = chat.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField(i));
                    if (t != null) {
                        System.out.println(t[0] + ": " + t[1] + ": " + t[2]);
                        i++;
                    }
                    
                    if (in.ready() && (message = in.readLine()) != null) {
                        t = chat.get(new ActualField("lock"), new FormalField(Integer.class));
                        i = (int)t[1];
                        chat.put(name, message, i);
                        System.out.println(name + ": " + message + ": " + i);
                        i++;
                        chat.put("lock", i);
                    }
                    
                } catch (Exception e) {
                    //TODO: handle exception
                }
			}
        } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void gameJoin(BufferedReader in, Console console) {
        try {
			// Set the URI of the chat space
			// Default value
			System.out.print("Enter URI of the chat server or press enter for default: ");
			String uri = in.readLine();
            // Default value
            // localhost: "tcp://127.0.0.1:9001/?keep"
            // router extern port forwarded IP: "tcp://212.237.106.43:9001/chat?keep"
			if (uri.isEmpty()) { 
				uri = "tcp://212.237.106.43:9001/chat?keep";
			}

			// Connect to the remote chat space 
			System.out.println("Connecting to chat space " + uri + "...");
			RemoteSpace chat = new RemoteSpace(uri);

			// Read user name from the console			
			System.out.print("Enter your name: ");
			String name = in.readLine();

            // Keep sending whatever the user types
            int i = 0;
            String message;
            System.out.println("Start chatting...");
            try {
                Object[] initId = chat.get(new ActualField("lock"), new FormalField(Integer.class));
                i = (int)initId[1];
                chat.put(name, name + " has joined the chat", i);
                i++;
            } catch (Exception e) {
                //TODO: handle exception
            }
			while(true) {
                try {
                    //console.flush();
                    Object[] t = chat.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField(i));
                    if (t != null) {
                        System.out.println(t[0] + ": " + t[1] + ": " + t[2]);
                        i++;
                    }

                    if (in.ready() && (message = in.readLine()) != null) {
                        t = chat.get(new ActualField("lock"), new FormalField(Integer.class));
                        i = (int)t[1];
                        chat.put(name, message, i);
                        System.out.println(name + ": " + message + ": " + i);
                        i++;
                        chat.put("lock", i);
                    }
                    
                } catch (Exception e) {
                    //TODO: handle exception
                }
			}			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
}
