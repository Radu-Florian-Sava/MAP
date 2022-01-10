package Utils;

import Domain.Identifiable;

/**
 * @param <Id> id-ul generic al unui element
 * @param <T>  instanta a clasei Identifiable
 *             clasa care creeaza un anumit tip de obiect folosind strategy pattern
 */
@Deprecated
public interface TypeParser<Id, T extends Identifiable<Id>> {
    /**
     * @param attributes o lista de atribute de tip sir de caractere a elementului pe care il va genera
     * @return o inregistrare de tipul t cu atributele specificate
     */
    T parse(String[] attributes);
}
