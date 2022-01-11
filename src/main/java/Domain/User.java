package Domain;

import java.util.Objects;

/**
 * class which contains the information of a user
 */
public class User implements Identifiable<Integer> {

    private int id;
    private String firstName;
    private String surname;
    private String username;
    private String password;

    /**
     * @param firstName the first name of the user
     * @param surname the surname of the user
     */
    @Deprecated
    public User(String firstName, String surname) {
        this.id = 1;
        this.firstName = firstName;
        this.surname = surname;
    }

    /**
     * @param firstName the first name of the user
     * @param surname the surname of the user
     * @param username the username (unique for each)
     * @param password the user password (or rather hashed password)
     */
    @Deprecated
    public User(String firstName, String surname, String username, String password) {
        this.id = 1;
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    /**
     * @return the id of the user
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the surname of the user
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param id the user id (unique)
     * @param firstName the first name of the user
     * @param surname the surname of the user
     */
    @Deprecated
    public User(int id, String firstName, String surname) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
    }

    /**
     * @param id the id of the user (unique)
     * @param firstName the first name of the user
     * @param surname the surname of the user
     * @param username the username (unique for each)
     * @param password the user password (or rather hashed password)
     */
    public User(int id, String firstName, String surname, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    /**
     * @param firstName the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param surname the surname of the user
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return the username (unique for each)
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the user password (or rather hashed password)
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the object as a string as:  id;firstName;surname\n
     */
    @Override
    public String toString() {
        return this.getId() + ";" + firstName + ";" + surname + "\n";
    }

    /**
     * @param o element to be compared
     * @return true if o is equal with this object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }
}
