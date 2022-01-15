package com.example.lab_gui;

import Control.Controller;
import Domain.UserDTO;
import Exceptions.BusinessException;
import Utils.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * a javafx controller class for the PDF creating menu
 */
public class PdfController {
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
    @FXML
    public Button createPDFButton;
    private int option;
    private int id = 1;
    private UserDTO friend;


    /**
     * @param option the option for the PDF, it can represent messages with a friend or just app activity
     * @param id the ID of the user whose activity we'll report
     * @param friend the friend with which the user conversed if we print the conversation
     */
    void setUp(int option, int id, UserDTO friend) {
        this.option = option;
        this.id = id;
        this.friend = friend;
        if(option == 1)
            optionLabel.setText("Create a PDF for all your messages and friendships between a date");
        else
            optionLabel.setText("Create a PDF for all your messages from a friend between a date");
    }

    /**
     * create the initial build for the application
     */
    @FXML
    void initialize() {
        if(option == 1)
            optionLabel.setText("Create a PDF for all your messages and friendships between a date");
        else
            optionLabel.setText("Create a PDF for all your messages from a friend between a date");
    }

    /**
     *  creates a PDF when clicked
     */
    @FXML
    public void onCreatePDFClicked()  {
        String err = "";
        Date dateStart = null;
        Date dateEnd = null;
        System.out.println(dateStart);
        if(this.dateStart.getValue() != null && this.dateEnd.getValue() != null) {
            dateStart = new Date(this.dateStart.getValue().toEpochDay() * Constants.MILLISECONDS_IN_A_DAY);
            dateEnd = new Date(this.dateEnd.getValue().toEpochDay() * Constants.MILLISECONDS_IN_A_DAY);
        }
        else
            err += "The dates must be initialized!\n";
        if(pathBox.getText().length() == 0)
            err += "The path must not be null!\n";
        if(filenameBox.getText().length() == 0)
            err += "The filename must not be null!\n";
        File file = null;
        try {
            if(filenameBox.getText().length() != 0 && filenameBox.getText().length() != 0) {
                file = new File(pathBox.getText() + "/" + filenameBox.getText() + ".pdf");
            }
        } catch(Exception error) {
            err += "The path is not good!";
        }
        if(err.length() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(err);
            alert.show();
        }
        else if(option == 1) {
            try {
                Controller.getInstance().friendsAndMessagesBetweenADatePDF(
                        id,
                        dateStart,
                        dateEnd,
                        file
                );
                Stage stage = (Stage) createPDFButton.getScene().getWindow();
                System.out.println("ok");
                stage.close();
            } catch (IOException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
        else {
            try {
                Controller.getInstance().messagesFromAFriendBetweenDatesPDF(
                        dateStart,
                        dateEnd,
                        id,
                        friend,
                        file
                );
                Stage stage = (Stage) createPDFButton.getScene().getWindow();
                System.out.println("ok");
                stage.close();
            } catch (IOException | BusinessException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    /**
     * allows the user to choose the path where the PDF will be saved
     */
    @FXML
    public void onChooseFileClicked() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(chooseFileButton.getScene().getWindow());
        pathBox.setText(file.getAbsolutePath());
    }
}
