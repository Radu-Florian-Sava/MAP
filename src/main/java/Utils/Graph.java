package Utils;

import Domain.Friendship;
import Domain.User;
import Service.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * clasa care foloseste algoritmi de grafuri pentru a calcula numarul de comunitati (componente conexe)
 * si determina cea mai sociabila comunitate (componenta conexa cu cel mai lung lant simplu elementar)
 */
public class Graph {
    private Service<Integer,User> userService;
    private Service<Integer,Friendship>  friendshipService;
    private Integer[][] matrice;
    private int contor;
    private int compConexe;
    Integer[] communityAssigner;
    HashMap<Integer, Integer> idContor;
    ArrayList<Integer> reverseHash;
    int maxLength;
    int componentMember;


    /**
     * @param userService service de utilizatori pe care il ia ca parametru pentru a extrage si asocia nodurile
     * @param friendshipService service de prietenii pe care il ia ca parametru pentru a extrage si asocia muchii
     */
    public Graph( Service<Integer,User>  userService, Service<Integer,Friendship> friendshipService) {
        this.userService=userService;
        this.friendshipService=friendshipService;
    }

    private void setMatrice(int value) throws SQLException {
        contor = 0;
        idContor = new HashMap<>();
        reverseHash = new ArrayList<>();

        for (User user : userService.getRecords()) {
            idContor.put(user.getId(), contor++);
            reverseHash.add(user.getId());
        }

        matrice = new Integer[contor][contor];

        for (int i = 0; i < contor; i++)
            for (int j = 0; j < contor; j++)
                matrice[i][j] = value;

        for (Friendship friendship : friendshipService.getRecords()) {
            int i = idContor.get(friendship.getSender());
            int j = idContor.get(friendship.getReceiver());
            matrice[i][j] = 1;
            matrice[j][i] = 1;
        }
    }

    /**
     * @return returneaza numarul de componente conexe care reprezinta numarul de comunitati
     * foloseste logica algoritmului Roy-Warshal
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public int connected() throws SQLException {
        setMatrice(0);
        compConexe = 0;

        for (int i = 0; i < contor; i++)
            for (int j = 0; j < contor; j++)
                if (i != j) {
                    for (int k = 0; k < contor; k++)
                        if (matrice[i][k] * matrice[k][j] == 1)
                            matrice[i][j] = 1;
                }

        communityAssigner = new Integer[contor];
        for (int i = 0; i < contor; i++)
            if (communityAssigner[i] == null) {
                communityAssigner[i] = compConexe;
                for (int j = 0; j < contor; j++) {
                    if (matrice[i][j] == 1)
                        communityAssigner[j] = compConexe;
                }
                compConexe++;
            }
        return compConexe;
    }

    /**
     * @param distanta       tablou unidimensional care reprezinta distanta maxima de la in varf initial dat la celelalte
     * @param queue          coada in care se pastreaza noduril rand pe rand pana ce vor fi analizate si eliminate
     * @param distanceMatrix matricea distantelor care va fi esentiala in calcularea drumului maxim
     */
    private void BFS_r(Integer[] distanta, ArrayList<Integer> queue, Integer[][] distanceMatrix) {
        while (!(queue.size() == 0)) {
            int x = queue.get(0);
            queue.remove(0);
            for (int i = 0; i < contor; i++)
                if (matrice[x][i] == 1) {

                    Integer[] aux_vector = matrice[x];
                    for (int j = 0; j < contor; j++)
                        matrice[x][j] = matrice[j][x] = 0;
                    int aux = distanta[i];

                    distanta[i] = distanta[x] + 1;
                    if (distanceMatrix[x][i] < distanta[i]) {
                        distanceMatrix[x][i] = distanceMatrix[i][x] = distanta[i];
                        if (distanta[i] > maxLength) {
                            maxLength = distanta[i];
                            componentMember = i;
                        }
                    }

                    queue.add(i);
                    BFS_r(distanta, queue, distanceMatrix);
                    distanta[i] = aux;
                    matrice[x] = aux_vector;
                    for (int j = 0; j < contor; j++)
                        matrice[j][x] = matrice[x][j];
                }
        }
    }

    /**
     * @param x indicele x al unui utilizator din obiectul de servicii
     * @return returneaza indicii asociati fiecarui utilizator din comunitatea din care face parte
     */
    private ArrayList<Integer> getComponent(int x) {
        int index = communityAssigner[x];
        ArrayList<Integer> community = new ArrayList<>();
        for (int i = 0; i < contor; i++) {
            if (communityAssigner[i] == index)
                community.add(i);
        }
        return community;
    }

    /**
     * @return returneaza cea mai sociabila comunitate (componenta conexa cu cel mai lung drum)
     * foloseste un algoritm de backtracking pentru a determina cel mai lung lant simplu elementar
     * incepand din fiecare nod pe rand si il memoreaza lungimea maxima si un nod din componenta conexa
     * caruia ii vom gasi componenta conexa si o vom returna ca si lista de utilizatori
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public ArrayList<User> socialComponent() throws SQLException {
        connected();
        maxLength = -1;
        componentMember = 0;
        Integer[][] distanceMatrix = new Integer[contor][contor];
        for (int i = 0; i < contor; i++)
            for (int j = 0; j < contor; j++)
                distanceMatrix[i][j] = Integer.MIN_VALUE;

        ArrayList<User> userArrayList = new ArrayList<>();

        setMatrice(0);

        for (int i = 0; i < contor; i++) {
            Integer[] distanta = new Integer[contor];

            for (int j = 0; j < contor; j++)
                distanta[j] = Integer.MIN_VALUE;
            distanta[i] = 0;

            ArrayList<Integer> coada = new ArrayList<>();
            coada.add(i);
            BFS_r(distanta, coada, distanceMatrix);
        }

        for (int i : getComponent(componentMember)) {
            userArrayList.add(userService.findRecord(reverseHash.get(i)));
        }

        return userArrayList;
    }

}
