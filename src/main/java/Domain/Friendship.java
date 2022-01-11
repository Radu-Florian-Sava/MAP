package Domain;

import Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * creates a friendship relationship between the users(see User class)
 */
public class Friendship implements Identifiable<Integer> {
    private int id;
    private int sender, receiver;
    private Date date;
    private int friendshipRequest;

    /**
     * @return the date when the friendship was created
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param id makes each friendship uniquely identifiable
     * @param sender the person who sent the friendship request
     * @param receiver the person who received the friendship request
     * @param date the date when the friendship was created
     * @param friendshipRequest integer: 0 - PENDING, 1 - REJECTED, 2 - ACCEPTED
     */
    public Friendship(int id, int sender, int receiver, Date date, int friendshipRequest) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.friendshipRequest = friendshipRequest;
    }

    /**
     * @param id makes each friendship uniquely identifiable
     * @param sender the person who sent the friendship request
     * @param receiver the person who received the friendship request
     * @param date the date when the friendship was created
     * @deprecated only to be used with a non-GUI
     */
    @Deprecated
    public Friendship(int id, int sender, int receiver, Date date) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.friendshipRequest = 0;
    }

    /**
     * @param sender the person who sent the friendship request
     * @param receiver the person who received the friendship request
     * @deprecated only to be used with non-GUI, leaves INCOMPLETE attributes(for example data)
     *             WARNING: the ID is given as 1 by default
     */
    @Deprecated
    public Friendship(int sender, int receiver) {
        this.id = 1;
        this.sender = sender;
        this.receiver = receiver;
        this.date =  new Date(System.currentTimeMillis());
        this.friendshipRequest =Constants.PENDING_FRIENDSHIP;
    }

    /**
     * @param id makes each friendship uniquely identifiable
     * @param sender the person who sent the friendship request
     * @param receiver the person who received the friendship request
     */
    public Friendship(int id, int sender, int receiver) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.date = new Date(System.currentTimeMillis());
    }

    /**
     * @param o element to be compared
     * @return true if o and this element are equal, false otherwise
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
     * @return the date when the friendship was created as yyyy-mm-dd
     */
    public String getStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * @return id of the person who sent the friendship request
     */
    public int getSender() {
        return sender;
    }

    /**
     * @return the combination as follows: id;senderID;receiverID\n
     */
    @Override
    public String toString() {
        return this.getId() + ";" + sender + ";" + receiver + "\n";
    }

    /**
     * @return id of the person who received the friendship request
     */
    public int getReceiver() {
        return receiver;
    }

    /**
     * @return the id of the friendship request
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return the friendship request 'flag' ( 0 - pending, 1 - refused, 2 - accepted)
     */
    public int getFriendshipRequest() {
        return friendshipRequest;
    }

    /**
     * @param friendshipRequest sets the friendship request 'flag' to a different value
     */
    public void setFriendshipRequest(int friendshipRequest) {
        this.friendshipRequest = friendshipRequest;
    }
}
