package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.ISearchable;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {

    void generateMaze(int row, int col);

    Maze getMaze();

    void updateCharacterLocation(int direction);

    Position getCharPosition();

    void assignObserver(Observer obs);

//    void solveMaze(SearchableMaze maze);

    Solution getSolution();

    boolean isArrivedToGoal();

    void startServers();

    void loadGame(String path);

    void saveGame(String path);

    void stopServers();
}

