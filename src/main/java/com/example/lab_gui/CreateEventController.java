package com.example.lab_gui;

import Control.Controller;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *  the class corresponding to the create event fxml
 */
public class CreateEventController {

    private int id_user;

    @FXML
    public Spinner<Integer> minuteBox;
    @FXML
    public Spinner<Integer> hourBox;
    @FXML
    public Button createEventButton;
    @FXML
    public TextField titleBox;
    @FXML
    public TextArea descriptionBox;
    @FXML
    public DatePicker datePicker;

    /**
     * @param idUser the ID of the user who created the event
     */
    public void setUp(int idUser) {
        this.id_user = idUser;
        minuteBox.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        hourBox.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    }

    /**
     * when clicked it tries to load the event, which can be accepted if the data introduced is valid
     */
    @FXML
    public void onCreateEventClicked() {
        Timestamp timestamp;
        if(datePicker.getValue() == null)
            timestamp = null;
        else {
            timestamp = new Timestamp(datePicker.getValue().toEpochDay() * Constants.MILLISECONDS_IN_A_DAY
            + minuteBox.getValue() * Constants.MILLISECONDS_IN_A_MINUTE +
                    hourBox.getValue() * Constants.MILLISECONDS_IN_AN_HOUR - 120 * Constants.MILLISECONDS_IN_A_MINUTE);
        }

        try {
            Controller.getInstance().createEvent(
                    id_user,
                    titleBox.getText(),
                    descriptionBox.getText(),
                    timestamp);
            ((Stage) createEventButton.getScene().getWindow()).close();
        } catch (ValidateException | BusinessException | SQLException | RepoException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
