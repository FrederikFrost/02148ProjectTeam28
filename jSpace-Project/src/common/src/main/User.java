package common.src.main;

public class User {
    private String Name;
    private int Id;

    public User(String Name) {
        this.Name = Name;
    }

    public String Name() {
        return Name;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int Id(){
        return Id;
    }

}
