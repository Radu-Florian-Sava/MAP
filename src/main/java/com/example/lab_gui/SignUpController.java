package com.example.lab_gui;

import Control.Controller;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Utils.Hasher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 *  a javafx controller class for the signup page
 */
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

    /**
     *  when the signUp button is clicked it tries to go back to the login page
     *  or alerts the user if the data he introduced were wrong
     */
    @FXML
    public void signUpClicked() {
        String error = "";
        try {
            Controller.getInstance().signup(
                    firstnameField.getText(),
                    surnameField.getText(),
                    usernameField.getText(),
                    Hasher.hash(passwordField.getText()),
                    Hasher.hash(confirmPasswordField.getText()));
        }
        catch(BusinessException | SQLException | ValidateException | RepoException | NoSuchAlgorithmException businessException)
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
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load(), 320, 400);
                Stage stage = (Stage) signUpButton.getScene().getWindow();
                stage.close();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    /**
     * @param keyEvent if ENTER is pressed tries to sign up the user
     */
    public void signUpViaEnter(KeyEvent keyEvent)  {
        if(keyEvent.getCode()== KeyCode.ENTER){
            signUpClicked();
        }
    }
}
