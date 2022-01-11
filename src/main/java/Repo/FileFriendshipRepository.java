package Repo;

import Domain.Friendship;
import Utils.TypeParser;

/**
 *  specialised repository which contains friendship relationships in a file
 */
@Deprecated
public class FileFriendshipRepository extends FileRepository<Integer, Friendship> {
    /**
     * @param fileName the name of the file containing the elements
     * @param typeParser an object specialised for creating elements of the class Friendship
     */
    public FileFriendshipRepository(String fileName, TypeParser<Integer, Friendship> typeParser) {
        super(fileName, typeParser);
    }
}
