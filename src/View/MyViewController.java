package View;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;


public class MyViewController implements Observer, Initializable, IView{
    @FXML
    public BorderPane mainPane;
    @FXML
    public TextField textField_playerName;
    @FXML
    public TextField textField_mazeRows;
    @FXML
    public TextField textField_mazeColumns;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Label lbl_player_row;
    @FXML
    public Label lbl_player_column;
    @FXML
    public Button mute_Button;
    boolean mute;

    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();

    private MyViewModel vm;
    private Maze maze;
    private Solution solution;
    private MediaPlayer mediaPlayer;
    private String playerName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_player_row.textProperty().bind(update_player_position_row);
        lbl_player_column.textProperty().bind(update_player_position_col);
    }

    public void setViewModel(MyViewModel vm){
        this.vm = vm;
    }

    public void set_update_player_position_row(String update_player_position_row) {
        this.update_player_position_row.set(update_player_position_row);
    }

    public void set_update_player_position_col(String update_player_position_col) {
        this.update_player_position_col.set(update_player_position_col);
    }



    public void generateMaze()
    {
        mute_Button.setDisable(false);
        String rows = textField_mazeRows.getText();
        String cols = textField_mazeColumns.getText();
        setPlayerName(String.valueOf(textField_playerName.getText()));
        if(!vm.generateMaze(rows,cols)){
            showAlert("Please Enter valid numbers");
            return;
        }
        drawMaze();
        backgroundMusic();
    }

    private void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    public void showHint()
    {
        ArrayList<AState> path = this.solution.getSolutionPath();
        ArrayList<MazeState> positionsList = new ArrayList<>();
        for (AState state : path) {
            positionsList.add((MazeState)state);
        }
        mazeDisplayer.fillSolutionPath(positionsList);
    }

    public void showAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);;
        alert.show();
    }


    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            generateMaze();
        }
        vm.moveCharacter(keyEvent);
        keyEvent.consume();

    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {
            if (maze == null) {
                this.maze = vm.getMaze();
                set_update_player_position_row(String.valueOf(vm.getCharPosition().getRowIndex()));
                set_update_player_position_col(String.valueOf(vm.getCharPosition().getColumnIndex()));
                this.solution = vm.getSolution();
                drawMaze();
                mazeDisplayer.requestFocus();
            } else {
                if (this.maze == vm.getMaze()) { // character moved
                    Position posFromVM = vm.getCharPosition();
                    set_update_player_position_row(posFromVM.getRowIndex() + "");
                    set_update_player_position_col(posFromVM.getColumnIndex() + "");
                    mazeDisplayer.set_player_position(posFromVM);
                    if(vm.isArrivedToGoal()){
                        mediaPlayer.pause();
                        goalReached();
                    }

                }
                else { // generate Maze
                    this.maze = vm.getMaze();
                    set_update_player_position_row(String.valueOf(vm.getCharPosition().getRowIndex()));
                    set_update_player_position_col(String.valueOf(vm.getCharPosition().getColumnIndex()));
                    this.solution = vm.getSolution();
                    drawMaze();
                    mazeDisplayer.requestFocus();
                }
            }
        }
    }

    private void goalReached(){
        successSong();
        mazeDisplayer.clearCanvas();
        mazeDisplayer.win();
        showAlert("Good Job, " + this.playerName);

    }


    public void drawMaze(){
        mazeDisplayer.drawMaze(maze);
    }

    public void showAbout(ActionEvent actionEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("About Information");
            stage.setScene(new Scene(root , 700, 500));
            stage.setMinHeight(500);
            stage.setMinWidth(700);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showInstructions(ActionEvent actionEvent) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Instructions.fxml"));
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Instructions for the stupid LOVER");
            stage.setScene(new Scene(root , 700, 500));
            stage.setMinHeight(500);
            stage.setMinWidth(700);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exitProgram(ActionEvent actionEvent) {
        String question = "Are you sure you want to exit the game?";
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(question);
        Optional<ButtonType> ok = alert.showAndWait();
        if(ok.get() == ButtonType.OK)
            System.exit(0);
    }

    public void newMaze(ActionEvent actionEvent) {
        mazeDisplayer.clearCanvas();
        backgroundMusic();
        showAlert("Please choose new maze size for a new Journey ! ");
    }


    public void Zoom(ScrollEvent scrollEvent) {
        double zoomSize = 1.05;

        if(scrollEvent.isControlDown()){

            if(scrollEvent.getDeltaY()<0){
                zoomSize = 2.0 - zoomSize;
            }
            mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*zoomSize);
            mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*zoomSize);
        }
    }


    public void loadGame(ActionEvent actionEvent) {
        Stage loadStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a maze to load");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Maze files(.maze)" , ".maze"));
        File filetoLoad = fileChooser.showOpenDialog(loadStage);
        if(filetoLoad != null){
            String path = filetoLoad.getPath();
            vm.loadGame(path);
        }
    }

    public void saveGame(ActionEvent actionEvent) {
        Stage saveStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("My_Maze");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Maze files(.maze)", ".maze");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File fileToSave = fileChooser.showSaveDialog(saveStage);
        if (fileToSave != null) {
            String path = fileToSave.getPath();
            vm.saveGame(path);
        }

    }

    public void setMute(ActionEvent actionEvent) {
        if (mute) {
            mute_Button.setText("Mute");
            mediaPlayer.play();
            mute = false;
        } else {
            mute_Button.setText("Play Music");
            mediaPlayer.pause();
            mute = true;
        }
    }

    private void playMusic(String path) {
        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
        }
        javafx.scene.media.Media sound = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);

        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });

    }

    public void backgroundMusic(){
        if (mute) {
            mute = false;
            mute_Button.setText("Mute");
        }
        String musicFile = "./Resources/Clips/backgroundSongFull.mp3";
        playMusic(musicFile);

    }

    private void successSong() {
        if(mute){
            mute=false;
            mute_Button.setText("Mute");
        }
        String s = "./Resources/Clips/goalVideo.mp4";
        playMusic(s);
    }


    public void showConfig(ActionEvent actionEvent) {
        showAlert("MazeGenerator=MyMazeGenerator\n" +
                "SolvingMazeAlgorithm=BestFirstSearch\n" +
                "ThreadPool=5");
    }
}


