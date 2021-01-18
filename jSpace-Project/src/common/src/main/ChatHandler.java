package common.src.main;

import javax.swing.JEditorPane;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class ChatHandler implements Runnable {
    private Space _userSpace;
    private Space _chatSpace;
    private int chatId;
    private int nextUserId;
    private JEditorPane _chatBox;
    
    public ChatHandler(Space userSpace, Space chatSpace, int chatId, int nextUserId, JEditorPane chatBox) {
        this._userSpace = userSpace;
        this._chatSpace = chatSpace;
        this.chatId = chatId;
        this.nextUserId = nextUserId;
        this._chatBox = chatBox;
    }
    
    @Override
    public void run() {
         try {
             while(true) {
                //handle chat
                Object[] newUser = _userSpace.queryp(new ActualField("join"), new FormalField(String.class), new ActualField(nextUserId));
                Object[] newChat = _chatSpace.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField(chatId));
                //Object[] leftUser = _userSpace.queryp(new ActualField("leave"), new FormalField(String.class), new FormalField(Integer.class));
                if (newChat != null) {
                    MenuComponents.append(_chatBox, "<" + newChat[0] + ">:  " + newChat[1] + "\n", false);
                    System.out.println(newChat[0] + ": " + newChat[1]);
                    chatId++;
                }
                if (newUser != null) {
                    MenuComponents.append(_chatBox,"<" + newUser[1] + ">: has joined the game!\n", true);
                    MenuComponents.incNumPlayers();
                    System.out.println(newUser[1] + " has joined the game!");
                    nextUserId++;
                }
                /*
                if (leftUser != null) {
                    MenuComponents.append(_chatBox,"<" + leftUser[1] + ">: has left the game!\n", true);
                    MenuComponents.decNumPlayers();
                } */
             } 
         } catch (Exception e) {
         //TODO: make something
         e.printStackTrace();
         }

    }

    public void incChatId() {
        this.chatId++;
    }
    
    
}
