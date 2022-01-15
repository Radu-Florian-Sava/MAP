package com.example.lab_gui;

import Control.Controller;
import Domain.Event;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

/**
 *  the class corresponding to the show event fxml
 */
public class ShowEventController {

    int idUser;

    @FXML
    public TableColumn<Event, String> titleColumn;
    @FXML
    public TableColumn<Event, String> dateColumn;
    @FXML
    public TableColumn<Event, String> statusColumn;
    @FXML
    public TableView<Event> myEvents;
    @FXML
    public ListView<Event> allEvents;

    /**
     * @param idUser the id of the current user
     * it does the initial setup of the event window
     */
    public void setUp(int idUser) {
        this.idUser = idUser;
        load();
    }

    /**
     *  initialises each table column with the data it extracts
     *  loads all the events and the events in which the user will participate in
     */
    private void load() {
        titleColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getTitle()));
        dateColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getDate().toString()));
        statusColumn.setCellValueFactory((data) ->
                new SimpleStringProperty(data.getValue().getUsers().get(idUser).getKey().getStatus()));

        loadAllEvents();
        loadMyEvents();
    }

    @FXML
    public void initialize() {

    }

    /**
     *  loads all the events which exist in the application
     */
    public void loadAllEvents() {
        try {
            List<Event> events = (List<Event>) Controller.getInstance().getAllEvents();
            if(events != null && events.size() != 0)
                allEvents.setItems(FXCollections.observableList(events));
            else
                allEvents.setPlaceholder(new Label("There are no events yet\n" +
                        " Looks like it's time to change that"));
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.show();
        }
    }

    /**
     * loads all the events of a user from the application
     */
    public void loadMyEvents() {
        try {
            List<Event> events = (List<Event>) Controller.getInstance().getMyEvents(idUser);
            myEvents.setItems(FXCollections.observableList(events));
            myEvents.setPlaceholder(new Label("There are no events yet\n" +
                        " Looks like it's time to change that"));
            myEvents.refresh();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.show();
        }
    }

    /**
     * if the button is clicked the event will be joined
     */
    @FXML
    public void onJoinEventClicked() {
        Event event = allEvents.getSelectionModel().getSelectedItem();
        if(event == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must select an event");
            alert.show();
        }
        else {
            try {
                Controller.getInstance().joinEvent(idUser, event.getId());
                loadMyEvents();
            } catch (SQLException | BusinessException | ValidateException | RepoException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.show();
            }
        }
    }

    /**
     *  if leave event is pressed, the GUI lists will update
     */
    @FXML
    public void onLeaveEventClicked() {
        Event event = myEvents.getSelectionModel().getSelectedItem();
        if(event == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must select an event from yours");
            alert.show();
        }
        else {
            try {
                Controller.getInstance().deleteEvent(idUser, event.getUsers().get(idUser).getValue());
                loadMyEvents();
                loadAllEvents();
            } catch (SQLException | BusinessException | RepoException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.show();
            }
        }
    }
}
