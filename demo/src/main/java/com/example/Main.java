package com.example;

import java.sql.SQLException;

import com.example.model.DatabaseInitializer;
import com.example.model.StudentSeeder;
import com.example.view.App;

public class Main{

    public static void main(String[] args) {

        try {
            DatabaseInitializer.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            StudentSeeder.seed(DatabaseInitializer.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        App.launch(args);

    }

}