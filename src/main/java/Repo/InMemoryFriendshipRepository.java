package Repo;

import Domain.Friendship;

/**
 *  repozitoriu specializat care retine date despre relatiile de prietenie in memorie ca si HashMap
 */
@Deprecated
public class InMemoryFriendshipRepository extends InMemoryRepository<Integer, Friendship> {
    /**
     * constructor standard
     */
    public InMemoryFriendshipRepository() {
        super();
    }

}
