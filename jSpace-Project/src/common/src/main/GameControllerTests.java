package common.src.main;

import java.lang.reflect.Method;
import java.util.Random;

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

        // runMethod("CardShuffleTest", args);
        // runMethod("AssignRolesTest", args);
        // runMethod("SetupGameTest", args);
        runMethod("SuggestChancellorTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
        runMethod("ElectionTest", args);
    }

    private static void SuggestChancellorTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);

        try {
            _gameSpace.put("president", 1);
            new Thread(() -> {
                try {
                    _gameSpace.get(new ActualField("suggest"), new ActualField(1));
                    _gameSpace.put("suggestion", 4);

                } catch (Exception e) {
                    //TODO: handle exception
                }
            }).start();
    
            int suggestion = controller.SuggestChancellor();
            System.out.println("Voting on " + suggestion + " for Chancellor!");
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static void ElectionTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int playerCount = 5;
        Random rand = new Random();
        new Thread(() -> {
            try {
                _gameSpace.get(new ActualField("startVote"));
                for (int i = 0; i < 5; ++i) {
                    _gameSpace.get(new ActualField("lock"));
                    Object[] votesTuple = _gameSpace.get(new ActualField("votes"), new FormalField(Vote[].class), new FormalField(Integer.class));
                    Vote answer = rand.nextBoolean()? Vote.Ja : Vote.Nein;
                    Vote[] votes = (Vote[]) votesTuple[1];
                    votes[i] = answer;
                    int count = (int)votesTuple[2]+1;
                    _gameSpace.put("votes", votes, count);
                    System.out.println(i + " just voted: " + answer.toString() + " | count: " + count);
                    Helper.printArray("Votes", votes);
                    _gameSpace.put("lock");
                }

            } catch (Exception e) {
                //TODO: handle exception
            }
        }).start();
        try {
            boolean res = controller.Election(playerCount, 4);
            Object[] votesTuple = _gameSpace.get(new ActualField("votes"), new FormalField(Vote[].class), new FormalField(Integer.class));
            if (res) {
                Object[] chanTuple = _gameSpace.get(new ActualField("chancellor"), new FormalField(Integer.class));
                System.out.println((int) chanTuple[1] + " was elected chancellor");
                Helper.printArray("Votes", (Vote[]) votesTuple[1], true);
            } else {
                System.out.println("Votes failed");
                Helper.printArray("Votes", (Vote[]) votesTuple[1], true);
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }

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
            Object[] deck = _gameSpace.get(new ActualField("deck"), new FormalField(LegislativeType[].class));
            Helper.printArray("Liberal", (LegislativeType[]) boards[1]);
            Helper.printArray("Fascist", (LegislativeType[]) boards[2]);
            Helper.printArray("Actions", (ActionType[]) boards[3]);
            Helper.printArray("Deck", (LegislativeType[]) deck[1], true);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void AssignRolesTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int numberOfPlayers = 7;
        try {
            controller.AssignRoles(numberOfPlayers);
            Object[] roles = _gameSpace.get(new ActualField("roles"), new FormalField(Role[].class), new FormalField(Integer.class));
            Object[] president = _gameSpace.get(new ActualField("president"), new FormalField(Integer.class));
            Helper.printArray("Roles", (Role[]) roles[1]);
            System.out.println("President: " + president[1]);
        } catch (Exception e) {
            //TODO: handle exception
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
