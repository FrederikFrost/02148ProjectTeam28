package common.src.main;

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
        try {
            while (!gameStarted) {
                if (!enoughPlayers) {
                    Object[] userLock = _userSpace.query(new ActualField("lock"), new FormalField(Integer.class));
                    if ((int) userLock[1] >= 5) {
                        enoughPlayers = true;
                        _gameSpace.put("readyToStart");
                    }
                } else {
                    Object[] startGame = _gameSpace.query(new ActualField("start"));
                    gameStarted = true;
                }
            }

            while(true) {
                //game started
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }
    }


}
