package com.example.lab_gui;

import Control.Controller;
import Domain.*;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Utils.StatusFriendship;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *  controller of the main window
 */
public class HelloController {


    @FXML
    public HBox changeStatusSection;
    @FXML
    public HBox containingWindow;
    @FXML
    public Button pdfSimpleButton;

    // pseudo - fx: id(s)
    private Controller controller = Controller.getInstance();
    private UserDTO currentUserControl = null;
    private UserDTO messageUserControl = null;
    private UserDTO changeStatusUserControl = null;
    private FriendshipDTO selectedFriendship = null;
    private int idToReply=-1;

    // messages
    @FXML
    private TableView<MessageDTO> messageTable;

    @FXML
    private TableColumn<MessageDTO,String> messageColumn;

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

    private void loadFriendships()  {

        List<FriendshipDTO> friendships = null;

        if (currentUserControl != null) {
            try {
                friendships = controller.getAllTypesOfFriendshipsOf(currentUserControl.getId());
                friendshipTable.setItems(FXCollections.observableList(friendships));

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }

        if (friendships == null || friendships.isEmpty()) {
            friendshipTable.setPlaceholder(new Label("There are no friendships to show \n for now :'("));
        }

    }

    private void loadUsers()  {
        List<UserDTO> userDTOS;
        try {
            userDTOS = controller.getAllUsersDTO();
            userTable.setItems(FXCollections.observableList(userDTOS));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void loadFriends() {

        if (currentUserControl != null) {

            List<UserDTO> hiddenUserDTO = null;

            hiddenTable.setVisible(true);
            changeStatusSection.setVisible(true);
            changeFriendStatus.setText("Unfriend");

            try {
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
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }

            if (hiddenUserDTO==null) {
                hiddenTable.setPlaceholder(new Label("For the moment there are no friends to show \n" +
                        "Try again after you make some :P"));
                changeStatusUserControl = null;
            }
        }
    }

    private void loadBefriendable()  {
        if (currentUserControl != null) {
            List<UserDTO> hiddenUserDTO = null;

            hiddenTable.setVisible(true);
            changeStatusSection.setVisible(true);
            changeFriendStatus.setText("Send request");

            try {
                hiddenUserDTO = controller.getAllUsersDTO().stream().filter(
                        x -> {
                            try {
                                return !controller.areFriends(currentUserControl.getId(), x.getId()) &&
                                        x.getId() != currentUserControl.getId() &&
                                        !controller.getSentFriendships(currentUserControl.getId()).stream().filter(
                                                        y -> y.getFriendshipRequest() == 0 || y.getFriendshipRequest() == 2
                                                ).toList()
                                                .contains(new Friendship(0, currentUserControl.getId(), x.getId()));
                            } catch (SQLException ignored) {
                            }
                            return false;
                        }
                ).toList();
                hiddenTable.setItems(FXCollections.observableList(hiddenUserDTO));
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }


            if (hiddenUserDTO==null) {
                hiddenTable.setPlaceholder(new Label("It looks like you've got lots of friends \n" +
                        "There's no one to add at the moment XD"));
                changeStatusUserControl = null;
            }
        }
    }

    private void hideRelationsMenu()  {
        changeStatusSection.setVisible(false);
        hiddenTable.setVisible(false);
        changeFriendStatus.setText("Unfriend\\Befriend");
        changeStatusUserControl = null;
        passiveUserName.setText("Nume Prenume");
        loadFriendships();
    }

    private void hideMessages(){
        messageFunctionality.setVisible(false);
        messageUserControl =null;
    }

    private void showMessages(){
        if(messageUserControl !=null)
        {
            messageFunctionality.setVisible(true);
            messageColumn.setText("Messages with " +
                    messageUserControl.getFirstName() +
                    " " +
                    messageUserControl.getSurname()
            );
        }
    }

    private Iterable<MessageDTO> getMessages()  {
        if(currentUserControl!=null && messageUserControl !=null){
            try {
                return controller.getMessagesBy2Users(currentUserControl.getId(), messageUserControl.getId());
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
        return null;
    }

    public void load() {

        relationColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStringRelation()));
        userColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSecondName()));
        statusColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStatus()));


        userID.setCellValueFactory((data) -> new SimpleStringProperty(Integer.toString(data.getValue().getId())));
        userFirstName.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getFirstName()));
        userSurname.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSurname()));

        hiddenID.setCellValueFactory((data) -> new SimpleStringProperty(Integer.toString(data.getValue().getId())));
        hiddenFirstName.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getFirstName()));
        hiddenSurname.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSurname()));

        messageColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().toString()));

        loadFriendships();
        loadUsers();


    }

    @FXML
    private void setPassiveUser() {
        changeStatusUserControl = hiddenTable.getSelectionModel().getSelectedItem();
        if (changeStatusUserControl != null) {
            passiveUserName.setText(
                    changeStatusUserControl.toString()
            );
        }
    }

    public void revealPotentialFriends()  {
        loadBefriendable();

    }

    public void revealCurrentFriends()  {
        loadFriends();
    }

    public void changeStatusOfFriendship()  {
        try {
            if (changeStatusUserControl != null && currentUserControl != null) {

                if (Objects.equals(changeFriendStatus.getText(), "Send request")) {
                    controller.sendFriendship(currentUserControl.getId(), changeStatusUserControl.getId());
                    hideRelationsMenu();
                } else if (Objects.equals(changeFriendStatus.getText(), "Unfriend")) {
                    Iterable<Friendship> friendshipList = controller.getAcceptedFriendshipsOf(currentUserControl.getId());
                    friendshipList.forEach(friendship -> {
                                if (friendship.getSender() == changeStatusUserControl.getId() ||
                                        friendship.getReceiver() == changeStatusUserControl.getId()) {
                                    try {
                                        controller.deleteFriendship(friendship.getId());
                                        if (changeStatusUserControl.getId() == messageUserControl.getId())
                                            hideMessages();
                                        hideRelationsMenu();
                                    } catch (RepoException | SQLException ignored) {
                                    }
                                }
                            }
                    );
                    hideRelationsMenu();

                }

            }
        }catch(SQLException| BusinessException| ValidateException| RepoException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
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

    public void tryToFindUserByID(KeyEvent keyEvent)  {
        if (keyEvent.getCode() != KeyCode.ENTER)
            return;
        try {
            int tempId = Integer.parseInt(selectedUser.getText());
            User tempUser = null;
            try {
                tempUser = controller.findUser(tempId);
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            if (tempUser != null)
                selectedUser.setText(new UserDTO(tempId, tempUser.getFirstName(), tempUser.getSurname(),tempUser.getUsername()).toString());
            else
                throw new NumberFormatException("This is not the user that you are looking for");
        } catch (NumberFormatException numberFormatException) {
            summonMessageBox("Try searchig by id number");
        }
    }

    public void selectFriendship()  {
        try {
            selectedFriendship = friendshipTable.getSelectionModel().getSelectedItem();

            if (selectedFriendship != null &&
                    Objects.equals(selectedFriendship.getStatus(), "Waiting...")
            ) {
                Alert acceptOrReject = new Alert(Alert.AlertType.CONFIRMATION);
                acceptOrReject.setGraphic(acceptOrReject.getDialogPane().getGraphic());
                if (selectedFriendship.getRelation() == 1) {

                    acceptOrReject.setTitle("Do you confirm this friendship request ?");
                    acceptOrReject.setContentText("The user " + selectedFriendship.getSecondName() +
                            " has sent you a friend request");

                    ButtonType acceptRequest = new ButtonType("Accept");
                    ButtonType rejectRequest = new ButtonType("Reject");
                    ButtonType ignore = new ButtonType("Ignore for now", ButtonBar.ButtonData.CANCEL_CLOSE);

                    acceptOrReject.getButtonTypes().setAll(acceptRequest, rejectRequest, ignore);
                    int user_id = Integer.parseInt(selectedFriendship.getFirstName().split(";")[0]);

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
                            selectedFriendship.getSecondName());

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

            if (selectedFriendship != null &&
                    Objects.equals(selectedFriendship.getStatus(), "Accepted")
            ) {
                loadMessages();
            }
        }catch(SQLException| ValidateException| BusinessException| RepoException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void loadMessages()  {
        if(selectedFriendship!=null)
        {
            idToReply = -1;
            int passive_user_id = Integer.parseInt(selectedFriendship.getSecondName().split(";")[0]);
            User tempUser = null;
            try {
                tempUser = controller.findUser(passive_user_id);
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            messageUserControl =new UserDTO(tempUser.getId(),tempUser.getFirstName(), tempUser.getSurname(),tempUser.getUsername());
        }

        showMessages();
        List<MessageDTO> messages = (List<MessageDTO>) getMessages();
        if(messages!=null && messages.size()!=0)
            messageTable.setItems(FXCollections.observableList(messages));
        else
            messageTable.setPlaceholder(new Label("There are no messages between you \n" +
                    " Looks like it's time to change that"));
    }

    @FXML
    public void sendMessage() {
        if(currentUserControl!=null && messageUserControl !=null){
            String message = messageBody.getText();
            if(message.length()!=0)
            {
                try {
                    if (idToReply == -1)
                        controller.sendMessage(currentUserControl.getId(), messageUserControl.getId(), message, null);
                    else {
                        controller.sendMessage(currentUserControl.getId(), messageUserControl.getId(), message, idToReply);
                        idToReply = -1;
                        sendMessageButton.setText("Send \nMessage");
                    }

                    loadMessages();
                    messageBody.setText("");
                }catch (BusinessException|SQLException|ValidateException|RepoException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }
            }
        }
    }

    public void sendMessageViaEnter(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ENTER){
            sendMessage();
        }
    }

    @FXML
    public void login(UserDTO user)  {
        currentUserControl = user;
        selectedUser.setText(
                currentUserControl.toString()
        );
        loadFriendships();
        hideRelationsMenu();
        hideMessages();
    }

    @FXML
    public void onPDFSimpleClicked()  {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pdf.fxml"));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        Scene scene = new Scene(parent, 321, 400);
        PdfController pdfController = fxmlLoader.getController();

        pdfController.setUp(1, currentUserControl.getId(), null);

        Stage stage = new Stage();

        stage.setTitle("Generate PDF");
        stage.setScene(scene);
        stage.setMinHeight(175);
        stage.setMinWidth(400);
        stage.setHeight(175);
        stage.setWidth(400);
        stage.setMaxHeight(175);
        stage.setMaxWidth(400);

        stage.show();
    }

    @FXML
    public void onPDFFriendClicked()  {
        if(selectedFriendship==null || selectedFriendship.getStatus() != StatusFriendship.ACCEPT.getStatus()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must choose a friend for this PDF");
            alert.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pdf.fxml"));
            Parent parent = null;
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            Scene scene = new Scene(parent, 321, 400);
            PdfController pdfController = fxmlLoader.getController();

            pdfController.setUp(2, currentUserControl.getId(), messageUserControl);

            Stage stage = new Stage();

            stage.setTitle("Generate PDF for a friend");
            stage.setScene(scene);

            stage.setMinHeight(175);
            stage.setMinWidth(400);
            stage.setHeight(175);
            stage.setWidth(400);
            stage.setMaxHeight(175);
            stage.setMaxWidth(400);

            stage.show();
        }
    }

    @FXML
    public void selectReply() {
        if(idToReply==-1)
        {
            idToReply = messageTable.getSelectionModel().getSelectedItem().getId();
            sendMessageButton.setText("Send \nReply");
        }
        else{
            idToReply = -1;
            sendMessageButton.setText("Send \nMessage");
        }
    }
}