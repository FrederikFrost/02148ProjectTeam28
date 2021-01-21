package common.src.main;

public class Menu {
    public static GameHandler game;
    public static ChatHandler chatHandler;

    public static void main(String[] args) {
        
        game = new GameHandler();
        try {
            MenuComponents.menu();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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