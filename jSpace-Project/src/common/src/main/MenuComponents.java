package common.src.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import common.src.main.Types.ErrorType;
import common.src.main.Types.LegislativeType;
import common.src.main.Types.RoleType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class MenuComponents {
    static String appName;
    static String localtcp = "192.168.50.218";
    static String port = "9001";
    static String tcp;
    static String username;
    static int numOfPlayers = 0;
    static boolean gameHost = false;
    static JFrame frame = new JFrame("Secret Hitler");
    static JFrame gameFrame = new JFrame();
    static JPanel gamePanel = new JPanel();
    static JPanel menuPanel = new JPanel();
    static JPanel chatPanel;
    static JPanel IDPanel;
    static JPanel innerIDPanel;
    static JPanel startGamePanel;
    static JPanel mainPanel;
    static JLabel[] labels = new JLabel[3];
    static JLabel numPlayerLabel;
    static JLabel waiting;
    static JTextPane joinedPlayers;
    static JButton[] buttons = new JButton[10];
    static JButton startGameButton;
    static JButton sendMessage;
    static JTextField messageBox;
    static JEditorPane chatBox;
    static JScrollPane scrollPane;
    static JScrollBar scrollBar;

    static Component ra1, ra2;
    static Component ra3 = Box.createRigidArea(new Dimension(150,0));
    static Component ra4 = Box.createRigidArea(new Dimension(150,0));

    static ArrayList<LegislativeType> legiChoices;
    static ImageIcon fascistCard;
    static ImageIcon fascistCardSelected;

    static ImageIcon[][] fascistBoardImageMatrix = new ImageIcon[3][7];
    static ImageIcon[][] liberalBoardImageMatrix = new ImageIcon[6][5];

    static ImageIcon liberalCard;
    static ImageIcon liberalCardSelected;
    static ImageIcon fascistMembership;
    static ImageIcon liberalMembership;
    static ImageIcon fascistRole;
    static ImageIcon liberalRole;
    static ImageIcon hitlerRole;
    static ImageIcon deadLabelIcon;

    static ImageIcon jaIcon;
    static ImageIcon neinIcon;
    static ImageIcon jaIconSelected;
    static ImageIcon neinIconSelected;
    static String chosenVote;

    static JLabel fascistBoard = new JLabel();
    static JLabel liberalBoard = new JLabel();
    static JLabel membershipCard = new JLabel();
    static JLabel roleCard = new JLabel();

    static int fascistBoardIndex = 0;
    static int fascistArticles = 0;
    static int liberalArticles = 0;
    static int liberalFails = 0;

    public static void initGameCards() throws IOException {

        //Boards
        for (int i = 0 ; i < 3 ; i++) {
            for (int j = 0 ; j < 7 ; j++) {
                int index = (i*2) + 5;
                fascistBoardImageMatrix[i][j] = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/boards/" +
                    index + "to" + (index+1) + "/" + j + "-articles.png")));
            }
        }

        for (int i = 0 ; i < 6 ; i++) {
            for (int j = 0 ; j < 4 ; j++) {
                liberalBoardImageMatrix[i][j] = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal/boards/" +
                    i + " articles/"+j+"-fails.png")));
                if (i == 5) break;
            }
        }

        // Article cards
        fascistCard = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/article.png")));
        liberalCard = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal/article.png")));
        fascistCardSelected = new ImageIcon(
                ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/article-selected.png")));
        liberalCardSelected = new ImageIcon(
                ImageIO.read(Menu.class.getResource("gui/gamecards/liberal/article-selected.png")));

        // Membership cards
        fascistMembership = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/membership.png")));
        liberalMembership = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal/membership.png")));

        // Role cards
        fascistRole = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/role.png")));
        liberalRole = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/liberal/role.png")));
        hitlerRole = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/fascist/hitler-role.png")));

        // Vote cards
        jaIcon = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/votes/ja.png")));
        neinIcon = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/votes/nein.png")));
        jaIconSelected = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/votes/ja-selected.png")));
        neinIconSelected = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/gamecards/votes/nein-selected.png")));

        // Additional labels
        deadLabelIcon = new ImageIcon(ImageIO.read(Menu.class.getResource("gui/labels/deadlabel.png")));
    }

    public static void menu() throws IOException {

        // Labels
        createLabel(0, "secrethitlerlogo.png");
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
        for (int i = 0; i < 3; i++) {
            menuPanel.add(buttons[i]);
            menuPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        }
        startGameButton = createButton(3, "button_start-game.png", "button_start-game_hover.png", null);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
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
        if (path != null)
            b = new JButton(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/buttons/" + path))));
        else
            b = new JButton(io);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setContentAreaFilled(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (hoverPath != null)
            b.setRolloverIcon(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/buttons/" + hoverPath))));
        if (index != -1)
            buttons[index] = b;
        return b;
    }

    // Method for creating labels
    public static void createLabel(int index, String path) throws IOException {
        JLabel l = new JLabel(new ImageIcon(ImageIO.read(Menu.class.getResource("gui/labels/" + path))));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        labels[index] = l;
    }
    public static void addJoinedPlayer(String user){
        StyledDocument doc = joinedPlayers.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), "\n" + user, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void incNumPlayers() {
        numOfPlayers++;
        numPlayerLabel.setText("Number of players: " + numOfPlayers);
        if (numOfPlayers >= 5 && gameHost) {
            System.out.println("Enough players to start!");
            waiting.setText("Waiting for host to start game!");
            startGamePanel.add(startGameButton);
            gamePanel.revalidate();
        }
    }

    public static void decNumPlayers() {
        numOfPlayers--;
        numPlayerLabel.setText("Number of players: " + numOfPlayers);
        if (numOfPlayers == 4 && gameHost) {
            //innerIDPanel.remove(startGameButton);
            startGamePanel.remove(startGameButton);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public static void chooseCards(boolean veto, LegislativeType... cards) throws IOException {
        legiChoices = new ArrayList<LegislativeType>();
        JPanel choicePanel = new JPanel();
        choicePanel.setSize(300, 400);
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));
        JFrame choiceFrame = new JFrame();
        choiceFrame.setLayout(new BoxLayout(choiceFrame.getContentPane(), BoxLayout.Y_AXIS));
        choiceFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (cards.length == 3)
            choiceFrame.setTitle("Choose 2 out of 3 article cards");
        else
            choiceFrame.setTitle("Choose 1 out of 2 article cards");
        System.out.println(cards.length);
        for (LegislativeType card : cards) {
            ImageIcon io, ios;
            if (card == LegislativeType.Fascist) {
                io = fascistCard;
                ios = fascistCardSelected;
            } else {
                io = liberalCard;
                ios = liberalCardSelected;
            }
            JButton cardChoice = createButton(-1, null, null, io);
            choicePanel.add(cardChoice);
            choicePanel.add(Box.createRigidArea(new Dimension(10, 10)));
            cardChoice.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean isSelected = cardChoice.getIcon().equals(ios);
                    boolean valid = false;
                    if (legiChoices.size() < cards.length - 1 && !isSelected || legiChoices.size() >= 1 && isSelected) {
                        cardChoice.setIcon(isSelected ? io : ios);
                        cardChoice.invalidate(); // might not be needed
                        cardChoice.repaint();
                        isSelected = !isSelected;
                        valid = true;
                    }
                    if (valid) {
                        if (isSelected) {
                            if (cardChoice.getIcon().equals(fascistCard)
                                    || cardChoice.getIcon().equals(fascistCardSelected))
                                legiChoices.add(LegislativeType.Fascist);
                            else
                                legiChoices.add(LegislativeType.Liberal);
                        } else {
                            if (cardChoice.getIcon().equals(fascistCard)
                                    || cardChoice.getIcon().equals(fascistCardSelected))
                                legiChoices.remove(LegislativeType.Fascist);
                            else
                                legiChoices.remove(LegislativeType.Liberal);
                        }
                        System.out.println("Legichoices size = " + legiChoices.size());
                    }
                }
            });
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(300, 400);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton submitButton = new JButton("Submit article choices!");
        buttonPanel.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (legiChoices.size() == cards.length - 1) {
                    for (LegislativeType choice : legiChoices)
                    System.out.println(choice);
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        Menu.game.getGameSpace().put("legiChoices", legiChoices);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else
                System.out.println("You haven't picked the right amount of article cards!");
            }
        });
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        if(veto) {
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            JButton vetoButton = new JButton("I wish to call Veto!");
            buttonPanel.add(vetoButton);
            vetoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                    SwingUtilities.getWindowAncestor(vetoButton).setVisible(false);
                    try {
                        ArrayList<Integer> vetoCards = new ArrayList<Integer>();
                        Menu.game.getGameSpace().put("legiChoices", vetoCards);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
            vetoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        choiceFrame.add(choicePanel);
        choiceFrame.add(buttonPanel);
        // choiceFrame.add(Box.createRigidArea(new Dimension(20, 20)));
        // choiceFrame.add(Box.createRigidArea(new Dimension(10, 10)));
        choiceFrame.pack();
        choiceFrame.setLocationRelativeTo(null);
        choiceFrame.setVisible(true);
    }

    public static void presVeto() {
        JFrame choiceFrame = new JFrame();
        choiceFrame.setLayout(new BoxLayout(choiceFrame.getContentPane(), BoxLayout.Y_AXIS));
        choiceFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        choiceFrame.setTitle("Accept/decline Veto");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(300, 400);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton noVetoButton = new JButton("I do not wish to call Veto!");
        buttonPanel.add(noVetoButton);
        noVetoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(noVetoButton).setVisible(false);
                try {
                    Menu.game.getGameSpace().put("presVeto", false);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        noVetoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton vetoButton = new JButton("I wish to call Veto!");
        buttonPanel.add(vetoButton);
        vetoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(vetoButton).setVisible(false);
                try {
                    Menu.game.getGameSpace().put("presVeto", true);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        vetoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        choiceFrame.add(buttonPanel);
        choiceFrame.pack();
        choiceFrame.setLocationRelativeTo(null);
        choiceFrame.setVisible(true);
    }

    // public static String voteDialogueBox(String sugChan) {
    //     String[] choices = { "Ja", "Nein" };
    //     String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
    //             "Should " + sugChan + " be elected chancellor?", JOptionPane.QUESTION_MESSAGE, null, // Use
    //             // default
    //             // icon
    //             choices, // Array of choices
    //             "Select vote");
    //     return input;
    // }

    public static void voteDialogue(String sugChan) throws IOException {
        chosenVote = null;

        JFrame choiceFrame = new JFrame();
        choiceFrame.setTitle("Vote for chancellor");
        choiceFrame.setLayout(new BoxLayout(choiceFrame.getContentPane(), BoxLayout.Y_AXIS));
        choiceFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel choicePanel = new JPanel();
        choicePanel.setSize(300, 400);
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));

        JLabel voteLabel = new JLabel("Should " + sugChan + " be elected chancellor?");
        voteLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        voteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0 ; i < 2 ; i++){
            ImageIcon io, ios;
            if (i == 0) { io = jaIcon; ios = jaIconSelected; }
            else {io = neinIcon; ios = neinIconSelected; }
            JButton voteButton = createButton(-1, null, null, io);
            Border border = BorderFactory.createLineBorder(Color.BLACK);
            voteButton.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(0,0,0,0)));
            choicePanel.add(voteButton);
            choicePanel.add(Box.createRigidArea(new Dimension(10, 10)));
            voteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean isSelected = voteButton.getIcon().equals(ios);
                    boolean valid = false;
                    if (chosenVote == null || isSelected) {
                        voteButton.setIcon(isSelected ? io : ios);
                        voteButton.invalidate(); // might not be needed
                        voteButton.repaint();
                        isSelected = !isSelected;
                        valid = true;
                    }
                    if (valid){
                        if (isSelected) {
                            chosenVote = voteButton.getIcon().equals(jaIconSelected) ? "Ja" : "Nein";
                        }
                        else chosenVote = null;
                    }
                }
            });
        }
        JButton submitButton = new JButton("Submit vote!");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (chosenVote != null){
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    System.out.println(chosenVote);
                    try {
                        Menu.game.getGameSpace().put("vote", chosenVote, Menu.game.getUser().Id());
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else
                    System.out.println("You haven't picked a vote!");
            }
        });
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        choiceFrame.add(voteLabel);
        choiceFrame.add(Box.createRigidArea(new Dimension(0, 20)));
        choiceFrame.add(choicePanel);
        choiceFrame.add(Box.createRigidArea(new Dimension(0, 20)));
        choiceFrame.add(submitButton, BorderLayout.CENTER);
        choiceFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        choiceFrame.pack();
        choiceFrame.setLocationRelativeTo(null);
        choiceFrame.setVisible(true);
    }
    
    public static void showRole(RoleType secretRole, PlayerRole[] allies) throws IOException {
        JFrame showFrame = new JFrame();
        showFrame.setTitle("Your role and party");
        showFrame.setLayout(new BoxLayout(showFrame.getContentPane(), BoxLayout.Y_AXIS));
        showFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel showPanel = new JPanel();
        showPanel.setSize(300, 400);
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));

        JLabel roleLabel = new JLabel("   You have been assigned the following party and secret role:   ");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon ioMem, ioSecret;
        if (secretRole == RoleType.Liberal) {
            ioMem = liberalMembership;
            ioSecret = liberalRole; // TODO
        } else if (secretRole == RoleType.Fascist) {
            ioMem = fascistMembership;
            ioSecret = fascistRole; // TODO
        } else {
            ioMem = fascistMembership;
            ioSecret = hitlerRole; // TODO
        }

        JLabel membership = new JLabel(ioMem);
        showPanel.add(membership);
        showPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        JLabel secret = new JLabel(ioSecret);
        showPanel.add(secret);

        JButton submitButton = new JButton("Duly noted!");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (secretRole == RoleType.Liberal || allies == null) {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        Menu.game.getGameSpace().put("checkedMyRole", Menu.game.getUser().Id());
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        showAllyRoles(allies);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }});
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        showFrame.add(Box.createRigidArea(new Dimension(0,30)));
        showFrame.add(roleLabel);
        showFrame.add(Box.createRigidArea(new Dimension(0,30)));
        showFrame.add(showPanel);
        showFrame.add(Box.createRigidArea(new Dimension(0,20)));
        showFrame.add(submitButton, BorderLayout.CENTER);
        showFrame.add(Box.createRigidArea(new Dimension(0,10)));
        showFrame.pack();
        showFrame.setLocationRelativeTo(null);
        showFrame.setVisible(true);
    }

    public static void showAllyRoles(PlayerRole[] allies) throws IOException {
        JFrame showFrame = new JFrame();
        showFrame.setLayout(new BoxLayout(showFrame.getContentPane(), BoxLayout.Y_AXIS));
        showFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        showFrame.setTitle("Allies");

        JPanel showPanel = new JPanel();
        showPanel.setSize(300, 400);
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));

        JLabel allyLabel = new JLabel("   These players are your allies!   ");
        JLabel noteLabel = new JLabel("(Make a mental note in your head!)");
        allyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        allyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noteLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        ImageIcon ioSecret;
        String name;
        for (PlayerRole playerRole : allies) {
            name = playerRole.Name;
            if (playerRole.SecretRole == RoleType.Fascist) {
                ioSecret = fascistRole;
            } else {
                ioSecret = hitlerRole;
            }
            JPanel allyPanel = new JPanel();
            allyPanel.setLayout(new BoxLayout(allyPanel, BoxLayout.Y_AXIS));
            JLabel allyRole = new JLabel(ioSecret);
            JLabel allyName = new JLabel(name);
            allyName.setFont(new Font("SansSerif", Font.BOLD, 14));
            allyName.setAlignmentX(Component.CENTER_ALIGNMENT);
            allyRole.setAlignmentX(Component.CENTER_ALIGNMENT);
            allyPanel.add(allyName);
            allyPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            allyPanel.add(allyRole);
            Border border = BorderFactory.createLineBorder(Color.BLACK);
            allyPanel.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            showPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            showPanel.add(allyPanel);
        }
        showPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        JButton submitButton = new JButton("I got it");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        Menu.game.getGameSpace().put("checkedMyRole", Menu.game.getUser().Id());
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            }});
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showFrame.add(Box.createRigidArea(new Dimension(0, 20)));
        showFrame.add(allyLabel);
        showFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        showFrame.add(noteLabel);
        showFrame.add(Box.createRigidArea(new Dimension(0, 40)));
        showFrame.add(showPanel);
        showFrame.add(Box.createRigidArea(new Dimension(0, 20)));
        showFrame.add(submitButton, BorderLayout.CENTER);
        showFrame.add(Box.createRigidArea(new Dimension(0, 10)));
        showFrame.pack();
        showFrame.setLocationRelativeTo(null);
        showFrame.setVisible(true);
    }

    public static void investigatePlayer(String Name, RoleType partyMembership) throws IOException {
        JFrame showFrame = new JFrame();
        showFrame.setLayout(new BoxLayout(showFrame.getContentPane(), BoxLayout.Y_AXIS));
        showFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        showFrame.setTitle("Investigate");

        JPanel showPanel = new JPanel();
        showPanel.setSize(300, 400);
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));
        
        
        String name = Name;
        ImageIcon ioMem;
        if (partyMembership == RoleType.Liberal) {
            ioMem = liberalMembership;
        } else {
            ioMem = fascistMembership;
        }
        
        JPanel investigatePanel = new JPanel();
        investigatePanel.setLayout(new BoxLayout(investigatePanel, BoxLayout.Y_AXIS));
        JLabel playerParty = new JLabel(ioMem);
        JLabel playerName = new JLabel(name);
        playerName.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerParty.setAlignmentX(Component.CENTER_ALIGNMENT);
        investigatePanel.add(playerName);
        investigatePanel.add(Box.createRigidArea(new Dimension(10, 10)));
        investigatePanel.add(playerParty);
        showPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        showPanel.add(investigatePanel);
        showPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        JButton submitButton = new JButton("I got it");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        Menu.game.getGameSpace().put("investigated");
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            }});
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showFrame.add(showPanel);
        showFrame.add(Box.createRigidArea(new Dimension(20, 20)));
        showFrame.add(submitButton, BorderLayout.CENTER);
        showFrame.add(Box.createRigidArea(new Dimension(10, 10)));
        showFrame.pack();
        showFrame.setLocationRelativeTo(null);
        showFrame.setVisible(true);
    }

    public static void showTopCards(LegislativeType... deckTop) throws IOException {
        JFrame showFrame = new JFrame();
        showFrame.setLayout(new BoxLayout(showFrame.getContentPane(), BoxLayout.Y_AXIS));
        showFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        showFrame.setTitle("Showing top 3 cards in deck");

        JPanel showPanel = new JPanel();
        showPanel.setSize(300, 400);
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.X_AXIS));
        
        ImageIcon cardIcon;
        String cardName;
        for (int i = 0; i < 3; i++) {
            
            if (deckTop[i] == LegislativeType.Fascist) {
                cardIcon = fascistCard;
            } else {
                cardIcon = liberalCard;
            }
            JPanel peekPanel = new JPanel();
            peekPanel.setLayout(new BoxLayout(peekPanel, BoxLayout.Y_AXIS));
            JLabel cardIconLabel = new JLabel(cardIcon);
            if (i == 0) {
                cardName = "Top card:";
                JLabel cardNameLabel = new JLabel(cardName);
                cardNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                peekPanel.add(cardNameLabel);
            }
            peekPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            peekPanel.add(cardIconLabel);
            showPanel.add(Box.createRigidArea(new Dimension(10, 10)));
            showPanel.add(peekPanel);
        }
        showPanel.add(Box.createRigidArea(new Dimension(10, 10)));

        JButton submitButton = new JButton("I got it");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                    try {
                        Menu.game.getGameSpace().put("iPeekedCards");
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

            }});
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showFrame.add(showPanel);
        showFrame.add(Box.createRigidArea(new Dimension(20, 20)));
        showFrame.add(submitButton, BorderLayout.CENTER);
        showFrame.add(Box.createRigidArea(new Dimension(10, 10)));
        showFrame.pack();
        showFrame.setLocationRelativeTo(null);
        showFrame.setVisible(true);
    }

    public static void initChatPanel() {
        chatPanel = new JPanel();
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
        chatPanel.setSize(400,1000);
    }

    public static void initGamePanel() {
        waiting = new JLabel("Waiting for players...");
        waiting.setFont(new Font("SansSerif",Font.BOLD, 40));
        waiting.setAlignmentX(Component.CENTER_ALIGNMENT);

        joinedPlayers = new JTextPane();
        joinedPlayers.setEditable(false);
        joinedPlayers.setFont(new Font("SansSerif",Font.PLAIN, 35));
        joinedPlayers.setAlignmentX(Component.CENTER_ALIGNMENT);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        joinedPlayers.setBorder(BorderFactory.createCompoundBorder(border,
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        StyledDocument doc = joinedPlayers.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        try {
            doc.insertString(0, "Joined players:\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setSize(900,1000);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        fascistBoard.setAlignmentX(Component.CENTER_ALIGNMENT);
        boardPanel.add(fascistBoard);
        boardPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        liberalBoard.setAlignmentX(Component.CENTER_ALIGNMENT);
        boardPanel.add(liberalBoard);
        IDPanel = new JPanel();
        innerIDPanel = new JPanel();
        //innerIDPanel.setLayout(new GridLayout(1,0));
        innerIDPanel.setLayout(new BoxLayout(innerIDPanel, BoxLayout.X_AXIS));
        IDPanel.setLayout(new BorderLayout());
        //membershipCard.setHorizontalAlignment(JLabel.LEFT);

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        nameLabel.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        numPlayerLabel = new JLabel("Number of players: " + numOfPlayers);
        numPlayerLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        numPlayerLabel.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        numPlayerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        membershipCard.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        membershipCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        roleCard.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        roleCard.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);

        innerIDPanel.add(nameLabel);
        innerIDPanel.add(Box.createRigidArea(new Dimension(30, 0)));
        innerIDPanel.add(membershipCard);
        innerIDPanel.add(Box.createRigidArea(new Dimension(15,0)));
        innerIDPanel.add(roleCard);
        startGamePanel = new JPanel();
        startGamePanel.setLayout(new BorderLayout());

        // ra1 = Box.createRigidArea(new Dimension(200, 0));
        // innerIDPanel.add(ra1);
        innerIDPanel.add(Box.createHorizontalGlue());
        innerIDPanel.add(numPlayerLabel);
        IDPanel.add(innerIDPanel, BorderLayout.PAGE_END);
        gamePanel.add(waiting);
        gamePanel.add(startGamePanel);
        ra2 = Box.createRigidArea(new Dimension(0, 50));
        gamePanel.add(ra2);
        gamePanel.add(joinedPlayers);
        gamePanel.add(boardPanel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        gamePanel.add(IDPanel);
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public static void gameFrame() throws IOException {
        initGameCards();
        initChatPanel();
        initGamePanel();

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setSize(chatPanel.getWidth() + gamePanel.getWidth(), 1000);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(chatPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(30, 30)));
        mainPanel.add(gamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(30, 30)));

        gameFrame.add(mainPanel);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(mainPanel.getSize());
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        gameFrame.setVisible(true);
        //showAllyRoles(new PlayerRole[]{new PlayerRole("Elias", RoleType.Hitler), new PlayerRole("Erik", RoleType.Fascist)});
        //investigatePlayer("Elias", RoleType.Liberal);
        //newVote("Elias");
        // welcomeDialogue();
        // chooseCards(false, LegislativeType.Fascist, LegislativeType.Fascist, LegislativeType.Liberal);
        // chooseCards(true, LegislativeType.Fascist, LegislativeType.Fascist, LegislativeType.Liberal);
        //showRole(RoleType.Fascist, new PlayerRole[]{new PlayerRole("Elias", RoleType.Hitler), new PlayerRole("Erik", RoleType.Fascist)});
        // deadScreen();
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
                            "<ChatBot>: Chat commands will be listed:\n"
                                    + "\".clear\":   Clears the chat screen messages.\n"
                                    + "\".tcp\":   Lists the tcp address of the chat room.\n"
                                    + "\".leave\":   Allows for leaving chatroom.\n",
                            true);
                } else if (msg.equals(".clear")) {
                    chatBox.setText("");
                    append(chatBox, "<ChatBot>: The chat has been cleared!\n", true);
                } else if (msg.equals(".tcp")) {
                    append(chatBox, "<ChatBot>: This chat room's tcp is: " + tcp + "\n", true);
                } else if (msg.equals(".increment")){
                    append(chatBox, "<ChatBot>: The board was updated!\n", true);
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
        scrollBar.setValue(scrollBar.getMaximum());
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

    public static String suggestDialogueBox(String[] choices, String suggestMsg) {
        String input = (String) JOptionPane.showInputDialog(null, "Choose now...",
                suggestMsg, JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                choices, // Array of choices
                choices[0]); // Initial choice
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
            // String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", localtcp+":"+port);
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "192.168.68.112:9001");
                    // IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", localtcp+":"+port);
                } while (IP_Port.isEmpty());
            }
            frame.setVisible(false);
            tcp = IP_Port;
            gameHost = true;
            gameFrame.setTitle("Secret Hitler  |  " + name + "'s Room  |  tcp: " + tcp);
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
            // String IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)",  localtcp+":"+port);
            if (IP_Port == null) {
                frame.setVisible(true);
                return;
            } else if (IP_Port.isEmpty()) {
                do {
                    IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)", "212.237.106.43:9001");
                    // IP_Port = JOptionPane.showInputDialog(frame, "Enter tcp address: (default)",  localtcp+":"+port);
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

            gameFrame.setTitle("Secret Hitler  |  " + hostName + "'s Room  |  tcp: " + tcp);
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
        private static final long serialVersionUID = 3963988367577770364L;

        public void actionPerformed(ActionEvent e) {
            try {
                Menu.game.getGameSpace().put("start", numOfPlayers);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //gamePanel.remove(startGameButton);
            //innerIDPanel.remove(startGameButton);
            startGamePanel.remove(startGameButton);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    };

    public static void setGameBoard(int playerCount){
        gamePanel.remove(ra2);
        gamePanel.remove(joinedPlayers);
        gamePanel.remove(waiting);
        gamePanel.remove(startGamePanel);
        fascistBoardIndex = (playerCount < 7) ? 0 : (playerCount < 9) ? 1 : 2;
            fascistBoard.setIcon(fascistBoardImageMatrix[fascistBoardIndex][0]);
        liberalBoard.setIcon(liberalBoardImageMatrix[0][0]);
    }

    public static void setCards(RoleType rt){
        ImageIcon role, party;
        if (rt == RoleType.Fascist || rt == RoleType.Hitler) {
            party = fascistMembership;
            if (rt == RoleType.Hitler) role = hitlerRole;
            else role = fascistRole;
        } else {
            role = liberalRole;
            party = liberalMembership;
        }
        membershipCard.setIcon(party);
        roleCard.setIcon(role);
    }

	public static void gameOverScreen(int gameState) {
        if (gameState == 1) {
            Helper.appendAndSend("Liberals won!");
        } else {
            Helper.appendAndSend("Fascists won!");
        }
	}

	public static void incFascArticles() {
        fascistArticles++;
        fascistBoard.setIcon(fascistBoardImageMatrix[fascistBoardIndex][fascistArticles]);
        resetLibFails();
    }

	public static void incLibArticles() {
        liberalArticles++;
        liberalFails = 0;
        liberalBoard.setIcon(liberalBoardImageMatrix[liberalArticles][liberalFails]);
    }
    
    public static void incLibFails(){
        liberalFails++;
        liberalBoard.setIcon(liberalBoardImageMatrix[liberalArticles][liberalFails]);
    }

    public static void resetLibFails(){
        liberalFails=0;
        liberalBoard.setIcon(liberalBoardImageMatrix[liberalArticles][liberalFails]);
    }

	public static void deadScreen() {
        JFrame showFrame = new JFrame();
        showFrame.setTitle("You are dead!");
        showFrame.setLayout(new BoxLayout(showFrame.getContentPane(), BoxLayout.Y_AXIS));
        showFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel showPanel = new JPanel();
        showPanel.setSize(300, 400);
        showPanel.setLayout(new BoxLayout(showPanel, BoxLayout.Y_AXIS));

        JLabel killedLabel = new JLabel("You have been killed!");
        killedLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        killedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel deadLabel = new JLabel();
        deadLabel.setIcon(deadLabelIcon);
        deadLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea infoText = new JTextArea();
        infoText.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoText.setEditable(false);
        infoText.setText("   You will be able to spectate the game until it's done, \n           but you will be unable to perform any actions!   ");
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton submitButton = new JButton("Alright!");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    SwingUtilities.getWindowAncestor(submitButton).setVisible(false);
                }
            });
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        showPanel.add(killedLabel);
        showPanel.add(deadLabel);
        showPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        showPanel.add(infoText);
        showPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        showPanel.add(submitButton);
        showPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        showFrame.add(showPanel);
        showFrame.pack();
        showFrame.setLocationRelativeTo(null);
        showFrame.setVisible(true);
	}
    
    // public static void playSong(URL media) {
    //     Player mediaPlayer = Manager.createRealizedPlayer(media);
    //     mediaPlayer.start();
    // WORK IN PROGRESS
}
