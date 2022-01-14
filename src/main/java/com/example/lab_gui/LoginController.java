package com.example.lab_gui;

import Control.Controller;
import Domain.User;
import Domain.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    public TextField usernameBox;
    @FXML
    public TextField passwordBox;
    @FXML
    public Button loginButton;
    @FXML
    public Button signupButton;


    @FXML
    public void onLoginClicked()  {
        String username = usernameBox.getText();
        String password = passwordBox.getText();
        User user = null;

        String err = "";
        if(username.length() == 0)
            err += "The username box must not be empty!\n";
        else if(password.length() == 0)
            err += "The password box must not be empty!\n";
        else {
            try {
                user = Controller.getInstance().login(username, password);
            } catch (SQLException | NoSuchAlgorithmException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            if (user == null)
                err += "Authentication error!\n";
        }


        if(err.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(err);
            alert.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Parent parent;
            try {
                parent = fxmlLoader.load();
                Scene scene = new Scene(parent, 320, 400);
                HelloController helloController = fxmlLoader.getController();
                helloController.login(new UserDTO(user.getId(), user.getFirstName(), user.getSurname(),user.getUsername()));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setResizable(true);
                stage.setMinWidth(964.8);
                stage.setMinHeight(582.4);
                stage.setTitle("Webber");
                stage.setScene(scene);
                stage.show();
                stage.setMaximized(true);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void onSignUpClicked()  {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup.fxml"));
        Parent parent;
        try {
            parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 320, 400);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            stage.setTitle("Sign Up");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public void onLoginViaEnter(KeyEvent keyEvent)  {
        if(keyEvent.getCode()== KeyCode.ENTER){
            onLoginClicked();
        }
    }
}
