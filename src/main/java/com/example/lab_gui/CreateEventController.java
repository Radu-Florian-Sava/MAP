package com.example.lab_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateEventController {

    private int id_user;
    @FXML
    public TextField titleBox;
    @FXML
    public TextArea descriptionBox;
    @FXML
    public DatePicker datePicker;

    public void setUp(int id_user) {
        this.id_user = id_user;
    }

    @FXML
    public void onCreateEventClicked(ActionEvent actionEvent) {
    }
}
