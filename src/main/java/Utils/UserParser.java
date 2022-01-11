package Utils;

import Domain.User;

/**
 * class which creates a new USER
 * @deprecated only to be used with non-GUI interfaces
 */
@Deprecated
public class UserParser implements TypeParser<Integer, User> {

    /**
     * @param attributes a list of attributes of the element that it's going to create
     *        3 attributes - id(int)
     *                       firstName(String)
     *                       surname(String)
     * @return a User object which has the associated characteristics as the parameters given by the list
     */
    @Override
    public User parse(String[] attributes) {
        if (attributes.length != 3)
            return null;
        int id = Integer.parseInt(attributes[0]);
        String firstName = attributes[1];
        String surname = attributes[2];
        return new User(id, firstName, surname);
    }
}
