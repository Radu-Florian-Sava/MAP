package Utils;

import Domain.Friendship;
import Domain.User;
import Service.MergedService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @deprecated visit Graph instead
 * class which uses graph theory algorthm in order to calculate the number of communities (conex components)
 * and determine the most sociable community (the conex component which has the longest path)
 */
@Deprecated
public class MergeGraph {
    private MergedService service;
    private Integer[][] matrice;
    private int contor;
    private int compConexe;
    Integer[] communityAssigner;
    HashMap<Integer, Integer> idContor;
    ArrayList<Integer> reverseHash;
    int maxLength;
    int componentMember;

    /**
     * @param service object of type service for which is going to find out the statistics described as the class header
     */
    public MergeGraph(MergedService service) {
        this.service = service;
    }

    /**
     * @param value sets every value of the matrix which doesn't represent an edge with the given value
     * @throws SQLException if it cannot connect to the database
     */
    private void setMatrice(int value) throws SQLException {
        contor = 0;
        idContor = new HashMap<>();
        reverseHash = new ArrayList<>();

        for (User user : service.getUsers()) {
            idContor.put(user.getId(), contor++);
            reverseHash.add(user.getId());
        }

        matrice = new Integer[contor][contor];

        for (int i = 0; i < contor; i++)
            for (int j = 0; j < contor; j++)
                matrice[i][j] = value;

        for (Friendship friendship : service.getFriendships()) {
            int i = idContor.get(friendship.getSender());
            int j = idContor.get(friendship.getReceiver());
            matrice[i][j] = 1;
            matrice[j][i] = 1;
        }
    }

    /**
     * @return the number of conex components/ communities
     * uses the Roy-Warshal algorithm logic
     * @throws SQLException if it cannot connect to the database
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
     * @param distanta       unidimensional array which contains the distance from the initial vertex to the others
     * @param queue          in which we keep our vertexes once we discover them
     * @param distanceMatrix distance matrix used in order to calculate the maximum distance
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
     * @param x the index of a user from the Service object
     * @return the indexes of the other users which are in the same community
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
     * @return the most sociable community (the conex component with the longest path)
     * uses backtracking by trying to find every path and get the max value
     * @throws SQLException if it cannot connect to the database
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
            userArrayList.add(service.findUser(reverseHash.get(i)));
        }

        return userArrayList;
    }

}
