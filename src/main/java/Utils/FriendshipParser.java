package Utils;

import Domain.Friendship;

/**
 * class which creates a friendship relationship
 * @deprecated this class must only be used with a non-GUI
 */
@Deprecated
public class FriendshipParser implements TypeParser<Integer, Friendship> {

    /**
     * @param attributes a list of attributes which the created element will have
     * @return a Friendship class object having the given attributes
     */
    @Override
    public Friendship parse(String[] attributes) {
        if (attributes.length != 3)
            return null;
        int id = Integer.parseInt(attributes[0]);
        int one = Integer.parseInt(attributes[1]);
        int two = Integer.parseInt(attributes[2]);
        return new Friendship(id, one, two);
    }
}
