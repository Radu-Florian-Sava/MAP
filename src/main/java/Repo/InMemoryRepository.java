package Repo;

import Domain.Identifiable;
import Exceptions.RepoException;

/**
 * @param <Id> id-ul generic al unui element din repozitoriu
 * @param <T>  instanta a clasei Identifiable
 *             tip de repozitoriu care retine elementele de tip T in memorie
 *             elementele dispar la inchiderea aplicatiei
 */
@Deprecated
public class InMemoryRepository<Id, T extends Identifiable<Id>> extends AbstractRepository<Id, T> {

    /**
     * constructor standard
     */
    public InMemoryRepository() {
        super();
    }

    /**
     * @param t element de tip generic care va fi adaugat in repozitoriu
     * @throws RepoException arunca exceptie daca exista un element generic cu id-ul dat in repozitoriu
     * @return
     */
    @Override
    public Id add(T t) throws RepoException {
        if (elems.containsKey(t.getId()))
            throw new RepoException("Element existent\n");
        elems.put(t.getId(), t);
        return t.getId();
    }

    /**
     * @param id id-ul elementului de tip generic care va fi sters din repozitoriu
     * @return returneaza elementul generic sters
     * @throws RepoException arunca exceptie daca nu exista elementul cu id-ul dat in repozitoriu
     */
    @Override
    public T delete(Id id) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        return elems.remove(id);
    }

    /**
     * @param id id-ul elementului care va fi modificat
     * @param t  element de tip generic care va inlocui elementul cu id-ul id din repozitoriu
     * @throws RepoException arunca exceptie daca nu exista elementul cu id-ul dat in repozitoriu
     */
    @Override
    public void update(Id id, T t) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        elems.replace(id, t);
    }
}
