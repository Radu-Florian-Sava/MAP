package Repo;

import Domain.Identifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <Id> id-ul generic al unui element din repozitoriu
 * @param <T>  instanta a clasei Identifiable
 *             implementeaza partial interfata Repository
 */
@Deprecated
public abstract class AbstractRepository<Id, T extends Identifiable<Id>> implements Repository<Id, T> {

    protected Map<Id, T> elems;

    /**
     * constructor care initializeaza campul protejat elems cu un nou dictionar cu tabela de dispersie
     */
    public AbstractRepository() {
        this.elems = new HashMap<>();
    }

    /**
     * @param id id-ul elementului care va fi cautat in repozitoriu
     * @return returneaza elementul generic cu id-ul dat ca parametru sau null daca nu exista in repozitoriu
     */
    @Override
    public T find(Id id) {
        if (!elems.containsKey(id))
            return null;
        return elems.get(id);
    }

    /**
     * @return returneaza o lista iterabila cu elementele generice din repozitoriu
     */
    @Override
    public Iterable<T> getAll() {
        return elems.values();
    }

    /**
     * @return returneaza numarul de elemente din repozitoriu
     */
    @Override
    public int size() {
        return elems.size();
    }
}
