package Domain;

import java.util.List;

/**
 * contains data that the current user uses regularly
 */
public class Page {

    private final UserDTO mainUser;
    private List<UserDTO> friends;
    private List<FriendshipDTO> friendshipRequests;
    private List<Message> messages;
    private UserDTO messageUser;
    private UserDTO friendshipUser;
    private FriendshipDTO friendshipFocus;
    private int idToReply;
    private int pageNumber;

    /**
     * @param mainUser the user who logged in
     * @param friends a list of the user's friends
     * @param friendshipRequests a list of the user's friendships
     * @param messages a list of the user's messages
     */
    public Page(UserDTO mainUser,
                List<UserDTO> friends,
                List<FriendshipDTO> friendshipRequests,
                List<Message> messages){
        this.mainUser=mainUser;
        this.friends=friends;
        this.friendshipRequests = friendshipRequests;
        this.messages= messages;
        this.messageUser=null;
        this.friendshipUser=null;
        this.friendshipFocus=null;
        this.idToReply=-1;
        this.pageNumber=0;
    }

    /**
     * @param pageNumber the curren page displayed
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @param messageUser the user with which the logged user converses
     */
    public void setMessageUser(UserDTO messageUser) {
        this.messageUser = messageUser;
    }

    /**
     * @param friendshipUser the user with which the logged user's relationship is about to change
     */
    public void setFriendshipUser(UserDTO friendshipUser) {
        this.friendshipUser = friendshipUser;
    }

    /**
     * @param friendshipFocus the friendship relationship from which we are going to extract information
     */
    public void setFriendshipFocus(FriendshipDTO friendshipFocus) {
        this.friendshipFocus = friendshipFocus;
    }

    /**
     * @param idToReply the id of the message we are going to reply
     */
    public void setIdToReply(int idToReply) {
        this.idToReply = idToReply;
    }

    /**
     * @return the user with which the logged user converses
     */
    public UserDTO getMessageUser() {
        return messageUser;
    }

    /**
     * @return the user with which the logged user's relationship is about to change
     */
    public UserDTO getFriendshipUser() {
        return friendshipUser;
    }

    /**
     * @return the friendship relationship from which we are going to extract information
     */
    public FriendshipDTO getFriendshipFocus() {
        return friendshipFocus;
    }

    /**
     * @return the id of the message we are going to reply
     */
    public int getIdToReply() {
        return idToReply;
    }

    /**
     * @return the user who logged in
     */
    public UserDTO getMainUser() {
        return mainUser;
    }

    /**
     * @return a list of the user's friends
     */
    public List<UserDTO> getFriends() {
        return friends;
    }

    /**
     * @return a list of the user's friendships
     */
    public List<FriendshipDTO> getFriendshipRequests() {
        return friendshipRequests;
    }

    /**
     * @return a list of the user's messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * @return the curren page displayed
     */
    public int getPageNumber() {
        return pageNumber;
    }

}