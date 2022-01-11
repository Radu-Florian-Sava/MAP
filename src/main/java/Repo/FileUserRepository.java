package Repo;

import Domain.User;
import Utils.TypeParser;

/**
 * specialised repository which contains users in a file
 */
@Deprecated
public class FileUserRepository extends FileRepository<Integer, User> {

    /**
     * @param fileName the name of the file containing the elements
     * @param typeParser an object specialised for creating elements of the class User
     */
    public FileUserRepository(String fileName, TypeParser<Integer, User> typeParser) {
        super(fileName, typeParser);
    }
}
