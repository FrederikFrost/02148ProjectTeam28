package common.src.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;

public class Menu {
    public static SecretHitlerV2 game;
	public static void display() {
        JFrame frame = new JFrame("Secret Hitler");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.setSize(800, 600);
        int WIDTH = frame.getWidth();
        int HEIGHT = frame.getHeight();

        JLabel nameInputLabel = new JLabel("Enter your name");
        nameInputLabel.setHorizontalAlignment(JLabel.CENTER);

        JTextField nameInput = new JTextField(20);

        JButton createGame = new JButton("Create Game");
        JButton joinGame = new JButton("Join Game");
        JButton playMusic = new JButton("Play Music");

        // add text field and label to panel
        frame.getContentPane().add(nameInput, BorderLayout.SOUTH);
        frame.getContentPane().add(nameInputLabel, BorderLayout.SOUTH);
        frame.getContentPane().add(createGame, BorderLayout.SOUTH);
        frame.getContentPane().add(joinGame);
        frame.getContentPane().add(playMusic);
        frame.pack();

        System.out.println(frame.getHeight() + " " +  frame.getWidth());
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
                String IP_Port = JOptionPane.showInputDialog(frame, "What is your name?", "192.168.68.112:9001");
                game.gameCreate(IP_Port);
                System.out.println("Created Game");
            } 
            });  

        joinGame.addActionListener(new AbstractAction(){  
            public void actionPerformed(ActionEvent e){  
                    System.out.println("Joined Game");
            }  
            });  

        nameInput.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String content = nameInput.getText();
                System.out.println(content + " just entered their name!");
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
        JFrame cgFrame = new JFrame("Secret Hitler");
        JPanel cgPanel = new JPanel();
        JButton submitGameId = new JButton("Search for game");
        JLabel gameId = new JLabel("Enter Game ID");
        JTextField gameIdField = new JTextField(6);
        gameIdField.setDocument(new JTextFieldLimit(6));
        cgPanel.add(gameId);
        cgPanel.add(gameIdField);
        cgPanel.add(submitGameId);
        cgFrame.add(cgPanel);
        cgFrame.setLocationRelativeTo(null);
        cgFrame.pack();
        cgFrame.setVisible(true);
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
