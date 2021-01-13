package common.src.main;

import java.lang.reflect.Method;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class GameControllerTests {
    public static Space _chatSpace;
    public static Space _userSpace;
    public static Space _gameSpace;
    public static void main(String[] args) {
        _chatSpace = new SequentialSpace();
        _userSpace = new SequentialSpace();
        _gameSpace = new SequentialSpace();
        try {
            _chatSpace.put("lock", 0);
            _userSpace.put("lock", 0);
            _gameSpace.put("lock");
        } catch (Exception e) {
            e.printStackTrace();
        }

        runMethod("CardShuffleTest", args);
        runMethod("SetupGameTest", args);
    }

    

    private static void CardShuffleTest() {

        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        LegislativeType[] deck = controller.GetShuffledDeck();
        int lib, fas;
        lib = fas = 0;

        System.out.print("[");
        for (int i = 0; i < 17; ++i) {
            if (deck[i] == LegislativeType.Fascist) {
                System.out.print("F");
                fas++;
            } else {
                System.out.print("L");
                lib++;
            }
            if (i != 17-1){
                System.out.print(", ");
            }
        }
        System.out.println("]");
        System.out.println("Lib: " + lib + ", Fas: " + fas);
        System.out.println();
    }

    private static void SetupGameTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int numberOfPlayers = 7;
        try {
            controller.SetupGame(numberOfPlayers);
            Object[] boards = _gameSpace.get(new ActualField("boards"), new FormalField(LegislativeType[].class), 
                new FormalField(LegislativeType[].class), new FormalField(ActionType[].class));
            Helper.printArray("Liberal", (LegislativeType[]) boards[1]);
            Helper.printArray("Fascist", (LegislativeType[]) boards[2]);
            Helper.printArray("Actions", (ActionType[]) boards[3]);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void runMethod(String method, String[] args) {
        System.out.println("---------------------------------------");
        System.out.println("Starting test: " + method);
        try {
            Class<?> c = Class.forName("common.src.main.GameControllerTests");
            Method methodToRun = c.getDeclaredMethod(method, new Class[]{});
            methodToRun.invoke(null, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
