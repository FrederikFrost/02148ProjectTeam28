package common.src.main;
public class Types {
    
    public enum ActionType {
        None, /** No legislative power */
        Investigate, /** Investigate a players party member card */
        S_Election, /** Special Election */
        Peek, /** Policy Peek */
        Kill, /** Execute a player */
        Veto  /** A player is killed here aswell. Veto power is unlocked permanently.  <p>
                Veto power: If Chancellor and President agrees, they can discard all legislative cards drawn */
    }

    public enum LegislativeType {
        None,
        Fascist,
        Liberal
    }

    public enum RoleType {
        Liberal,
        Fascist,
        Hitler
    }

    public enum VoteType {
        None,
        Nein,
        Ja
    }

    public enum CommandType {
        Election, //Whenever a election is to be made.
        LegislativeSession,
        ExecutiveAction,
    }


}