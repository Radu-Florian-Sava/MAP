package com.example.lab_gui;

import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 400);

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

    public static void main(String[] args) {
        launch();
    }
}