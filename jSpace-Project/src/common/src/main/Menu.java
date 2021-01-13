package common.src.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

public class Menu {

    static String      appName     = "Secret Hitler Chat";
    static JFrame      newFrame    = new JFrame(appName);
    static JButton sendMessage;
    static JTextField  messageBox;
    static JTextArea   chatBox;
    static JTextField  usernameChooser;
    static JFrame      preFrame;
    static String      username;
    public static SecretHitlerV2 game;
    public static ChatHandler chatHandler;

    public static void display() {
        JFrame frame = new JFrame("Secret Hitler");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setSize(800, 600);
        int WIDTH = frame.getWidth();
        int HEIGHT = frame.getHeight();

        JButton createGame = new JButton("Create Game");
        JButton joinGame = new JButton("Join Game");
        JButton playMusic = new JButton("Play Music");

        frame.getContentPane().add(createGame, BorderLayout.SOUTH);
        frame.getContentPane().add(joinGame);
        frame.getContentPane().add(playMusic);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    // ACTION LISTENERS
        createGame.addActionListener(new AbstractAction(){  
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                frame.dispose();
                String name = JOptionPane.showInputDialog(frame, "What is your name?");
                game.setUser(name);
                username = name;
                String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                game.gameCreate(IP_Port);
                chatDisplay();
                chatHandler = new ChatHandler(game.getUserSpace(), game.getChatSpace(), game.getChatId(), game.getUser().Id(), chatBox);
                new Thread(chatHandler).start();
                System.out.println("Created Game");
            } 
            });  

        joinGame.addActionListener(new AbstractAction(){  
            public void actionPerformed(ActionEvent e){  
                frame.setVisible(false);
                frame.dispose();
                String name = JOptionPane.showInputDialog(frame, "What is your name?");
                game.setUser(name);
                username = name;
                String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                game.gameJoin(IP_Port);
                chatDisplay();
                chatHandler = new ChatHandler(game.getUserSpace(), game.getChatSpace(), game.getChatId(), game.getUser().Id(), chatBox);
                new Thread(chatHandler).start();
                System.out.println("Joined Game");
            }  
            });  

        playMusic.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println("Music is playing!");
                }
        });

        
		
    }

    static class JTextFieldLimit extends PlainDocument {
        private int limit;
        JTextFieldLimit(int limit) {
          super();
          this.limit = limit;
        }
      
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
          if (str == null)
            return;
      
          if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
          }
        }
      }

      public static void createGameFrame(){
        JFrame gFrame = new JFrame("Secret Hitler");
        JPanel gPanel = new JPanel();
        JTextArea chat = new JTextArea(20,20);
        JTextField textField = new JTextField("Start chatting", 6);
        JButton startButton = new JButton("Start game");
        textField.setDocument(new JTextFieldLimit(40));
        gPanel.add(chat);
        gPanel.add(textField);
        gPanel.add(startButton);
        gFrame.add(gPanel);
        gFrame.setLocationRelativeTo(null);
        gFrame.pack();
        gFrame.setVisible(true);
    }

    public static void chatDisplay() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.BLUE);
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();
        messageBox.addActionListener(new sendMessageListener());

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(new sendMessageListener());

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setLocationRelativeTo(null);
        newFrame.setSize(470, 300);
        newFrame.setVisible(true);
    }

    static class sendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else if (messageBox.getText().equals(".clear")) {
                chatBox.setText("Cleared all messages\n");
                messageBox.setText("");
            } else {
                String msg = messageBox.getText();
                game.sendMessage(msg, chatHandler);
                chatBox.append("<" + username + ">:  " + msg + "\n");
                messageBox.setText("");
            }
            messageBox.requestFocusInWindow();
        }
    }

	public static void main(String[] args) {
        game = new SecretHitlerV2();
        display();
        
    }
    /*
    public static void playSong(URL media) {
        Player mediaPlayer = Manager.createRealizedPlayer(media);
        mediaPlayer.start();
    WORK IN PROGRESS
    */
    }
