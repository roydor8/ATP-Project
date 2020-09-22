package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.ISearchable;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;
    private Maze maze;
    private Position charPosition;
    private Solution solution;
    private String playerName;
    private Position startPosition;
    private Position goalPosition;

    private boolean arrivedToGoal;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        this.maze = null;
        this.solution = null;
        this.playerName = null;
        this.arrivedToGoal = false;
    }

    public boolean isArrivedToGoal() {
        return arrivedToGoal;
    }

    public Position getCharPosition() {
        return charPosition;
    }

    public Maze getMaze() {
        return maze;
    }



    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel) {
            if (maze == null) { // generate MAZE
                this.maze = model.getMaze();
                this.charPosition = model.getCharPosition();
                this.solution = model.getSolution();
            } else { // NOT generate Maze
                if (this.maze == model.getMaze()) { //char has been moved
                    this.charPosition = model.getCharPosition();
                    this.arrivedToGoal = model.isArrivedToGoal();
                } else { //a new maze has been generated
                    this.maze = model.getMaze();
                    this.charPosition = model.getCharPosition();
                    this.solution = model.getSolution();
                }
            }
        }

        // notifying the VIEW that we finished
        setChanged();
        notifyObservers();
    }

    public boolean generateMaze(String sRow, String sCol){
        int row;
        int col;
        try{
             row= Integer.parseInt(sRow);
             col = Integer.parseInt(sCol);
        }catch (NumberFormatException | NullPointerException e){
            return false;
        }
        if (row < 2 || col < 2) { return false; }
        model.generateMaze(row, col);
        return true;
    }

    public void moveCharacter(KeyEvent keyEvent){
        int direction = -1;
        switch (keyEvent.getCode()){
            case NUMPAD8:
            case UP:
                direction = 8;
                break;
            case NUMPAD2:
            case DOWN:
                direction = 2;
                break;
            case NUMPAD6:
            case RIGHT:
                direction = 6;
                break;
            case NUMPAD4:
            case LEFT:
                direction = 4;
                break;
            case NUMPAD1:
                direction = 1;
                break;
            case NUMPAD3:
                direction = 3;
                break;
            case NUMPAD7:
                direction = 7;
                break;
            case NUMPAD9:
                direction = 9;
                break;
        }
        if(direction!=-1){model.updateCharacterLocation(direction);}
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void loadGame(String path) {
        model.loadGame(path);
    }

    public void saveGame(String path) {
        model.saveGame(path);
    }
}
