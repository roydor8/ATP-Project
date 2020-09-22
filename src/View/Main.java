package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {
    IModel model;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyView.fxml"));
        Parent root = fxmlLoader.load();
        MyViewController view = fxmlLoader.getController();
//        ASearchingAlgorithm solver = new BestFirstSearch();
//        AMazeGenerator generator = new MyMazeGenerator();

        model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        //-----------------
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        //--------------
        primaryStage.setTitle("The Love Maze <3");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        double scWidth = screenSize.getWidth();
        double scHeight = screenSize.getHeight();

        primaryStage.setWidth(scWidth-100);
        primaryStage.setHeight(scHeight-100);

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1100);
        primaryStage.setScene(new Scene(root, 900, 600));

        primaryStage.show();
    }

    @Override
    public void stop(){
        model.stopServers();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

