package Utils;

import Domain.User;

/**
 * clasa cara creeaza un nou utilizator
 */
@Deprecated
public class UserParser implements TypeParser<Integer, User> {

    /**
     * @param attributes o lista de atribute de tip sir de caractere a elementului pe care il va genera
     * @return un obiect de tip utilizator cu atribute tranformate din sir de caractere in cele necesare
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
