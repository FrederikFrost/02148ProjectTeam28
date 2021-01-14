package common.src.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Menu {
    static String guiPath = "gui/";
    static String appName;
    static String tcp;
    static JFrame frame = new JFrame("Secret Hitler");
    static JFrame newFrame = new JFrame();
    static JPanel mainPanel = new JPanel();
    static JLabel[] labels = new JLabel[3];
    static JButton[] buttons = new JButton[3];
    static JButton sendMessage;
    static JTextField messageBox;
    static JEditorPane chatBox;
    static JScrollPane scrollPane;
    static JScrollBar scrollBar;
    static JTextField usernameChooser;
    static JFrame preFrame;
    static String username;
    public static SecretHitlerV2 game;
    public static ChatHandler chatHandler;

    public static void main(String[] args) {
        game = new SecretHitlerV2();
        try {
            menu();
        } catch (Exception e) {
            // TODO: handle exception
            try {
                game.leaveGame();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(2);
            }
            System.exit(1);
        }   
    }

    public static void menu() throws IOException {

        // Labels
        createLabel(0, "SecretLogo.png");
        createLabel(1, "label_secret-hitler.png");
        createLabel(2, "label_group-name.png");

        // Buttons
        JButton createGameButton = createButton(0, "button_create.png", "button_create_hover.png");
        JButton joinGameButton = createButton(1, "button_join.png", "button_join_hover.png");
        JButton exitButton = createButton(2, "button_exit.png", "button_exit_hover.png");

        // Panel settings
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (JLabel l : labels)
            mainPanel.add(l);
        mainPanel.add(Box.createRigidArea(new Dimension(30, 30)));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        for (JButton b : buttons) {
            mainPanel.add(b);
            mainPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        }

        // Frame
        frame.setBackground(Color.WHITE);
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Action Listeners
        createGameButton.addActionListener(createGameAction);
        joinGameButton.addActionListener(joinGameAction);
        exitButton.addActionListener(exitAction);
    }

    // Method for creating buttons
    public static JButton createButton(int index, String path, String hoverPath) throws IOException {
        JButton b = new JButton(new ImageIcon(ImageIO.read(Menu.class.getResource(guiPath + path))));
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setContentAreaFilled(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setRolloverIcon(new ImageIcon(ImageIO.read(Menu.class.getResource(guiPath + hoverPath))));
        buttons[index] = b;
        return b;
    }

    // Method for creating labels
    public static void createLabel(int index, String path) throws IOException {
        JLabel l = new JLabel(new ImageIcon(ImageIO.read(Menu.class.getResource(guiPath + path))));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        labels[index] = l;
    }

    // static class JTextFieldLimit extends PlainDocument {
    // private static final long serialVersionUID = 1L;
    // private int limit;
    // JTextFieldLimit(int limit) {
    // super();
    // this.limit = limit;
    // }

    // public void insertString(int offset, String str, AttributeSet attr) throws
    // BadLocationException {
    // if (str == null)
    // return;

    // if ((getLength() + str.length()) <= limit) {
    // super.insertString(offset, str, attr);
    // }
    // }
    // }

    // public static void createGameFrame(){
    // JFrame gFrame = new JFrame("Secret Hitler");
    // JPanel gPanel = new JPanel();
    // JTextArea chat = new JTextArea(20,20);
    // JTextField textField = new JTextField("Start chatting", 6);
    // JButton startButton = new JButton("Start game");
    // textField.setDocument(new JTextFieldLimit(40));
    // gPanel.add(chat);
    // gPanel.add(textField);
    // gPanel.add(startButton);
    // gFrame.add(gPanel);
    // gFrame.setLocationRelativeTo(null);
    // gFrame.pack();
    // gFrame.setVisible(true);
    // }

    // Chatdisplay appears after creating/joining game
    public static JPanel chatPanel() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();
        messageBox.addActionListener(new sendMessageListener());

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(new sendMessageListener());

        chatBox = new JEditorPane("text/rtf", "");
        chatBox.setEditable(false);
        chatBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        scrollPane = new JScrollPane(chatBox);
        scrollBar = scrollPane.getVerticalScrollBar();
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.insets = new Insets(0, 10, 0, 0);
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
        chatPanel.add(BorderLayout.SOUTH, southPanel);
        chatPanel.setSize(400,760);
        return chatPanel;
    }

    public static JPanel gamePanel(){
         JPanel gamePanel = new JPanel();
        //  JLabel gameLabel = new JLabel("SECRET HITLER");
        //  gameLabel.setFont(new Font("Calibri", Font.PLAIN, 45));
         gamePanel.setLayout(new GridBagLayout());
        //  gamePanel.add(gameLabel);
         gamePanel.setSize(1200,800);

         // ADD GAMEPLAY GUI HERE
         return gamePanel;
    }

    public static void gameFrame(){
        JPanel chatPanel = chatPanel();
        JPanel gamePanel = gamePanel();
        newFrame.add(chatPanel);
        newFrame.setLayout(new BorderLayout());
        newFrame.add(gamePanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(chatPanel.getWidth() + gamePanel.getWidth(), 800);
        newFrame.setLocationRelativeTo(null);
        newFrame.setVisible(true);
    }

    public static void append(JEditorPane jea, String s, Boolean b) {
        try {
            Document doc = jea.getDocument();
            if (b) {
                doc.insertString(doc.getLength(), s, bold());
            } else
                doc.insertString(doc.getLength(), s, null);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    private static SimpleAttributeSet bold() {
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setBold(sas, true);
        return sas;
    }

    static class sendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
            }
            // do nothing
            else {
                String msg = messageBox.getText();

                newFrame.revalidate();
                scrollBar.setValue(scrollBar.getMaximum());

                if (msg.equals(".help")) {
                    append(chatBox,
                            "<ChatBot>: Chat commands will be listed:\n\n"
                                    + "\".clear\":   Clears the chat screen messages.\n"
                                    + "\".tcp\":   Lists the tcp address of the chat room.\n"
                                    + "\".leave\":   Allows for leaving chatroom.\n",
                            true);
                } else if (msg.equals(".clear")) {
                    chatBox.setText("");
                    append(chatBox, "<ChatBot>: The chat has been cleared!\n", true);
                } else if (msg.equals(".tcp")) {
                    append(chatBox, "<ChatBot>: This chat room's tcp is: " + tcp + "\n", true);
                } else if (msg.equals(".leave")) {
                    game.sendMessage("<ChatBot>: " + username + " has left!", chatHandler);
                    try {
                        game.leaveGame();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }
                    System.exit(1);
                } else if (msg.startsWith(".") && !msg.endsWith(".")) {
                    append(chatBox, "<ChatBot>: Use .help to retrieve list of commands\n", true);
                } else {
                    append(chatBox, "<" + username + ">:  " + msg + "\n", false);
                    game.sendMessage(msg, chatHandler);
                }
                messageBox.requestFocusInWindow();
                messageBox.setText("");
            }
        }
    }

    // AbstractActions
    public static AbstractAction createGameAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            // frame.setVisible(false);
            String name = JOptionPane.showInputDialog(frame, "Enter your name");
            if (name == null) {
                frame.setVisible(true);
                return;
            } else if (name.isEmpty() || name.equals("ChatBot")) {
                System.out.println("I go here");
                do {
                    name = JOptionPane.showInputDialog(frame, "Enter your name");
                } while (name.isEmpty() || name.equals("ChatBot"));
            }
            game.setUser(name);
            username = name;

            String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                } while (IP_Port.isEmpty());
            }
            frame.setVisible(false);
            tcp = IP_Port;
            newFrame.setTitle("Secret Hitler  |  " + name + "'s Room  |  tcp: " + tcp);
            game.gameCreate(IP_Port);
            gameFrame();
            chatHandler = new ChatHandler(game.getUserSpace(), game.getChatSpace(), game.getChatId(),
                    game.getUser().Id(), chatBox);
            new Thread(chatHandler).start();
            System.out.println("Created Game");
        }
    };

    public static AbstractAction joinGameAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            // frame.setVisible(false);
            String name = JOptionPane.showInputDialog(frame, "Enter your name");
            if (name == null) {
                frame.setVisible(true);
                return;
            } else if (name.isEmpty() || name.equals("ChatBot")) {
                System.out.println("I go here");
                do {
                    name = JOptionPane.showInputDialog(frame, "Enter your name");
                } while (name.isEmpty() || name.equals("ChatBot"));
            }
            game.setUser(name);
            username = name;

            String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                } while (IP_Port.isEmpty());
            }
            frame.setVisible(false);
            tcp = IP_Port;
            game.gameJoin(IP_Port);
            Space userSpace = game.getUserSpace();
            Object[] creator;
            try {
                creator = userSpace.query(new ActualField("join"), new FormalField(String.class), new ActualField(0));
                String roomName = creator[1].toString();
                newFrame.setTitle("Secret Hitler  |  " + roomName + "'s Room  |  tcp: " + tcp);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            gameFrame();
            chatHandler = new ChatHandler(game.getUserSpace(), game.getChatSpace(), game.getChatId(),
                    game.getUser().Id(), chatBox);
            new Thread(chatHandler).start();
            System.out.println("Joined Game");
        }
    };

    public static AbstractAction exitAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                game.leaveGame();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
                    System.exit(1);
                }
            };
            
    // public static void playSong(URL media) {
    //     Player mediaPlayer = Manager.createRealizedPlayer(media);
    //     mediaPlayer.start();
    // WORK IN PROGRESS
}
