package common.src.main;


public class Menu {
    public static SecretHitlerV2 game;
    public static ChatHandler chatHandler;

    public static void main(String[] args) {
        game = new SecretHitlerV2();
        try {
            MenuComponents.menu();
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

    // public static void suggestList(JPanel panel){
    //     JList jList = new JList();
    //     JTextArea jta = new JTextArea();
    //     jList.setModel(new AbstractListModel() {
    //         String[] strings = {"User 1", "User 2", "User 3", "User 4", "User 5"};
    //         @Override
    //         public int getSize() {
    //             return strings.length;
    //         }
    //         @Override
    //         public Object getElementAt(int i) {
    //             return strings[i];
    //         }
    //     });
    //     jList.addListSelectionListener(new ListSelectionListener() {
    //         @Override
    //         public void valueChanged(ListSelectionEvent evt) {
    //             jListValueChanged(jList, jta, evt);
    //         }
    //     });
    //     panel.add(jList);
    //     panel.add(jta);
    // }
    
    // private static void jListValueChanged(JList jl, JTextArea jta, ListSelectionEvent evt) {
    //     //set text on right here
    //     String s = (String) jl.getSelectedValue();
    //     if (s.equals("Item 1")) {
    //         jta.setText("You clicked on list 1");
    //     }
    //     if (s.equals("Item 2")) {
    //         jta.setText("You clicked on list 2");
    //     }
    // }
