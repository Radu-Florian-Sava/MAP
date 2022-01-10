package Repo;

import Domain.Identifiable;
import Exceptions.RepoException;
import Utils.TypeParser;

import java.io.*;

/**
 * @param <Id> id-ul generic al unui element din repozitoriu
 * @param <T>  instanta a clasei Identifiable
 *             tip de repozitoriu care retine elementele de tip T in memorie si in fisier
 *             elementele nu dispar la inchiderea aplicatiei
 */
@Deprecated
public class FileRepository<Id, T extends Identifiable<Id>> extends AbstractRepository<Id, T> {
    private String fileName;
    private TypeParser<Id, T> typeParser;

    /**
     * @param fileName   numele fisierului in care vor fi puse inregistrarile
     * @param typeParser numele obiectului care va genera elemente generice de tipul specificat
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
     * metoda privata care incarca inregistrarile din fisier in repozitoriu
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
     * @param string sirul de caractere care va fi scris in fisier
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
     * scrie toate intregistrarile din repozitoriu in fisier
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
     * @param t element de tip generic care va fi adaugat in repozitoriu
     * @throws RepoException arunca exceptie daca exista un element generic cu id-ul dat in repozitoriu
     * @return
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
     * @param id id-ul elementului de tip generic care va fi sters din repozitoriu
     * @return returneaza elementul generic sters
     * @throws RepoException arunca exceptie daca nu exista elementul cu id-ul dat in repozitoriu
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
     * @param id id-ul elementului care va fi modificat
     * @param t  element de tip generic care va inlocui elementul cu id-ul id din repozitoriu
     * @throws RepoException arunca exceptie daca nu exista elementul cu id-ul dat in repozitoriu
     */
    @Override
    public void update(Id id, T t) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        elems.replace(id, t);
        writeToFile();
    }
}
