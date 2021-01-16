package common.src.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import common.src.main.Types.ActionType;
import common.src.main.Types.LegislativeType;
import common.src.main.Types.VoteType;

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
        runMethod("GetCardsFromDeckTest", args);
        // runMethod("ElectionTest", args);
    }

    private static void GetCardsFromDeckTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int playerCount = 5;
        controller.setPlayerCount(playerCount);

        try {
            controller.SetupGame();

            int draw = (int) _gameSpace.query(new ActualField("drawPile"), new FormalField(Integer.class))[1];
            int discard = (int) _gameSpace.query(new ActualField("discardPile"), new FormalField(Integer.class))[1];
            System.out.println("Draw: " + draw + " | Discard: " + discard);
            Helper.printArray("Deck", controller.getDeck().toArray(), true);
            System.out.println("GetCardsFromDeck(1)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(1).toArray());
            System.out.println("GetCardsFromDeck(1, true)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(1, true).toArray());
            System.out.println("GetCardsFromDeck(1)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(1).toArray());
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            Helper.printArray("Deck", controller.getDeck().toArray(), true);
            System.out.println("GetCardsFromDeck(3,true)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3,true).toArray());
            Helper.printArray("Deck", controller.getDeck().toArray(), true);
            System.out.println("GetCardsFromDeck(3)");
            Helper.printArray("Drawn", controller.GetCardsFromDeck(3).toArray());
            Helper.printArray("Deck", controller.getDeck().toArray(), true);


            
        } catch (Exception e) { 
            e.printStackTrace();
        }
        

    }

    private static void SuggestChancellorTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);

        try {
            _gameSpace.put("president", 1);
            new Thread(() -> {
                try {
                    _gameSpace.get(new ActualField("suggest"), new ActualField(1), new FormalField(ArrayList.class));
                    _gameSpace.put("suggestion", 4);

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();

            int suggestion = controller.SuggestChancellor();
            System.out.println("Voting on " + suggestion + " for Chancellor!");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void ElectionTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int playerCount = 7;
        controller.setPlayerCount(playerCount);
        ArrayList<Integer> deads = new ArrayList<Integer> (Arrays.asList(0,1));

        Random rand = new Random();
        new Thread(() -> {
            try {
                _gameSpace.get(new ActualField("startVote"));
                for (int i = 0; i < 5; ++i) {
                    _gameSpace.get(new ActualField("lock"));
                    Object[] votesTuple = _gameSpace.get(new ActualField("votes"), new FormalField(VoteType[].class),
                            new FormalField(Integer.class));
                    VoteType answer = rand.nextBoolean() ? VoteType.Ja : VoteType.Nein;
                    VoteType[] votes = (VoteType[]) votesTuple[1];
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
            _gameSpace.put("deadPlayers", deads);
            boolean res = controller.Election(4);
            Object[] votesTuple = _gameSpace.get(new ActualField("votes"), new FormalField(VoteType[].class), new FormalField(Integer.class));
            if (res) {
                Object[] chanTuple = _gameSpace.get(new ActualField("chancellor"), new FormalField(Integer.class));
                System.out.println((int) chanTuple[1] + " was elected chancellor");
                Helper.printArray("Votes", (VoteType[]) votesTuple[1], true);
            } else {
                System.out.println("Votes failed");
                Helper.printArray("Votes", (VoteType[]) votesTuple[1], true);
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static void rotatePresidentTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int playerCount = 7;
        controller.setPlayerCount(playerCount);
        ArrayList<Integer> deads = new ArrayList<Integer> (Arrays.asList(0,1));

        try {
            _gameSpace.put("deadPlayers", deads);
            int pres = 6;
            int oldPres = 5;
            _gameSpace.put("president", pres);
            _gameSpace.put("oldPresident", oldPres);

            System.out.println("President: " + pres + " | Old president: " + oldPres);

            boolean useOldPres = controller.rotatePresident(false); 

            pres = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
            oldPres = (int) _gameSpace.query(new ActualField("oldPresident"), new FormalField(Integer.class))[1];
            System.out.println("President: " + pres + " | Old president: " + oldPres);

            useOldPres = controller.rotatePresident(useOldPres, 4);

            pres = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
            oldPres = (int) _gameSpace.query(new ActualField("oldPresident"), new FormalField(Integer.class))[1];
            System.out.println("President: " + pres + " | Old president: " + oldPres);

            useOldPres = controller.rotatePresident(useOldPres);

            pres = (int) _gameSpace.query(new ActualField("president"), new FormalField(Integer.class))[1];
            oldPres = (int) _gameSpace.query(new ActualField("oldPresident"), new FormalField(Integer.class))[1];
            System.out.println("President: " + pres + " | Old president: " + oldPres);


        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static void GetEligibleCandidatesTest() {
        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        int playerCount = 7;
        controller.setPlayerCount(playerCount);
        controller.setLastPres(3);
        controller.setLastChancellor(4);
        ArrayList<Integer> deads = new ArrayList<Integer> (Arrays.asList(1,5));
        try {
            _gameSpace.put("deadPlayers", deads);
            ArrayList<Integer> eligible = controller.GetEligibleCandidates();
            Helper.printArray("Eligible", eligible.toArray(new Integer[0]));
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private static void CardShuffleTest() {

        GameController controller = new GameController(_chatSpace, _userSpace, _gameSpace);
        LegislativeType[] deck = (LegislativeType[]) controller.GetShuffledDeck().toArray();
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
        int playerCount = 5;
        controller.setPlayerCount(playerCount);

        try {
            controller.SetupGame();
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
        int playerCount = 5;
        controller.setPlayerCount(playerCount);

        try {
            controller.AssignRoles();
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
