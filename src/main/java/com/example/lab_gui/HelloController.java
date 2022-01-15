package com.example.lab_gui;

import Control.Controller;
import Domain.*;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Utils.Constants;
import Utils.StatusFriendship;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

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

    @FXML
    public Spinner<Integer> pageSpinner;

    @FXML
    public Label updatingTextLabel;

    @FXML
    public TextField firstNameFilter;

    @FXML
    public TextField surnameFilter;

    @FXML
    public VBox addOrRemoverFriendBox;

    @FXML
    public WebView webView;

    @FXML
    public Button searchButton;

    // pseudo - fx: id(s)
    private Controller controller = Controller.getInstance();
    private Page userPage = null;

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

    private final ContextMenu messageContextMenu = new ContextMenu();
    private final MenuItem deleteMessage = new MenuItem("Delete Message");
    private final MenuItem broadcastMessage = new MenuItem("Broadcast Message");

    // all users
    @FXML
    private Alert messageBox;
    @FXML
    private TextField selectedUser;
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
        Runnable runnable = ()-> {
            WebEngine webEngine = webView.getEngine();
            webEngine.loadContent("<html> <body style=\"background-color:#7b68ee;\"> <body> <iframe width=\"300\" " +
                    "height=\"180\" src=\"https://www.youtube.com/embed/qlXoh54zock\" " +
                    "title=\"YouTube video player\" frameborder=\"0\" " +
                    "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope;" +
                    " picture-in-picture\" allowfullscreen></iframe> </body> </html>");
        };
        Thread thread= new Thread(runnable);
        Platform.runLater(thread::run);
    }

    private void setController(Controller controller) {
        this.controller = controller;
    }

    private void loadFriendships()  {

        List<FriendshipDTO> friendships;
        try {
            friendships = controller.getAllTypesOfFriendshipsOf(userPage.getMainUser().getId());
            friendshipTable.setItems(FXCollections.observableList(friendships));
            if (friendships.isEmpty()) {
                friendshipTable.setPlaceholder(new Label("There are no friendships to show \n for now :'("));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        Runnable runnable =  () -> {
            while(true){
                try {
                    Thread.sleep(Constants.MILLISECONDS_IN_A_MINUTE);
                    Platform.runLater(()->
                            updatingTextLabel.setText(Constants.TIPS[new Random().nextInt(Constants.TIPS.length)])
                    );
                    Thread.sleep(Constants.MILLISECONDS_IN_A_MINUTE);
                } catch (InterruptedException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();

    }

    private void loadFriends() {
            List<UserDTO> hiddenUserDTO;
            addOrRemoverFriendBox.setVisible(true);
            changeFriendStatus.setText("Unfriend");
            try {
                hiddenUserDTO = controller.getAllUsersDTO().stream().filter(
                        x -> {
                            try {
                                return controller.areFriends(userPage.getMainUser().getId(), x.getId());
                            } catch (SQLException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setContentText(e.getMessage());
                                alert.show();
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
    }

    private void loadBefriendable()  {
        if (userPage.getMainUser() != null) {
            List<UserDTO> hiddenUserDTO = null;

            addOrRemoverFriendBox.setVisible(true);
            changeFriendStatus.setText("Send request");

            try {
                hiddenUserDTO = controller.getAllUsersDTO().stream().filter(
                        x -> {
                            try {
                                return !controller.areFriends(userPage.getMainUser() .getId(), x.getId()) &&
                                        x.getId() != userPage.getMainUser() .getId() &&
                                        !controller.getSentFriendships(userPage.getMainUser() .getId()).stream().filter(
                                                        y -> y.getFriendshipRequest() == 0 || y.getFriendshipRequest() == 2
                                                ).toList()
                                                .contains(new Friendship(0, userPage.getMainUser() .getId(), x.getId()));
                            } catch (SQLException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setContentText(e.getMessage());
                                alert.show();
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
                userPage.setFriendshipUser(null);            }
        }
    }

    private void hideRelationsMenu()  {
        addOrRemoverFriendBox.setVisible(false);
        changeFriendStatus.setText("Unfriend\\Befriend");
        userPage.setFriendshipUser(null);
        passiveUserName.setText("Nume Prenume");
        loadFriendships();
    }

    private void hideMessages(){
        messageFunctionality.setVisible(false);
        userPage.setMessageUser(null) ;
    }

    private void showMessages(){
        if(userPage.getMessageUser() !=null)
        {
            messageFunctionality.setVisible(true);
            messageColumn.setText("Messages with " +
                    userPage.getMessageUser().getFirstName() +
                    " " +
                    userPage.getMessageUser().getSurname()
            );
        }
    }

    private Iterable<MessageDTO> getMessages()  {
        if(userPage.getMessageUser()!=null){
            try {
                return controller.getAllMessagesPaged(userPage.getMainUser().getId(),
                        userPage.getMessageUser().getId(), userPage.getPageNumber());
            } catch (SQLException | BusinessException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
        return null;
    }

    private void initFriendshipTable(){
        relationColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStringRelation()));
        userColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSecondName()
                .replace(';',' ')));

        statusColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void initAddOrDeleteFriendTable(){
        hiddenID.setCellValueFactory((data) -> new SimpleStringProperty(Integer.toString(data.getValue().getId())));
        hiddenFirstName.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getFirstName()));
        hiddenSurname.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getSurname()));
    }

    private void initMessageFactory(){
        messageColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().toString()));

        deleteMessage.setOnAction((ActionEvent e) -> {
            int idToRemove = messageTable.getSelectionModel().getSelectedItem().getId();
            try {
                controller.deleteMessage(idToRemove);
                if(userPage.getIdToReply()==idToRemove){
                    userPage.setIdToReply(Constants.NO_MESSAGE_ID);
                    sendMessageButton.setText("Send \nMessage");
                }
                int nrOfPages = controller.getNrMaxPages(userPage.getMainUser().getId(),userPage.getMessageUser().getId());
                pageSpinner.setValueFactory(
                        new SpinnerValueFactory
                                .IntegerSpinnerValueFactory(0, nrOfPages - 1, nrOfPages - 1));
                userPage.setPageNumber(nrOfPages - 1);
                loadMessages();
            } catch (SQLException | RepoException | BusinessException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });
        broadcastMessage.setOnAction((ActionEvent e) -> {
            try {
                Iterable<Friendship> friendships = controller
                        .getAcceptedFriendshipsOf(userPage.getMainUser().getId());
                ArrayList<Integer> friendIDs = new ArrayList<>();
                TableView<User> broadcastTable =new TableView<>();
                TableColumn<User,String > friendNames=new TableColumn<>();
                friendNames.setCellValueFactory((data)->new SimpleStringProperty(data.getValue().getFirstName()
                        +" "+data.getValue().getSurname()));
                broadcastTable.getColumns().add(friendNames);
                for (Friendship friendship :friendships) {
                    if(friendship.getSender() == userPage.getMainUser().getId()){
                        friendIDs.add(friendship.getReceiver());
                    }
                    else
                        friendIDs.add(friendship.getSender());
                }
                ArrayList<User> userArrayList = new ArrayList<>();
                friendIDs.forEach(x-> {
                    try {
                        userArrayList.add(controller.findUser(x));
                    } catch (SQLException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText(ex.getMessage());
                        alert.show();
                    }
                });
                broadcastTable.getItems().setAll(userArrayList);
                friendNames.setText("Select the friends that you want to omit");
                friendNames.setMinWidth(broadcastTable.getPrefWidth());
                broadcastTable.setOnMouseClicked(event -> {
                    if(broadcastTable.getSelectionModel().getSelectedItem()!=null){
                        friendIDs.remove(broadcastTable.getSelectionModel().getSelectedItem().getId());
                        broadcastTable.getItems().remove(broadcastTable.getSelectionModel().getSelectedItem());
                    }
                });
                broadcastTable.setOnKeyPressed(event -> {
                    if(event.getCode()==KeyCode.ENTER ){
                        if(!Objects.equals(messageBody.getText(), "")){
                            try {
                                controller.sendMessageToIds(userPage.getMainUser().getId(),messageBody.getText(),friendIDs);
                                messageBody.setText("");
                                loadMessages();
                            } catch (ValidateException | BusinessException | SQLException | RepoException ex) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setContentText(ex.getMessage());
                                alert.show();
                            }
                        }
                        messageFunctionality.getChildren().remove(broadcastTable);
                    }
                });
                broadcastTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                broadcastTable.setVisible(true);
                broadcastTable.setMaxHeight(100);
                messageFunctionality.getChildren().add(0,broadcastTable);

            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.show();
            }
        });
        messageContextMenu.getItems().addAll(deleteMessage,broadcastMessage);
    }

    public void load() {

        initFriendshipTable();

        initAddOrDeleteFriendTable();

        initMessageFactory();

    }

    @FXML
    private void setPassiveUser() {
        userPage.setFriendshipUser(hiddenTable.getSelectionModel().getSelectedItem());
        if (userPage.getFriendshipUser() != null) {
            passiveUserName.setText(
                    userPage.getFriendshipUser().toString()
            );
        }
    }

    @FXML
    public void revealPotentialFriends()  {
        loadBefriendable();

    }

    @FXML
    public void revealCurrentFriends()  {
        loadFriends();
    }

    public void changeStatusOfFriendship()  {
        try {
            if (userPage.getFriendshipUser() != null ) {

                if (Objects.equals(changeFriendStatus.getText(), "Send request")) {
                    controller.sendFriendship(userPage.getMainUser().getId(),
                            userPage.getFriendshipUser().getId());
                    hideRelationsMenu();
                } else if (Objects.equals(changeFriendStatus.getText(), "Unfriend")) {
                    Iterable<Friendship> friendshipList = controller
                            .getAcceptedFriendshipsOf(userPage.getMainUser().getId());
                    friendshipList.forEach(friendship -> {
                                if (friendship.getSender() == userPage.getFriendshipUser().getId() ||
                                        friendship.getReceiver() == userPage.getFriendshipUser()
                                                .getId()) {
                                    try {
                                        controller.deleteFriendship(friendship.getId());
                                        if (userPage.getFriendshipUser().getId()
                                                == userPage.getMessageUser().getId())
                                            hideMessages();
                                        hideRelationsMenu();
                                    } catch (RepoException | SQLException e) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setContentText(e.getMessage());
                                        alert.show();
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

    @FXML
    public void selectFriendship()  {
        try {
            userPage.setFriendshipFocus(friendshipTable.getSelectionModel().getSelectedItem());

            if (userPage.getFriendshipFocus() != null &&
                    Objects.equals(userPage.getFriendshipFocus()
                            .getStatus(), "Waiting...")
            ) {
                Alert acceptOrReject = new Alert(Alert.AlertType.CONFIRMATION);
                acceptOrReject.setGraphic(acceptOrReject.getDialogPane().getGraphic());
                if (userPage.getFriendshipFocus().getRelation() == 1) {

                    acceptOrReject.setTitle("Do you confirm this friendship request ?");
                    acceptOrReject.setContentText("The user " + userPage.getFriendshipFocus()
                            .getSecondName() +
                            " has sent you a friend request");

                    ButtonType acceptRequest = new ButtonType("Accept");
                    ButtonType rejectRequest = new ButtonType("Reject");
                    ButtonType ignore = new ButtonType("Ignore for now", ButtonBar.ButtonData.CANCEL_CLOSE);

                    acceptOrReject.getButtonTypes().setAll(acceptRequest, rejectRequest, ignore);
                    int user_id = Integer.parseInt(userPage.getFriendshipFocus()
                            .getFirstName().split(";")[0]);

                    Optional<ButtonType> result = acceptOrReject.showAndWait();

                    if (result.isPresent()) {
                        if (result.get() == acceptRequest) {
                            controller.acceptFriendship(userPage.getFriendshipFocus()
                                    .getId(), user_id);
                        } else if (result.get() == rejectRequest) {
                            controller.rejectFriendship(userPage.getFriendshipFocus()
                                    .getId(), user_id);
                        }
                        userPage.setFriendshipFocus(null);

                        loadFriendships();
                    }
                } else {
                    acceptOrReject.setTitle("Do you cancel this friendship request ?");
                    acceptOrReject.setContentText("You have sent a friendship request to " +
                            userPage.getFriendshipFocus().getSecondName());

                    ButtonType deleteRequest = new ButtonType("Yes");
                    ButtonType ignore = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    acceptOrReject.getButtonTypes().setAll(deleteRequest, ignore);
                    Optional<ButtonType> result = acceptOrReject.showAndWait();

                    if (result.isPresent()) {
                        if (result.get() == deleteRequest) {
                            controller.deleteFriendship(userPage.getFriendshipFocus().getId());
                        }
                        userPage.setFriendshipFocus(null);
                        loadFriendships();
                    }
                }
            }

            if (userPage.getFriendshipFocus() != null &&
                    Objects.equals(userPage.getFriendshipFocus().getStatus(), "Accepted")
            ) {
                User tempUser;
                try {
                    int passiveUserId = Integer.parseInt(userPage.getFriendshipFocus().getSecondName().split(";")[0]);
                    tempUser = controller.findUser(passiveUserId);
                    userPage.setMessageUser(new UserDTO(tempUser.getId(),tempUser.getFirstName(),
                            tempUser.getSurname(),tempUser.getUsername()));
                    int nrOfPages = controller.getNrMaxPages(userPage.getMainUser().getId(),userPage.getMessageUser().getId());
                    pageSpinner.setValueFactory(
                            new SpinnerValueFactory
                                    .IntegerSpinnerValueFactory(0, nrOfPages - 1, nrOfPages - 1));
                    userPage.setPageNumber(nrOfPages - 1);
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText(e.getMessage());
                    alert.show();
                }
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
        if(userPage.getFriendshipFocus()!=null)
        {
            userPage.setIdToReply(Constants.NO_MESSAGE_ID);
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
        if(userPage.getMainUser()!=null && userPage.getMessageUser() !=null){
            String message = messageBody.getText();
            if(message.length()!=0)
            {
                try {
                    if (userPage.getIdToReply() == Constants.NO_MESSAGE_ID)
                        controller.sendMessage(userPage.getMainUser().getId(),
                                userPage.getMessageUser().getId(), message, null);
                    else {
                        controller.sendMessage(userPage.getMainUser().getId(),
                                userPage.getMessageUser().getId(), message, userPage.getIdToReply());
                        userPage.setIdToReply(Constants.NO_MESSAGE_ID);
                        sendMessageButton.setText("Send \nMessage");
                    }
                    int nrOfPages = controller.getNrMaxPages(userPage.getMainUser().getId(),userPage.getMessageUser().getId());
                    pageSpinner.setValueFactory(
                            new SpinnerValueFactory
                                    .IntegerSpinnerValueFactory(0, nrOfPages - 1, nrOfPages - 1));
                    userPage.setPageNumber(nrOfPages - 1);
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

    @FXML
    public void sendMessageViaEnter(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ENTER){
            sendMessage();
        }
    }

    public void login(UserDTO user)  {
        try {
            userPage=new Page(user,controller.getAllUsersDTO().stream()
                    .filter(x-> {
                        try {
                            return controller.areFriends(x.getId(),user.getId());
                        } catch (SQLException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText(e.getMessage());
                            alert.show();
                        }
                        return false;
                    }).toList(),
                    controller.getAllTypesOfFriendshipsOf(user.getId()),
                    (List<Message>) controller.getMessages()
            );
            selectedUser.setText(
                    userPage.getMainUser().toString()
            );
            loadFriendships();
            hideRelationsMenu();
            hideMessages();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
    private void setPDFData(String title,Stage stage,Scene scene){
        stage.setTitle(title);
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
    public void onPDFSimpleClicked()  {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pdf.fxml"));
        Parent parent;
        try {
            parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 321, 400);
            scene.getStylesheets().add(HelloApplication.class.getResource("aside.css").toExternalForm());
            PdfController pdfController = fxmlLoader.getController();
            pdfController.setUp(1, userPage.getMainUser().getId(), null);
            Stage stage = new Stage();
            setPDFData("Generate PDF",stage,scene);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }

    }

    @FXML
    public void onPDFFriendClicked()  {
        if(userPage.getFriendshipFocus()==null ||
                !Objects.equals(userPage.getFriendshipFocus().getStatus(),
                        StatusFriendship.ACCEPT.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must choose a friend for this PDF");
            alert.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pdf.fxml"));
            Parent parent;
            try {
                parent = fxmlLoader.load();
                Scene scene = new Scene(parent, 321, 400);
                scene.getStylesheets().add(HelloApplication.class.getResource("aside.css").toExternalForm());
                PdfController pdfController = fxmlLoader.getController();
                pdfController.setUp(2, userPage.getMainUser().getId(), userPage.getMessageUser());
                Stage stage = new Stage();
                setPDFData("Generate PDF for a friend",stage,scene);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    @FXML
    public void selectReply() {
        messageContextMenu.hide();
        if(userPage.getIdToReply()==Constants.NO_MESSAGE_ID
                && messageTable.getSelectionModel().getSelectedItem() != null)
        {
            userPage.setIdToReply(messageTable.getSelectionModel().getSelectedItem().getId());
            sendMessageButton.setText("Send \nReply");
        }
        else {
            userPage.setIdToReply(Constants.NO_MESSAGE_ID);
            sendMessageButton.setText("Send \nMessage");
        }
    }

    @FXML
    public void onCreateEventClicked() {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("createEvent.fxml"));
        Parent parent;
        try {
            parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 321, 400);
            scene.getStylesheets().add(HelloApplication.class.getResource("aside.css").toExternalForm());
            CreateEventController createEventController = fxmlLoader.getController();
            createEventController.setUp(userPage.getMainUser().getId());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Create an event");
            stage.setHeight(410);
            stage.setWidth(317);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void onShowEventClicked() {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("showEvent.fxml"));
        Parent parent;
        try {
            parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 321, 400);
            scene.getStylesheets().add(HelloApplication.class.getResource("aside.css").toExternalForm());
            ShowEventController showEventController = fxmlLoader.getController();
            showEventController.setUp(userPage.getMainUser().getId());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setHeight(430);
            stage.setWidth(620);
            stage.setTitle("Show events");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void changeMessagePage() {
        userPage.setPageNumber(pageSpinner.getValue());
        loadMessages();
    }

    @FXML
    public void openMouseContextMenu(ContextMenuEvent contextMenuEvent) {
        messageContextMenu.show(messageTable,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    @FXML
    public void filterFirstName(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ENTER){
            hiddenTable.setItems(FXCollections.observableList(
            hiddenTable.getItems().stream().filter(x->x.getFirstName().contains(firstNameFilter.getText())).toList()
            ));
        }else isBackSpace(keyEvent, firstNameFilter, surnameFilter);
    }

    @FXML
    public void filterSurname(KeyEvent keyEvent) {
        if(keyEvent.getCode()==KeyCode.ENTER) {
            hiddenTable.setItems(FXCollections.observableList(
                    hiddenTable.getItems().stream().filter(x -> x.getSurname().contains(surnameFilter.getText())).toList()
            ));
        }
        else {
            isBackSpace(keyEvent, surnameFilter, firstNameFilter);
        }
    }

    private void isBackSpace(KeyEvent keyEvent, TextField surnameFilter, TextField firstNameFilter) {
        if(keyEvent.getCode()== KeyCode.BACK_SPACE){
            if(Objects.equals(surnameFilter.getText(), "")){
                firstNameFilter.setText("");
                if(Objects.equals(changeFriendStatus.getText(), "Unfriend")){
                    loadFriends();
                }
                else if(Objects.equals(changeFriendStatus.getText(), "Send request")){
                    loadBefriendable();
                }
            }

        }
    }

    public void searchOnTheInternet() {
            Runnable runnable = ()-> {
                WebEngine webEngine = webView.getEngine();
                webEngine.loadContent("<html> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> " +
                        "<button id=\"search\">Search</button>\n" +
                        "<script>\n" +
                        "var q = \" \";\n" +
                        "document.getElementById('search').onclick = function() {\n" +
                        "    window.open('http://google.com/search?q='+q);\n" +
                        "};\n" +
                        "</script> </html>");
            };
            Thread thread= new Thread(runnable);
            Platform.runLater(thread::run);
    }
}