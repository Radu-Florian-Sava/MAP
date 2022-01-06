package com.example.lab_gui;

import Control.Controller;
import Domain.Friendship;
import Domain.FriendshipDTO;
import Domain.User;
import Domain.UserDTO;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HelloController {


    @FXML
    public HBox changeStatusSection;
    public HBox containingWindow;

    // pseudo - fx: id(s)
    private Controller controller = Controller.getInstance();
    private UserDTO currentUserControl = null;
    private UserDTO passiveUserControl = null;
    private FriendshipDTO selectedFriendship = null;

    // messages
    @FXML
    private TableView<String> messageTable;

    @FXML
    private TableColumn<String,String> messageColumn;

    @FXML
    private Button sendMessageButton;

    @FXML
    private VBox messageFunctionality;

    @FXML
    private TextField messageBody;

    // all users
    @FXML
    private Alert messageBox;
    @FXML
    private TextField selectedUser;
    @FXML
    private TableColumn<UserDTO, String> userID;
    @FXML
    private TableColumn<UserDTO, String> userSurname;
    @FXML
    private TableColumn<UserDTO, String> userFirstName;
    @FXML
    private TableView<UserDTO> userTable;
    @FXML
    private Button changeFriendStatus;


    //hiddenUsers
    @FXML
    private Label passiveUserName;
    @FXML
    private TableColumn<UserDTO, String> hiddenID;
    @FXML
    private TableColumn<UserDTO, String> hiddenSurname;
    @FXML
    private TableColumn<UserDTO, String> hiddenFirstName;
    @FXML
    private TableView<UserDTO> hiddenTable;


    // friendship table
    @FXML
    private TableView<FriendshipDTO> friendshipTable;

    @FXML
    private TableColumn<FriendshipDTO, String> relationColumn;

    @FXML
    private TableColumn<FriendshipDTO, String> userColumn;

    @FXML
    private TableColumn<FriendshipDTO, String> statusColumn;

    @FXML
    public void initialize() {
        load();
        setController(Controller.getInstance());
    }

    private void setController(Controller controller) {
        this.controller = controller;
    }

    private void loadFriendships() throws SQLException {

        List<FriendshipDTO> friendships = null;

        if (currentUserControl != null) {
            friendships = controller.getAllTypesOfFriendshipsOf(currentUserControl.getId());

            friendshipTable.setItems(FXCollections.observableList(friendships));
        }

        if (friendships == null || friendships.isEmpty()) {
            friendshipTable.setPlaceholder(new Label("There are no friendships to show \n for now :'("));
        }

    }

    private void loadUsers() throws SQLException {
        List<UserDTO> userDTOS = controller.getAllUsersDTO();
        userTable.setItems(FXCollections.observableList(userDTOS));
    }

    private void loadFriends() throws SQLException {

        if (currentUserControl != null) {

            List<UserDTO> hiddenUserDTO;

            hiddenTable.setVisible(true);
            changeStatusSection.setVisible(true);
            changeFriendStatus.setText("Unfriend");

            hiddenUserDTO = controller.getAllUsersDTO().stream().filter(
                    x -> {
                        try {
                            return controller.areFriends(currentUserControl.getId(), x.getId());
                        } catch (SQLException ignored) {
                        }
                        return false;
                    }
            ).toList();

            hiddenTable.setItems(FXCollections.observableList(hiddenUserDTO));
            if (hiddenUserDTO.isEmpty()) {
                hiddenTable.setPlaceholder(new Label("For the moment there are no friends to show \n" +
                        "Try again after you make some :P"));
                passiveUserControl = null;
            }
        }
    }

    private void loadBefriendable() throws SQLException {
        if (currentUserControl != null) {
            List<UserDTO> hiddenUserDTO;

            hiddenTable.setVisible(true);
            changeStatusSection.setVisible(true);
            changeFriendStatus.setText("Send request");

            hiddenUserDTO = controller.getAllUsersDTO().stream().filter(
                    x -> {
                        try {
                            return !controller.areFriends(currentUserControl.getId(), x.getId()) &&
                                    x.getId() != currentUserControl.getId() &&
                                    !controller.getSentFriendships(currentUserControl.getId()).stream().filter(
                                                    y -> y.getFriendship_request() == 0 || y.getFriendship_request() == 2
                                            ).toList()
                                            .contains(new Friendship(0, currentUserControl.getId(), x.getId()));
                        } catch (SQLException ignored) {
                        }
                        return false;
                    }
            ).toList();

            hiddenTable.setItems(FXCollections.observableList(hiddenUserDTO));

            if (hiddenUserDTO.isEmpty()) {
                hiddenTable.setPlaceholder(new Label("It looks like you've got lots of friends \n" +
                        "There's no one to add at the moment XD"));
                passiveUserControl = null;
            }
        }
    }

    private void hideRelationsMenu() throws SQLException {
        changeStatusSection.setVisible(false);
        hiddenTable.setVisible(false);
        changeFriendStatus.setText("Unfriend\\Befriend");
        passiveUserControl = null;
        passiveUserName.setText("Nume Prenume");
        loadFriendships();
    }

    private void hideMessages(){
        messageFunctionality.setVisible(false);
        passiveUserControl=null;
    }

    private void showMessages(){
        if(passiveUserControl!=null)
        {
            messageFunctionality.setVisible(true);
            messageColumn.setText("Messages with " +
                    passiveUserControl.getFirstName() +
                    " " +
                    passiveUserControl.getSurname()
            );
        }
    }

    private Iterable<String> getMessages() throws SQLException {
        if(currentUserControl!=null && passiveUserControl!=null){
            // return controller.getMessagesBy2Users(currentUserControl.getId(),passiveUserControl.getId());
            return null;
        }
        return null;
    }

    public void load() {
        try {

            relationColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStringRelation()));
            userColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSecond_name()));
            statusColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStatus()));


            userID.setCellValueFactory((data) -> new SimpleStringProperty(Integer.toString(data.getValue().getId())));
            userFirstName.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getFirstName()));
            userSurname.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSurname()));

            hiddenID.setCellValueFactory((data) -> new SimpleStringProperty(Integer.toString(data.getValue().getId())));
            hiddenFirstName.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getFirstName()));
            hiddenSurname.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSurname()));

            messageColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue()));

            loadFriendships();
            loadUsers();


        } catch (SQLException ignored) {}
    }

    @FXML
    private void selectedUser(MouseEvent mouseEvent) throws SQLException {
        if (currentUserControl != null) {
            selectedUser.setText(
                    currentUserControl.toString()
            );
            loadFriendships();
            hideRelationsMenu();
            hideMessages();
        }
    }

    @FXML
    private void setPassiveUser(MouseEvent mouseEvent) {
        passiveUserControl = hiddenTable.getSelectionModel().getSelectedItem();
        if (passiveUserControl != null) {
            passiveUserName.setText(
                    passiveUserControl.toString()
            );
        }
    }

    public void revealPotentialFriends(ActionEvent actionEvent) throws SQLException {
        loadBefriendable();

    }

    public void revealCurrentFriends(ActionEvent actionEvent) throws SQLException {
        loadFriends();
    }

    public void changeStatusOfFriendship(ActionEvent actionEvent) throws ValidateException, BusinessException, SQLException, RepoException {
        if (passiveUserControl != null && currentUserControl != null) {

            if (Objects.equals(changeFriendStatus.getText(), "Send request")) {
                controller.sendFriendship(currentUserControl.getId(), passiveUserControl.getId());
                hideRelationsMenu();
                hideMessages();
            } else if (Objects.equals(changeFriendStatus.getText(), "Unfriend")) {
                Iterable<Friendship> friendshipList = controller.getFriendshipsOf(currentUserControl.getId());
                friendshipList.forEach(friendship -> {
                            if (friendship.getOne() == passiveUserControl.getId() ||
                                    friendship.getTwo() == passiveUserControl.getId()) {
                                try {
                                    controller.deleteFriendship(friendship.getId());
                                    hideRelationsMenu();
                                    hideMessages();
                                } catch (RepoException | SQLException ignored) {
                                }
                            }
                        }
                );

            }

        }

    }

    private void initMessageBox() {
        messageBox = new Alert(Alert.AlertType.INFORMATION);
        messageBox.setTitle("Warning");
        messageBox.setHeaderText("You've really done it now");
        messageBox.setContentText("This is a secret message, congratulations! ");
    }

    public void summonMessageBox(String newText) {
        if (messageBox == null)
            initMessageBox();
        messageBox.setContentText(newText);
        messageBox.show();
    }

    public void tryToFindUserByID(KeyEvent keyEvent) throws SQLException {
        if (keyEvent.getCode() != KeyCode.ENTER)
            return;
        try {
            int tempId = Integer.parseInt(selectedUser.getText());
            User tempUser = controller.findUser(tempId);
            if (tempUser != null)
                selectedUser.setText(new UserDTO(tempId, tempUser.getFirstName(), tempUser.getSurname()).toString());
            else
                throw new NumberFormatException("This is not the user that you are looking for");
        } catch (NumberFormatException numberFormatException) {
            summonMessageBox("Try searchig by id number");
        }
    }

    public void selectFriendship(MouseEvent mouseEvent) throws SQLException, ValidateException, BusinessException, RepoException {
        selectedFriendship = friendshipTable.getSelectionModel().getSelectedItem();

        if (selectedFriendship != null &&
                Objects.equals(selectedFriendship.getStatus(), "Waiting...")
        ) {
            Alert acceptOrReject = new Alert(Alert.AlertType.CONFIRMATION);
            acceptOrReject.setGraphic(acceptOrReject.getDialogPane().getGraphic());
            if (selectedFriendship.getRelation() == 1) {

                acceptOrReject.setTitle("Do you confirm this friendship request ?");
                acceptOrReject.setContentText("The user " + selectedFriendship.getSecond_name() +
                        " has sent you a friend request");

                hideRelationsMenu();
                ButtonType acceptRequest = new ButtonType("Accept");
                ButtonType rejectRequest = new ButtonType("Reject");
                ButtonType ignore = new ButtonType("Ignore for now", ButtonBar.ButtonData.CANCEL_CLOSE);

                acceptOrReject.getButtonTypes().setAll(acceptRequest, rejectRequest, ignore);
                int user_id = Integer.parseInt(selectedFriendship.getFirst_name().split(";")[0]);

                Optional<ButtonType> result = acceptOrReject.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == acceptRequest) {
                        controller.acceptFriendship(selectedFriendship.getId(), user_id);
                    } else if (result.get() == rejectRequest) {
                        controller.rejectFriendship(selectedFriendship.getId(), user_id);
                    }
                    selectedFriendship = null;

                    loadFriendships();
                }
            } else {
                acceptOrReject.setTitle("Do you cancel this friendship request ?");
                acceptOrReject.setContentText("You have sent a friendship request to " +
                        selectedFriendship.getSecond_name());

                hideRelationsMenu();
                ButtonType deleteRequest = new ButtonType("Yes");
                ButtonType ignore = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                acceptOrReject.getButtonTypes().setAll(deleteRequest, ignore);
                Optional<ButtonType> result = acceptOrReject.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == deleteRequest) {
                        controller.deleteFriendship(selectedFriendship.getId());
                    }
                    selectedFriendship = null;
                    loadFriendships();
                }
            }
        }

        if(selectedFriendship!=null &&
            Objects.equals(selectedFriendship.getStatus(), "Accepted")
        ){
            loadMessages();
        }
    }

    public void loadMessages() throws SQLException {
        if(selectedFriendship!=null)
        {
            int passive_user_id = Integer.parseInt(selectedFriendship.getSecond_name().split(";")[0]);
            User tempUser = controller.findUser(passive_user_id);
            passiveUserControl=new UserDTO(tempUser.getId(),tempUser.getFirstName(), tempUser.getSurname());
        }

        showMessages();
        List<String> messages = (List<String>) getMessages();
        if(messages!=null && messages.size()!=0)
            messageTable.setItems(FXCollections.observableList(messages));
        else
            messageTable.setPlaceholder(new Label("There are no messages between you \n" +
                    " Looks like it's time to change that"));
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) throws ValidateException, BusinessException, SQLException, RepoException {
        if(currentUserControl!=null && passiveUserControl!=null){
            String message = messageBody.getText();
            if(message.length()!=0)
            {
                controller.sendMessage(currentUserControl.getId(),passiveUserControl.getId(),message,null);
                loadMessages();
                messageBody.setText("");
            }
        }
    }

    public void sendMessageViaEnter(KeyEvent keyEvent) throws ValidateException, BusinessException, SQLException, RepoException {
        if(keyEvent.getCode()==KeyCode.ENTER){
            sendMessage(null);
        }
    }

    @FXML
    public void login(UserDTO user) throws SQLException {
        currentUserControl = user;
        selectedUser.setText(
                currentUserControl.toString()
        );
        loadFriendships();
        hideRelationsMenu();
        hideMessages();
    }
}