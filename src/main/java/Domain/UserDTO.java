package Domain;

/**
 * class which contains more specific information about a user
 */
public class UserDTO {
    private final int id;
    private final String firstName;
    private final String surname;
    private final String username;


    /**
     * @param id the id of the user (unique)
     * @param firstName the first name of the user
     * @param surname the surname of the user
     * @param username the username (unique for each)
     */
    public UserDTO(int id, String firstName, String surname,String username) {
        this.id=id;
        this.firstName=firstName;
        this.surname=surname;
        this.username=username;
    }

    /**
     * @return the id of the user (unique)
     */
    public int getId(){
        return id;
    }

    /**
     * @return the first name of the user
     */
    public String getFirstName(){
        return this.firstName;
    }

    /**
     * @return the surname of the user
     */
    public String getSurname(){
        return this.surname;
    }

    /**
     * @return the username (unique for each)
     */
    public String getUsername(){return  this.username;}

    /**
     * @return the object as a string: username/firstName surname
     */
    @Override
    public String toString() {
        return  username +" / "+ firstName + " " + surname ;
    }
}
