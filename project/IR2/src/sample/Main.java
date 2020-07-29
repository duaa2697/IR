package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        IrData x = new IrData("root1","root","ir");
        x.ConnectToServer();
        x.selectDataBase();
        x.sss();//to creat table
        InputManager xx =new InputManager("C:\\Users\\ASUS\\Desktop\\ir_m\\IR Homework\\corpus");
        xx.scanFiles();
        IrData y= new IrData("root1","root","ir");
        y.ConnectToServer();
        y.selectDataBase();
        for(int i=1; i < 424; i++ ){
            y.set_len_doc(i);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
