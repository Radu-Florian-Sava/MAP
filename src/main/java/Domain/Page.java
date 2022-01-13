package Domain;

import java.util.List;

public class Page {

    private UserDTO mainUser;
    private List<UserDTO> friends;
    private List<FriendshipDTO> friendshipRequests;
    private List<Message> messages;
    private UserDTO messageUser;
    private UserDTO friendshipUser;
    private FriendshipDTO friendshipFocus;
    private int idToReply;

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
    }

    public void setMessageUser(UserDTO messageUser) {
        this.messageUser = messageUser;
    }

    public void setFriendshipUser(UserDTO friendshipUser) {
        this.friendshipUser = friendshipUser;
    }

    public void setFriendshipFocus(FriendshipDTO friendshipFocus) {
        this.friendshipFocus = friendshipFocus;
    }

    public void setIdToReply(int idToReply) {
        this.idToReply = idToReply;
    }

    public UserDTO getMessageUser() {
        return messageUser;
    }

    public UserDTO getFriendshipUser() {
        return friendshipUser;
    }

    public FriendshipDTO getFriendshipFocus() {
        return friendshipFocus;
    }

    public int getIdToReply() {
        return idToReply;
    }

    public UserDTO getMainUser() {
        return mainUser;
    }

    public List<UserDTO> getFriends() {
        return friends;
    }

    public List<FriendshipDTO> getFriendshipRequests() {
        return friendshipRequests;
    }

    public List<Message> getMessages() {
        return messages;
    }

}