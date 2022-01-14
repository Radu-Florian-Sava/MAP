package com.example.lab_gui;

import Control.Controller;
import Domain.Event;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;

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

    public void setUp(int id_user) {
        this.idUser = id_user;
        load();
    }

    private void load() {
        titleColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getTitle()));
        dateColumn.setCellValueFactory((data) -> new SimpleStringProperty(data.getValue().getDate().toString()));
        statusColumn.setCellValueFactory((data) ->
                new SimpleStringProperty(data.getValue().getUsers().get(idUser).getKey().getStatus()));

        load_all_events();
        load_my_events();
    }

    @FXML
    public void initialize() {

    }

    public void load_all_events() {
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

    public void load_my_events() {
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
                load_my_events();
            } catch (SQLException | BusinessException | ValidateException | RepoException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.show();
            }
        }
    }

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
                load_my_events();
                load_all_events();
            } catch (SQLException | BusinessException | RepoException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(e.getMessage());
                alert.show();
            }
        }
    }
}
