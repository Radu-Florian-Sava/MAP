package com.example.lab_gui;

import Control.Controller;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.sql.Timestamp;

public class CreateEventController {
    private int id_user;

    @FXML
    public Button createEventButton;
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
        Timestamp timestamp;
        if(datePicker.getValue() == null)
            timestamp = null;
        else {
            timestamp = new Timestamp(datePicker.getValue().toEpochDay() * 3600 * 24 * 1000);
        }

        try {
            Controller.getInstance().createEvent(
                    id_user,
                    titleBox.getText(),
                    descriptionBox.getText(),
                    timestamp);
        } catch (ValidateException | BusinessException | SQLException | RepoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
