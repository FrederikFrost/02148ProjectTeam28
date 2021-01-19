package common.src.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import common.src.main.Types.ErrorType;
import common.src.main.Types.LegislativeType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class MenuComponents {
    static String appName;
    static String localtcp = "192.168.0.101";
    static String port = "9001";
    static String tcp;
    static String username;
    static int numOfPlayers = 0;
    static boolean gameHost = false;
    static JFrame frame = new JFrame("Secret Hitler");
    static JFrame newFrame = new JFrame();
    static JPanel gamePanel = new JPanel();
    static JPanel menuPanel = new JPanel();
    static JPanel mainPanel;
    static JLabel[] labels = new JLabel[3];
    static JLabel numPlayerLabel;
    static JButton[] buttons = new JButton[10];
    static JButton startGameButton;
    static JButton sendMessage;
    static JTextField messageBox;
    static JEditorPane chatBox;
    static JScrollPane scrollPane;
    static JScrollBar scrollBar;
    static boolean submitted;

    static ArrayList<LegislativeType> legiChoices;
    static ImageIcon fascistCard;
    static ImageIcon fascistCardSelected;
    static ImageIcon liberalCard;
    static ImageIcon liberalCardSelected;
    static ImageIcon fascistBoardImage;
    static ImageIcon liberalBoardImage;
    static Board fascistBoard;
    static Board liberalBoard;

    public static void initializeCards() throws IOException {
        fascistCard = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist-card.png")));
        liberalCard = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal-card.png")));
        fascistCardSelected = new ImageIcon(
                ImageIO.read(Menu.class.getResource("gui/gamecards/fascist-card-selected.png")));
        liberalCardSelected = new ImageIcon(
                ImageIO.read(Menu.class.getResource("gui/gamecards/liberal-card-selected.png")));
        fascistBoardImage = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist-board.png")));
        liberalBoardImage = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal-board.png")));
        MenuComponents mc = new MenuComponents();
        fascistBoard = mc.new Board(fascistBoardImage);
        liberalBoard = mc.new Board(liberalBoardImage);
    }

    public static void menu() throws IOException {

        // Labels
        createLabel(0, "SecretLogo.png");
        createLabel(1, "label_secret-hitler.png");
        createLabel(2, "label_group-name.png");

        // Buttons
        JButton createGameButton = createButton(0, "button_create.png", "button_create_hover.png", null);
        JButton joinGameButton = createButton(1, "button_join.png", "button_join_hover.png", null);
        JButton exitButton = createButton(2, "button_exit.png", "button_exit_hover.png", null);

        // Panel settings
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (JLabel l : labels)
            menuPanel.add(l);
        menuPanel.add(Box.createRigidArea(new Dimension(30, 30)));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        for (int i = 0 ; i < 3 ; i++){
            menuPanel.add(buttons[i]);
            menuPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        }
        startGameButton = createButton(3, "button_start-game.png", "button_start-game_hover.png", null);
        // Frame
        frame.setBackground(Color.WHITE);
        frame.add(menuPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Action Listeners
        createGameButton.addActionListener(createGameAction);
        joinGameButton.addActionListener(joinGameAction);
        exitButton.addActionListener(exitAction);
        startGameButton.addActionListener(startGameAction);
    }

    // Method for creating buttons
    public static JButton createButton(int index, String path, String hoverPath, ImageIcon io) throws IOException {
        JButton b;
        if (path!=null) b = new JButton(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/buttons/" + path))));
        else b = new JButton(io);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setContentAreaFilled(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (hoverPath != null) b.setRolloverIcon(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/buttons/" + hoverPath))));
        if (index != -1) buttons[index] = b;
        return b;
    }

    // Method for creating labels
    public static void createLabel(int index, String path) throws IOException {
        JLabel l = new JLabel(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/labels/" + path))));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        labels[index] = l;
    }

    public static void addNumOfPlayers() {
        numPlayerLabel = new JLabel("Number of players: " + numOfPlayers);
        numPlayerLabel.setHorizontalAlignment(JLabel.RIGHT);
        numPlayerLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        mainPanel.add(numPlayerLabel, BorderLayout.SOUTH);
    }

    public static void incNumPlayers() {
        numOfPlayers++;
        numPlayerLabel.setText("Number of players: " + numOfPlayers);
        if (numOfPlayers >= 5 && gameHost) {
            System.out.println("Enough players to start!");
            GridBagConstraints con = new GridBagConstraints();
            con.anchor = GridBagConstraints.CENTER;
            //con.fill = GridBagConstraints.HORIZONTAL;
            con.gridwidth = 3;
            con.gridheight = 0;
            con.weightx = 0;
            con.gridx = 0;
            con.gridy = 0;
            gamePanel.add(startGameButton, con);
            gamePanel.revalidate();
            //gamePanel.repaint();
        }
    }

    public static void decNumPlayers() {
        numOfPlayers--;
        numPlayerLabel.setText("Number of players: " + numOfPlayers);
        if (numOfPlayers == 4 && gameHost) {
            gamePanel.remove(startGameButton);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public static ArrayList<LegislativeType> chooseCards (LegislativeType ... cards) throws IOException {
        submitted = false;
        legiChoices = new ArrayList<LegislativeType>();
        JPanel choicePanel = new JPanel();
        choicePanel.setSize(300,400);
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));
        JFrame choiceFrame = new JFrame();
        choiceFrame.setLayout(new BoxLayout(choiceFrame.getContentPane(), BoxLayout.Y_AXIS));
        if (cards.length == 3) choiceFrame.setTitle("Choose 2 out of 3 article cards");
        else choiceFrame.setTitle("Choose 1 out of 2 article cards");
        System.out.println(cards.length);
        for (LegislativeType card : cards){
            ImageIcon io, ios;
            if (card == LegislativeType.Fascist) {
                io = fascistCard;
                ios = fascistCardSelected;
            }
            else  {
                io = liberalCard;
                ios = liberalCardSelected;
            }
            JButton cardChoice = createButton(-1, null, null, io);
            choicePanel.add(cardChoice);
            choicePanel.add(Box.createRigidArea(new Dimension(10, 10)));
            cardChoice.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    boolean isSelected = cardChoice.getIcon().equals(ios);
                    boolean valid = false;
                    if (legiChoices.size() < cards.length-1 && !isSelected ||
                        legiChoices.size() >= 1 && isSelected) {
                        cardChoice.setIcon(isSelected ? io : ios);
                        cardChoice.invalidate(); // might not be needed
                        cardChoice.repaint();
                        isSelected = !isSelected;
                        valid = true;
                    }
                    if (valid){
                        if (isSelected) {
                            if (cardChoice.getIcon().equals(fascistCard) || 
                                cardChoice.getIcon().equals(fascistCardSelected))
                                    legiChoices.add(LegislativeType.Fascist);
                            else legiChoices.add(LegislativeType.Liberal);
                        }
                        else {
                            if (cardChoice.getIcon().equals(fascistCard) || 
                            cardChoice.getIcon().equals(fascistCardSelected))
                                legiChoices.remove(LegislativeType.Fascist);
                            else legiChoices.remove(LegislativeType.Liberal);
                        }
                    System.out.println("Legichoices size = " +legiChoices.size());
                    }
            }});
        }
        JButton submitButton = new JButton("Submit article choices!");
        submitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (legiChoices.size() == cards.length-1) {
                    for (LegislativeType choice : legiChoices) System.out.println(choice);
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    submitted = true;
                }
                else System.out.println("You haven't picked the right amount of article cards!");
            }});
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        choiceFrame.add(choicePanel);
        choiceFrame.add(Box.createRigidArea(new Dimension(20, 20)));
        choiceFrame.add(submitButton, BorderLayout.CENTER);
        choiceFrame.add(Box.createRigidArea(new Dimension(10, 10)));
        choiceFrame.pack();
        choiceFrame.setLocationRelativeTo(null);
        choiceFrame.setVisible(true);
        while (!submitted) {}
        return legiChoices;
    }
    public class Board extends JPanel {
        public final Image image;
        private static final long serialVersionUID = 1L;

        public Board(ImageIcon io){
            this.image = io.getImage();
            this.setVisible(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(this.image, 0, 0, null);
            }
        }

    public static void drawBoards(){
        gamePanel.add(fascistBoard);
        gamePanel.add(liberalBoard);
        gamePanel.revalidate();

    }

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
        chatPanel.setSize(400, 760);
        return chatPanel;
    }

    public static JPanel gamePanel() {
        JPanel gamePanel = new JPanel(new GridBagLayout());
        gamePanel.setLocation(400, 0);
        gamePanel.setSize(1200, 800);

        // ADD GAMEPLAY GUI HERE
        return gamePanel;
    }

    public static void gameFrame() throws IOException {
        initializeCards();
        mainPanel = new JPanel(new BorderLayout());
        JPanel chatPanel = chatPanel();
        gamePanel = gamePanel();
        drawBoards();
        mainPanel.setSize(chatPanel.getWidth() + gamePanel.getWidth(), 800);
        System.out.println("Chat panel width is: " + chatPanel.getWidth());
        System.out.println("Game panel width is: " + gamePanel.getWidth());
        mainPanel.add(chatPanel);
        //mainPanel.setLayout(new BorderLayout());
        mainPanel.add(gamePanel);
        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(mainPanel.getSize());
        //newFrame.setSize(chatPanel.getWidth() + gamePanel.getWidth(), 800);
        newFrame.setLocationRelativeTo(null);
        newFrame.setVisible(true);
        addNumOfPlayers();
<<<<<<< HEAD
        //chooseCards(LegislativeType.Fascist,LegislativeType.Liberal, LegislativeType.Fascist);
=======
        // chooseCards(LegislativeType.Fascist,LegislativeType.Liberal);
>>>>>>> c8e0b001ddcc2f4d57aa8a74fe13debd10a4a269
        // welcomeDialogue();
    }

    static class sendMessageListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
            }
            // do nothing
            else {
                String msg = messageBox.getText();

                mainPanel.revalidate();
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
                    try {
                        Menu.game.leaveGame();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        throw new RuntimeException(e);
                    }
                    System.exit(1);
                } else if (msg.startsWith(".") && !msg.endsWith(".")) {
                    append(chatBox, "<ChatBot>: Use .help to retrieve list of commands\n", true);
                } else {
                    append(chatBox, "<" + username + ">:  " + msg + "\n", false);
                    Menu.game.sendMessage(msg, Menu.chatHandler, false);
                }
                messageBox.requestFocusInWindow();
                messageBox.setText("");
            }
        }
    }

    public static void append(JEditorPane jea, String s, Boolean b) {
        try {
            Document doc = jea.getDocument();
            if (b)
                doc.insertString(doc.getLength(), s, bold());
            else
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

    public static void welcomeDialogue() {
        JOptionPane.showMessageDialog(null,
                "Welcome to Secret Hitler! The game will commence " + "as soon as a minimum of 5 players have joined.",
                "Welcome!", 1);
    }

    public static int exitDialogue(String errorMessage) {
        return JOptionPane.showOptionDialog(frame, errorMessage, "Error", 0, 1, null, null, null);
    }

    public static String suggestDialogueBox(String[] choices) {
        String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
                "Who should be suggested chancellor?", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                choices, // Array of choices
                choices[1]); // Initial choice
        return input;
    }

    public static String voteDialogueBox(String sugChan) {
        String[] choices = { "Ja", "Nein" };
        String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
                "Should " + sugChan + " be elected chancellor?", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                choices, // Array of choices
                "Select vote");
        return input;
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
            Menu.game.setUser(name);
            username = name;

            String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
            //String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", localtcp+":"+port);
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                    //IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", localtcp+":"+port);
                } while (IP_Port.isEmpty());
            }
            frame.setVisible(false);
            tcp = IP_Port;
            gameHost = true;
            newFrame.setTitle("Secret Hitler  |  " + name + "'s Room  |  tcp: " + tcp);
            Menu.game.gameCreate(IP_Port);
            Menu.game.chatSetup();
            
            try {
                gameFrame();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Menu.chatHandler = new ChatHandler(Menu.game.getUserSpace(), Menu.game.getChatSpace(),
                    Menu.game.getChatId(), Menu.game.getUser().Id(), chatBox);
            new Thread(Menu.chatHandler).start();
            new Thread(Menu.game).start();
            System.out.println("Created Game");
        }
    };

    public static AbstractAction joinGameAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        public void actionPerformed(ActionEvent e) {
            // frame.setVisible(false);
            String hostName = "";
            String name = getNameInput("Enter your name:");
            if (name == null) {
                frame.setVisible(true);
                return;
            }
            Menu.game.setUser(name);

            String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "212.237.106.43:9001");
            //String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)",  localtcp+":"+port);
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "212.237.106.43:9001");
                    //IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)",  localtcp+":"+port);
                } while (IP_Port.isEmpty());
            }
            frame.setVisible(false);

            Menu.game.setIP_Port(IP_Port);
            Object[] joinObject;
            do {
                joinObject = Menu.game.gameJoin();
                if (joinObject[0].equals(ErrorType.NameTaken)) {
                    name = getNameInput("Name taken, input new one:");
                    Menu.game.setUser(name);
                    if (name == null) {
                        frame.setVisible(true);
                        return;
                    }
                } else if (joinObject[0].equals(ErrorType.GameFull)) {
                    int ok = exitDialogue("The game is full, try another IP.");
                    if (ok == -1) {
                        frame.setVisible(true);
                        return;
                    }

                } else if (joinObject[0].equals(ErrorType.GameStarted)) {
                    int ok = exitDialogue("The game has started, try another IP.");
                    if (ok == -1) {
                        frame.setVisible(true);
                        return;
                    }
                }
            } while (!joinObject[0].equals(ErrorType.NoError));
            username = name;
            tcp = IP_Port;
            numOfPlayers = (int) joinObject[1];
            hostName = (String) joinObject[2];

            Menu.game.chatSetup();

            newFrame.setTitle("Secret Hitler  |  " + hostName + "'s Room  |  tcp: " + tcp);
            try {
                gameFrame();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Menu.chatHandler = new ChatHandler(Menu.game.getUserSpace(), Menu.game.getChatSpace(),
                    Menu.game.getChatId(), Menu.game.getUser().Id(), chatBox);
            new Thread(Menu.chatHandler).start();
            new Thread(Menu.game).start();
            System.out.println("Joined Game");
        }
    };

    public static String getNameInput(String namePrompt) {
        String name = JOptionPane.showInputDialog(frame, namePrompt);
        if (name == null) {
            return null;
        } else if (name.isEmpty() || name.equals("ChatBot")) {
            do {
                name = JOptionPane.showInputDialog(frame, "Name cannot be empty:");
            } while (name.isEmpty() || name.equals("ChatBot"));
        }
        return name;
    }

    public static AbstractAction exitAction = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Menu.game.leaveGame();
                numOfPlayers--;
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
            System.exit(1);
        }
    };

    public static AbstractAction startGameAction = new AbstractAction() {

        /**
         *
         */
        private static final long serialVersionUID = 3963988367577770364L;

        public void actionPerformed(ActionEvent e) {
            try {
                Menu.game.getGameSpace().put("start", numOfPlayers);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            gamePanel.remove(startGameButton);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    };
    
    // public static void playSong(URL media) {
    //     Player mediaPlayer = Manager.createRealizedPlayer(media);
    //     mediaPlayer.start();
    // WORK IN PROGRESS
}
