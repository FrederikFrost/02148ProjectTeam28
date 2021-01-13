package common.src.main;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.Action;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class GameController implements Runnable {
    public Space _chatSpace;
    public Space _userSpace;
    public Space _gameSpace;

    public GameController(Space _chatSpace, Space _userSpace,Space _gameSpace) {
        this._chatSpace = _chatSpace;
        this._userSpace = _userSpace;
        this._gameSpace = _gameSpace;
    }

    @Override
    public void run() {
        boolean enoughPlayers = false;
        boolean gameStarted = false;
        int playerCount = -1;
        try {
            while (!gameStarted) {
                if (!enoughPlayers) {
                    Object[] userLock = _userSpace.query(new ActualField("lock"), new FormalField(Integer.class));
                    if ((int) userLock[1] >= 5) {
                        enoughPlayers = true;
                        _gameSpace.get(new ActualField("lock"));
                        _gameSpace.put("readyToStart");
                        _gameSpace.put("lock");
                    }
                } else {
                    _gameSpace.get(new ActualField("start"));
                    playerCount = (int) _userSpace.query(new ActualField("lock"), new FormalField(Integer.class))[1];
                    //TODO: maybe grap userSpace lock to prevent players from joining a started game
                    gameStarted = true;
                }
            }

            _gameSpace.get(new ActualField("lock"));
            
            SetupGame(playerCount);
            AssignRoles(playerCount);
            
            _gameSpace.put("lock");
            
            while(true) {
                //game started
            }
            
        } catch (Exception e) {
            //possibly put tuble in game space to let players know an error occured
            e.printStackTrace();
        }
    }

    public void AssignRoles(int playerCount) throws Exception {

        Role liberal = new Role(RoleType.Liberal, RoleType.Liberal);
        Role fascist = new Role(RoleType.Fascist, RoleType.Fascist);
        Role hitler = new Role(RoleType.Fascist, RoleType.Hitler);

        Role[] roles;
        switch (playerCount) {
            case 5:
                roles = new Role[] {liberal, liberal, liberal, fascist, hitler};
                break;
            case 6:
                roles = new Role[] {liberal, liberal, liberal, liberal, fascist, hitler};
                break;     
            case 7:
                roles = new Role[] {liberal, liberal, liberal, liberal, fascist, fascist, hitler};
                break;
            case 8:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, fascist, fascist, hitler};
                break;
            case 9:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, fascist, fascist, fascist, hitler};
                break;
            case 10:
                roles = new Role[] {liberal, liberal, liberal, liberal, liberal, liberal, fascist, fascist, fascist, hitler};
                break;
            default:
                throw new IllegalArgumentException("Player size is wrong");   //maybe refactor this to if statement above
        }

        Collections.shuffle(Arrays.asList(roles));
        _gameSpace.put("roles", roles, playerCount);
        
        Random rand = new Random();
        int president = rand.nextInt(playerCount);
        _gameSpace.put("president", president);

    }

    public void SetupGame(int playerCount) throws Exception {
        if (playerCount < 5) throw new IllegalArgumentException("Player count cannot be less than 5." + (playerCount==-1? "PlayerCount was not set":""));

        LegislativeType[] liberalBoard = new LegislativeType[5];
        LegislativeType[] fascistBoard = new LegislativeType[6];
        Arrays.fill(liberalBoard, LegislativeType.None);
        Arrays.fill(fascistBoard, LegislativeType.None);

        //set presedential powers
        ActionType[] executivePowers; 
        switch (playerCount) {
            case 5:
            case 6:
                executivePowers = new ActionType[] {ActionType.None, ActionType.None, ActionType.Peek, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;     
            case 7:
            case 8:
                executivePowers = new ActionType[] {ActionType.None, ActionType.Investigate, ActionType.Peek, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;
            case 9:
            case 10:
                executivePowers = new ActionType[] {ActionType.Investigate, ActionType.Investigate, ActionType.Peek, ActionType.Kill, ActionType.Veto, ActionType.None};
                break;
            default:
                executivePowers = new ActionType[6];
                throw new IllegalArgumentException("Cannot be more than 10 players");   //maybe refactor this to if statement above
        }

        LegislativeType[] deck = GetShuffledDeck();

        _gameSpace.put("boards", liberalBoard, fascistBoard, executivePowers);
        _gameSpace.put("deck", deck);   //possibly shouldn't be there, depends how the users gameLoop's logic is implemented

    }

    public LegislativeType[] GetShuffledDeck() {
        int liberalCards = 6;
        int fascistCards = 11;

        LegislativeType[] deck = new LegislativeType[17];
        Random rand = new Random();
        for (int i = 0; i < 17; ++i) {
            if ((liberalCards > 0 && rand.nextInt(3) == 0) || fascistCards == 0) {
                deck[i] = LegislativeType.Liberal;
                liberalCards--;
            } else {
                deck[i] = LegislativeType.Fascist;
                fascistCards--;
            }
        }

        return deck;
    }

    private void printGameStatus() {
        //print game status for debug
    }
}
