package Repo;

import Domain.Friendship;
import Utils.TypeParser;

/**
 *  repozitoriu specializat care retine date despre utilizatori in fisier
 */
@Deprecated
public class FileFriendshipRepository extends FileRepository<Integer, Friendship> {
    /**
     * @param fileName   numele fisierului in care vor fi puse inregistrarile
     * @param typeParser numele obiectului care va genera elemente generice de tipul specificat
     */
    public FileFriendshipRepository(String fileName, TypeParser<Integer, Friendship> typeParser) {
        super(fileName, typeParser);
    }
}
