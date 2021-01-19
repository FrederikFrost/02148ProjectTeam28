package common.src.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Types.LegislativeType;
import common.src.main.Types.VoteType;

public class Game {
    static Space userSpace = Menu.game.getUserSpace();

    
    public static int suggest(int[] eligibleCands) {
            // TODO: make suggestion pop-up list.
            // String[] choices = new String[eligibleCands.size()];
            String[] choices = new String[eligibleCands.length];
            System.out.println(choices.length);
            // Integer[] eliCands = eligibleCands.toArray(new Integer[eligibleCands.size()]);
            for (int cand : eligibleCands) {
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

	public static VoteType vote(int suggestion) {
        // TODO: Make vote pop-up.
        String sugChan = "";
        try {
            sugChan = (String) userSpace.query(new ActualField("join"),
                new FormalField(String.class), new ActualField(suggestion))[1];
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Helper.appendAndSend(sugChan + " was suggested chancellor by the president! "
        + "Cast your vote now!");
        // MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: " +
        // sugChan + " was suggested chancellor by the president! "
        //     + "Cast your vote now!\n", true);
        String vote = null;
        do {
            vote = MenuComponents.voteDialogueBox(sugChan);
        } while (vote == null);
        Helper.appendAndSend(MenuComponents.username + " voted: " + vote);
        if (vote.equals("Ja")) {
            return VoteType.Ja;
        } else {
            return VoteType.Nein;
        }
	}

	public static void updateVotes(VoteType[] votes) {
        // TODO: update graphics for user votes.
	}

	public static ArrayList<LegislativeType> ChooseLegislate(ArrayList<LegislativeType> cards, boolean vetoEnabled) {
		return null;
	}
    
    public static ArrayList<LegislativeType> ChooseLegislate(ArrayList<LegislativeType> cards) {
        return ChooseLegislate(cards, false);
    }

	public static boolean GetVetoResponseFromPres() {
		return false;
	}

            
}
