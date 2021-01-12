package common.src.main;

import javax.swing.JTextArea;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class ChatHandler implements Runnable {
    private Space _userSpace;
    private Space _chatSpace;
    private int chatId;
    private int nextUserId;
    private JTextArea _chatBox;

    public ChatHandler(Space userSpace, Space chatSpace, int chatId, int nextUserId, JTextArea chatBox) {
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
                if (newChat != null) {
                    _chatBox.append("<" + newChat[0] + ">:  " + newChat[1] + "\n");
                    System.out.println(newChat[0] + ": " + newChat[1]);
                    chatId++;
                }
                if (newUser != null) {
                    _chatBox.append("<" + newUser[1] + ">: has joined the game!\n");
                    System.out.println(newUser[1] + " has joined the game!");
                    nextUserId++;
                }
             } 
         } catch (Exception e) {
         //TODO: make something
         }

    }

    public void incChatId() {
        this.chatId++;
    }
    
    
}
