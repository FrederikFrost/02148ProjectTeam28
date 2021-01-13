package common.src.main;

public class Role {
    private RoleType PartyMembership;
    private RoleType SecretRole;
    
    public Role(RoleType partyMembership, RoleType secretRole) {
        PartyMembership = partyMembership;
        SecretRole = secretRole;
    }

    public RoleType getSecretRole() {
        return SecretRole;
    }

    public RoleType getPartyMembership() {
        return PartyMembership;
    }

    @Override
    public String toString() {
        return SecretRole.toString();
    }
}
