package Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * caracterizeaza o relatie de prietenie din aplicatie prin id si doua id-uri care corespund unor utilizatori ai aplicatiei
 */
public class Friendship implements Identifiable<Integer> {
    private int id;
    private int sender, receiver;

    public Date getDate() {
        return date;
    }

    private Date date;
    private int friendship_request;

    public Friendship(int id, int sender, int receiver, Date date, int friendship_request) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.friendship_request = friendship_request;
    }

    public Friendship(int id, int sender, int receiver, int friendship_request) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.friendship_request = friendship_request;
    }

    public Friendship(int id, int sender, int receiver, Date date) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.friendship_request = 0;
    }

    public Friendship(int sender, int receiver) {
        this.id = 1;
        this.sender = sender;
        this.receiver = receiver;
        this.date =  new Date(System.currentTimeMillis());
    }

    /**
     * @param id  este id-ul prieteniei intre doi utilizatori
     * @param sender este id-ul unuia dintre utilizatori
     * @param receiver este id-ul celuilalt utilizator
     */
    public Friendship(int id, int sender, int receiver) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date(System.currentTimeMillis());
    }

    /**
     * @param o elementul pe care il comparam cu relatia de prietenie in cauza
     * @return returneaza true daca sunt egale sau false in caz contrar
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship that)) return false;
        boolean equal = Objects.equals(getId(), that.getId());
        if (equal)
            return true;
        equal = (Objects.equals(((Friendship) o).sender, this.sender) && Objects.equals(((Friendship) o).receiver, this.receiver)) ||
                (Objects.equals(((Friendship) o).sender, this.receiver) && Objects.equals(((Friendship) o).receiver, this.sender));
        return equal;
    }

    /**
     * @return data de la care doi utilizatori sunt prieteni ca si string sub forma yyyy-mm-dd
     */
    public String getStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * @return returneaza id-ul "primului" utilizator
     */
    public int getSender() {
        return sender;
    }

    /**
     * @return returneaza inregistrarea ca sir de caractere de forma id;idUtilizator1;idUtilizator2\n
     */
    @Override
    public String toString() {
        return this.getId() + ";" + sender + ";" + receiver + "\n";
    }

    /**
     * @return returneaza id-ul celui "de-al doilea" utilizator
     */
    public int getReceiver() {
        return receiver;
    }

    /**
     * @return returneaza id-ul relatiei de prietenie
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return returneaza flagul friendship request ( 0 - trimisa, 1 - refuzata, 2 - acceptata)
     */
    public int getFriendship_request() {
        return friendship_request;
    }

    /**
     * @param friendship_request seteaza flagului friendship_request valoarea data
     */
    public void setFriendship_request(int friendship_request) {
        this.friendship_request = friendship_request;
    }
}
