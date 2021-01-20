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