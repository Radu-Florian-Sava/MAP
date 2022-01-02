package com.example.lab_gui;

import Control.Controller;
import Domain.User;
import Domain.UserDTO;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
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
    public void onLoginClicked(ActionEvent actionEvent) throws IOException, SQLException {
        String username = usernameBox.getText();
        String password = passwordBox.getText();
        User user = null;

        String err = "";
        if(username.length() == 0)
            err += "The username box must not be empty!\n";
        else if(password.length() == 0)
            err += "The password box must not be empty!\n";
        else {
            user = Controller.getInstance().login(username, password);
            if (user == null)
                err += "User does not exist!\n";
        }


        if(err.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(err);
            alert.show();
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 320, 400);
            HelloController helloController = fxmlLoader.getController();

            try {
                helloController.login(new UserDTO(user.getId(), user.getFirstName(), user.getSurname()));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //makes all windows related to this application to use the same icon given by the relative path
            Window.getWindows().addListener((ListChangeListener<Window>) c -> {
                while (c.next()) {
                    for (Window window : c.getAddedSubList()) {
                        if (window instanceof Stage) {
                            ((Stage) window).getIcons().setAll(new Image("file:src/main/resources/Images/Webber.png"));
                        }
                    }
                }
            });

            Stage stage = (Stage) loginButton.getScene().getWindow();

            stage.setTitle("Webber");
            stage.setScene(scene);

            stage.setMinHeight(315);
            stage.setMinWidth(580);

            stage.setMaxHeight(490);
            stage.setMaxWidth(980);
            stage.show();
        }
    }

    public void onSignUpClicked(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 320, 400);

        Stage stage = (Stage) loginButton.getScene().getWindow();

        stage.close();

        stage.setTitle("Sign Up");
        stage.setScene(scene);

        stage.setMinHeight(285);
        stage.setMinWidth(250);

        stage.setMaxHeight(285);
        stage.setMaxWidth(250);
        stage.show();
    }
}
