package Utils;

import Domain.Friendship;

/**
 * clasa care creeaza o noua relatie de prietenie
 * @deprecated this class must 
 */
@Deprecated
public class FriendshipParser implements TypeParser<Integer, Friendship> {

    /**
     * @param attributes o lista de atribute de tip sir de caractere a elementului pe care il va genera
     * @return un obiect de tip relatie de prietenie cu atribute tranformate din sir de caractere in cele necesare
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
