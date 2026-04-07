package com.example;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage myStage) throws Exception {

        // Initialize a group of nodes
        Group root = new Group();
        
        // Initialize a scene that needs a node as argument, by convention call it root
        Scene myScene = new Scene(root, Color.BLACK); 

        // Give the stage a title
        myStage.setTitle("My First JavaFX Stage");

        // Set the scene into the stage
        myStage.setScene(myScene);
        // Always end the start method with showing a stage
        myStage.show();

    }
    public static void main(String[] args) {

        launch(args);

    }

}