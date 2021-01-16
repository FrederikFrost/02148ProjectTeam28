package common.src.main;
public class Types {
    
    public enum ActionType {
        /** No legislative power */
        None, 
        /** Investigate a players party member card */
        Investigate, 
        /** Special Election */
        S_Election, 
        /** Policy Peek */
        Peek, 
        /** Execute a player */
        Kill, 
        /** A player is killed here aswell. Veto power is unlocked permanently.  <p>
                Veto power: If Chancellor and President agrees, they can discard all legislative cards drawn */
        Veto  
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

    public enum ErrorType {
        GameFull,
        NameTaken,
        GameStarted,
        NoError,
    }


}