package com.example.lab_gui;

import Control.Controller;
import Domain.UserDTO;
import Exceptions.BusinessException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class Pdf {
    @FXML
    public TextField pathBox;
    @FXML
    public TextField filenameBox;
    @FXML
    public DatePicker dateStart;
    @FXML
    public DatePicker dateEnd;
    @FXML
    public Label optionLabel;
    @FXML
    public Button chooseFileButton;
    private int option;
    private int id;
    private UserDTO friend;


    void setUp(int option, int id, UserDTO friend) {
        this.option = option;
        this.id = id;
        this.friend = friend;
    }

    @FXML
    public void onCreatePDFClicked(ActionEvent actionEvent) throws SQLException, IOException, BusinessException {
        String err = "";
        Date date_start = new Date(dateStart.getValue().toEpochDay());
        Date date_end = new Date(dateEnd.getValue().toEpochDay());
        File file = null;
        try {
            file = new File(pathBox.getText() + filenameBox.getText());
        } catch(Exception error) {
            err += "The path is not good!";
        }
        if(err.length() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(err);
            alert.show();
            return;
        }
        if(option == 1)
            Controller.getInstance().friendsAndMessagesBetweenADatePDF(
                    id,
                    date_start,
                    date_end,
                    file
            );
        else
            Controller.getInstance().messagesFromAFriendBetweenDatesPDF(
                    date_start,
                    date_end,
                    id,
                    friend,
                    file
            );
    }

    @FXML
    public void onChooseFileClicked(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(chooseFileButton.getScene().getWindow());
        pathBox.setText(file.getAbsolutePath());
    }
}
