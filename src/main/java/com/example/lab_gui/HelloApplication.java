package com.example.lab_gui;


import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 *  the running application
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage)  {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 320, 400);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.show();
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

        stage.setScene(scene);
        stage.setTitle("Log in");

        stage.setWidth(450);
        stage.setHeight(400);
        stage.setResizable(false);

        stage.show();
    }

    /**
     * the main method which actually runs the application
     * @param args environment parameters
     */
    public static void main(String[] args) {
        launch();
    }
}