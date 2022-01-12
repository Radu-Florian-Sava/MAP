package com.example.lab_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ShowEventController {

    int id_user;
    int id_event;

    @FXML
    public ListView myEvents;
    @FXML
    public ListView allEvents;

    public void setUp(int id_user) {
        this.id_user = id_user;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    public void onJoinEventClicked(ActionEvent actionEvent) {
    }

    @FXML
    public void onLeaveEventClicked(ActionEvent actionEvent) {
    }
}
