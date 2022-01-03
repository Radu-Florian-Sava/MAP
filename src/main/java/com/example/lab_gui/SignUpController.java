package com.example.lab_gui;

import Control.Controller;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SignUpController {
    @FXML
    public Button signUpButton;
    @FXML
    public TextField firstnameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;

    @FXML
    public void signUpClicked(ActionEvent actionEvent) throws IOException {
        String error = "";
        try {
            Controller.getInstance().signup(
                    firstnameField.getText(),
                    surnameField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    confirmPasswordField.getText());
        }
        catch(BusinessException | SQLException | ValidateException | RepoException businessException)
        {
            error = businessException.getMessage();
        }
        if(error.length() != 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setHeaderText(error);
            alert.show();
        }
        else
        {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 400);

            Stage stage = (Stage) signUpButton.getScene().getWindow();

            stage.close();

            stage.setScene(scene);
            stage.setMinHeight(200);
            stage.setMinWidth(271);

            stage.setMaxHeight(200);
            stage.setMaxWidth(271);
            stage.setMaxHeight(200);
            stage.setHeight(200);
            stage.setWidth(271);

            stage.show();
        }
    }
}
