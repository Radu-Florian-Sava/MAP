package Repo;

import Domain.User;
import Utils.TypeParser;

/**
 *  repozitoriu specializat care retine date despre utilizatori in fisier
 */
@Deprecated
public class FileUserRepository extends FileRepository<Integer, User> {

    /**
     * @param fileName   numele fisierului in care vor fi puse inregistrarile
     * @param typeParser numele obiectului care va genera elemente generice de tipul specificat
     */
    public FileUserRepository(String fileName, TypeParser<Integer, User> typeParser) {
        super(fileName, typeParser);
    }
}
