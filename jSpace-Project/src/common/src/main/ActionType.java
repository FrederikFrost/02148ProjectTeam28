package common.src.main;

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
    /** A player is killed here aswell. Veto power is unlocked permanently. <p> Veto power: If Chancellor and President agrees, they can discard all legislative cards drawn */
    Veto

    
}
