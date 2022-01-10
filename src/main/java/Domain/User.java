package Domain;

import java.util.Objects;

/**
 * caracterizeaza un utilizator al aplicatiei prin id, nume si prenume
 */
public class User implements Identifiable<Integer> {

    private int id;
    private String firstName;
    private String surname;
    private String username;
    private String password;

    public User(String firstName, String surname) {
        this.id = 1;
        this.firstName = firstName;
        this.surname = surname;
    }

    public User(String firstName, String surname, String username, String password) {
        this.id = 1;
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    /**
     * @return returneaza id-ul de tipul numar intreg al unui utilizator
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return returneaza numele de tipul sir de caractere al unui utilizator
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return returneaza prenumele de tipul sir de caractere al unui utilizator
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param id        este id-ul utilizatorului
     * @param firstName este numele utilizatorului
     * @param surname   este prenumele utilizatorului
     */
    public User(int id, String firstName, String surname) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
    }

    public User(int id, String firstName, String surname, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    /**
     * @param firstName atribuie numelui utilizatorului parametrul de tip sir de caractere dat
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param surname atribuie prenumelui utilizatorului parametrul de tip sir de caractere dat
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return returneaza inregistrarea ca sir de caractere de forma id;nume;prenume\n
     */
    @Override
    public String toString() {
        return this.getId() + ";" + firstName + ";" + surname + "\n";
    }

    /**
     * @param o elementul pe care il comparam cu utilizatorul in cauza
     * @return returneaza true daca sunt egale sau false in caz contrar
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }
}
