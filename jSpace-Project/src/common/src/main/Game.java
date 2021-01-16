package common.src.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Types.VoteType;

public class Game {
    static Space userSpace = Menu.game.getUserSpace();

    
    public static int suggest(ArrayList<Integer> eligibleCands) {
            // TODO: make suggestion pop-up list.
            MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: The president is suggesting a chancellor\n", true);
            String[] choices = {};
            for (Integer cand : eligibleCands) {
                try {
                    choices[cand] = (String) userSpace.query(new ActualField("join"),
                        new FormalField(String.class), new ActualField(cand))[1];
                } catch (InterruptedException e) {
                    e.printStackTrace();}
            }
            String sugChan;
            sugChan = MenuComponents.suggestDialogueBox(choices);
            if (sugChan == null) {
                do sugChan = MenuComponents.suggestDialogueBox(choices);
                while (sugChan == null);
            }
        int suggestion = Arrays.asList(choices).indexOf(sugChan);
		return suggestion;
	}

	public static Boolean vote(int suggestion) {
        // TODO: Make vote pop-up.
        String sugChan = "";
        try {
            sugChan = (String) userSpace.query(new ActualField("join"),
                new FormalField(String.class), new ActualField(suggestion))[1];
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //TODO: MAKE THIS SEND TO ALL PLAYERS
        MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: " +
        suggestion + " was suggested chancellor by the president!"
            + "Use \".vote\"to cast your vote!\n", true);

        String vote = MenuComponents.voteDialogueBox(sugChan);
        if (vote == null) {
            do
                vote = MenuComponents.voteDialogueBox(sugChan);
            while (vote == null);
            }
        MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: " + MenuComponents.username + " voted: " + vote, true);
		return true;
	}

	public static void updateVotes(VoteType[] votes) {
        // TODO: update graphics for user votes.
	}
            
}
