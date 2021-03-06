package common.src.main;

import java.io.IOException;
import java.util.ArrayList;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import common.src.main.Types.LegislativeType;
import common.src.main.Types.VoteType;

public class Game {
    static Space userSpace = Menu.game.getUserSpace();
    static Space gameSpace = Menu.game.getGameSpace();

    public static int suggest(int[] eligibleCands, String suggestMsg, int id) throws InterruptedException {
        // TODO: make suggestion pop-up list.
        String[] choices = new String[eligibleCands.length];
        User[] users = (User[]) gameSpace.query(new ActualField("users"), new FormalField(User[].class))[1];

        for (int i = 0; i < choices.length; ++i) {
            choices[i] = users[eligibleCands[i]].Name();
        }

        Helper.printArray("choices", choices);
        MenuComponents.suggestDialogue(choices, suggestMsg);
        Object[] suggestObject = gameSpace.get(new ActualField("sugdiag"), new FormalField(String.class), new ActualField(id));
        String sugChan = (String) suggestObject[1];
        // sugChan = MenuComponents.suggestDialogueBox(choices, suggestMsg);
        // if (sugChan == null) {
        //     do
        //         sugChan = MenuComponents.suggestDialogueBox(choices, suggestMsg);
        //     while (sugChan == null);
        // }
        int suggestion = -1;
        for (User user : users) {
            if (user.Name().equals(sugChan)) {
                suggestion = user.Id();
                break;
            }
        }

        return suggestion;
    }

    public static VoteType vote(int suggestion, boolean pres, int id) throws InterruptedException, IOException {
        // TODO: Make vote pop-up.
        String sugChan = "";
        try {
            sugChan = (String) userSpace.query(new ActualField("join"),
                new FormalField(String.class), new ActualField(suggestion))[1];
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (pres) {
            Helper.appendAndSend(sugChan + " was suggested chancellor by the president! "
            + "Cast your vote now!");
        }
        // MenuComponents.append(MenuComponents.chatBox, "<ChatBot>: " +
        // sugChan + " was suggested chancellor by the president! "
        //     + "Cast your vote now!\n", true);
        MenuComponents.voteDialogue(sugChan);
        Object[] voteObject = gameSpace.get(new ActualField("vote"), new FormalField(String.class), new ActualField(id));
        String vote = (String) voteObject[1];
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
