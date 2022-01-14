package Repo;

import Domain.Identifiable;
import Exceptions.RepoException;
import Utils.TypeParser;
import java.io.*;

/**
 * @param <Id> generic ID of an element
 * @param <T>  instance of Identifiable
 *             the elements have the generic type T
 *             IMPORTANT: since the elements are saved in a file
 *                        the data won't disappear when the application in closed
 */
@Deprecated
public class FileRepository<Id, T extends Identifiable<Id>> extends AbstractRepository<Id, T> {
    private String fileName;
    private TypeParser<Id, T> typeParser;

    /**
     * @param fileName the name of the file containing the elements
     * @param typeParser an object specialised for creating elements which the repository contains
     */
    public FileRepository(String fileName, TypeParser<Id, T> typeParser) {
        super();
        this.typeParser = typeParser;
        this.fileName = fileName;
        File f = new File(fileName);
        try {
            f.createNewFile();
            loadFromFile();
        } catch (IOException ignored) {
        }
    }

    /**
     * private method which loads all the elements from the file in memory
     */
    private void loadFromFile() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    bufferedReader.close();
                    return;
                }
                String[] atribute = line.split(";");
                T t = typeParser.parse(atribute);
                if (t == null) {
                    bufferedReader.close();
                    return;
                }
                elems.put(t.getId(), t);
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * @param string which will be written in the file
     */
    private void appendToFile(String string) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));
            bufferedWriter.write(string);
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * write in the file all the elements which are in memory
     */
    private void writeToFile() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            for (T elem : elems.values()) {
                bufferedWriter.write(elem.toString());
            }
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * @param t the element which will be added
     * @throws RepoException if there already is an element with the given ID contained by the repository
     * @return the ID of the given element
     */
    @Override
    public Id add(T t) throws RepoException {
        if (elems.containsKey(t.getId()))
            throw new RepoException("Element existent\n");
        elems.put(t.getId(), t);
        appendToFile(t.toString());
        return t.getId();
    }

    /**
     * @param id of the element which will be deleted
     * @return the deleted element
     * @throws RepoException if there is no element with the given ID in the repository
     */
    @Override
    public T delete(Id id) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        T elem = elems.remove(id);
        writeToFile();
        return elem;
    }

    /**
     * @param id of the element which will be modified
     * @param t  element with the same ID which will replace the element with the given ID
     * @throws RepoException if there is no element with the given ID in the repository
     */
    @Override
    public void update(Id id, T t) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        elems.replace(id, t);
        writeToFile();
    }
}
