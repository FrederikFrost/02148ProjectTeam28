package common.src.main;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


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
                /**
                 * Suggest chancellor
                 * Vote chancellor
                 * loop if chancellor isn't chosen
                 *  - counter for inactive 
                 * 
                 * choose legislate
                 *  - show president 3 cards
                 *  - president discards 1, show chancellor remaining cards
                 *  - chancellor picks legislate
                 *     - veto logic should be here
                 * 
                 * executive action
                 * 
                 * rotate president (except if executive action: Special Election)
                 * 
                */

                //game started
                Boolean elected = false;
                int i = 0;
                int suggestedChancellor = -1;
                while(elected == false && i < 3) {
                    suggestedChancellor = SuggestChancellor();
                    elected = Election(playerCount, suggestedChancellor);
                    i++;
                    //TODO - we need to rotate president here
                }
                if (!elected) {
                    // skip choose legislate
                }
            }
            
        } catch (Exception e) {
            //possibly put tuble in game space to let players know an error occured
            e.printStackTrace();
        }
    }

    public int SuggestChancellor() throws Exception{
        _gameSpace.get(new ActualField("lock"));
        
        int pres = (int) _gameSpace.get(new ActualField("president"), new FormalField(Integer.class))[1];
        _gameSpace.put("suggest", pres);
        //TODO: send president choices to pick from
        _gameSpace.put("lock");
        //TODO: should a lock be here aswell?
        return (int) _gameSpace.get(new ActualField("suggestion"), new FormalField(Integer.class)) [1];
    }

    public Boolean Election(int playerCount, int newChancellor) throws Exception {
        //maybe change to ArrayList<int[]>
        _gameSpace.get(new ActualField("lock"));

        Vote[] votes = new Vote[playerCount];  //should account for dead players
        Arrays.fill(votes, Vote.None);
        _gameSpace.getp(new ActualField("votes"), new FormalField(Vote[].class), new ActualField(playerCount));    //should also account for votes

        _gameSpace.put("votes", votes, 0); 
        _gameSpace.put("startVote");

        _gameSpace.put("lock");
        
        Object[] votesReturn = _gameSpace.query(new ActualField("votes"), new FormalField(Vote[].class), new ActualField(playerCount));    //should also account for votes

        int numToPass = playerCount/2+1;
        int votesChancellor = 0;
        for (Vote vote : (Vote[]) votesReturn[1]) {
            if (vote == Vote.Ja) {
                votesChancellor++;
            }
        }
        if (votesChancellor >= numToPass) {
            _gameSpace.get(new ActualField("lock")); 
            _gameSpace.put("chancellor", newChancellor);
            _gameSpace.put("lock");

            return true;
        }
        
        return false;
    }

    //#region setup
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
    //#endregion

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


}
