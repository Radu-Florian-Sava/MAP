package Repo;

import Domain.User;

/**
 *  specialised repository which contains users in memory
 */
@Deprecated
public class InMemoryUserRepository extends InMemoryRepository<Integer, User> {
    /**
     *  standard constructor
     */
    public InMemoryUserRepository() {
        super();
    }
}
