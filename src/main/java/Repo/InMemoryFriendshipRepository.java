package Repo;

import Domain.Friendship;

/**
 *  specialised repository which contains friendship relationships in memory
 */
@Deprecated
public class InMemoryFriendshipRepository extends InMemoryRepository<Integer, Friendship> {
    /**
     * standard constructor
     */
    public InMemoryFriendshipRepository() {
        super();
    }

}
