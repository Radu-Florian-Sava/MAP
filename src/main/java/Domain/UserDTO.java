package Domain;

public class UserDTO {
    private final int id;
    private final String firstName;
    private final String surname;
    private final String username;


    public UserDTO(int id, String firstName, String surname,String username) {
        this.id=id;
        this.firstName=firstName;
        this.surname=surname;
        this.username=username;
    }

    public int getId(){
        return id;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getSurname(){
        return this.surname;
    }

    public String getUsername(){return  this.username;}

    @Override
    public String toString() {
        return  username +" / "+ firstName + " " + surname ;
    }
}
