package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import Server.*;
import Client.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel {
    Server generateMazeServer;
    Server solveMazeServer;
    private Maze maze;
    private Position charPosition;
    private Solution solution;
    private boolean arrivedToGoal;

    public MyModel() {
        this.generateMazeServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        this.solveMazeServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        this.maze=null;
        this.solution = null;

    }

    @Override
    public void startServers() {
        this.generateMazeServer.start();
        this.solveMazeServer.start();
    }

    public boolean isArrivedToGoal() {
        return arrivedToGoal;
    }

    public void updateCharacterLocation(int direction){
        int curCharRow = charPosition.getRowIndex();
        int curCharCol = charPosition.getColumnIndex();
        switch(direction) {
            case 8: //up
                if (curCharRow != 0 && maze.getData()[curCharRow - 1][curCharCol] == 0) {
                    charPosition.setRow(curCharRow- 1);
                }
                break;
            case 2: //down
                if (curCharRow != maze.getRows() - 1 && maze.getData()[curCharRow + 1][curCharCol] == 0)
                    charPosition.setRow(curCharRow + 1);
                break;
            case 6: //right
                if (curCharCol != maze.getColumns() - 1 && maze.getData()[curCharRow][curCharCol + 1] == 0)
                    charPosition.setColumn(curCharCol + 1);
                break;
            case 4: //left
                if (curCharCol != 0 && maze.getData()[curCharRow][curCharCol - 1] == 0)
                    charPosition.setColumn(curCharCol - 1);
                break;
            case 1: //down+left
                if (curCharRow != maze.getRows() - 1 && curCharCol != 0 && maze.getData()[curCharRow + 1][curCharCol - 1] == 0) {
                    if(maze.getData()[curCharRow + 1][curCharCol] == 0 || maze.getData()[curCharRow][curCharCol - 1] == 0){
                        charPosition.setRow(curCharRow + 1);
                        charPosition.setColumn(curCharCol - 1);
                    }
                }
                break;
            case 3: //down+right
                if (curCharRow != maze.getRows() - 1 && curCharCol != maze.getColumns() - 1 && maze.getData()[curCharRow + 1][curCharCol + 1] == 0) {
                    if(maze.getData()[curCharRow + 1][curCharCol] == 0 || maze.getData()[curCharRow][curCharCol + 1] == 0){
                        charPosition.setRow(curCharRow + 1);
                        charPosition.setColumn(curCharCol + 1);
                    }
                }
                break;
            case 7: //up+left
                if (curCharRow != 0 && curCharCol != 0 && maze.getData()[curCharRow - 1][curCharCol - 1] == 0){
                    if(maze.getData()[curCharRow - 1][curCharCol] == 0 || maze.getData()[curCharRow][curCharCol - 1] == 0){
                        charPosition.setRow(curCharRow - 1);
                        charPosition.setColumn(curCharCol - 1);
                    }
                }
                break;
            case 9: //up+right
                if(curCharRow != 0 && curCharCol != maze.getColumns() - 1 && maze.getData()[curCharRow - 1][curCharCol + 1] == 0) {
                    if(maze.getData()[curCharRow - 1][curCharCol] == 0 || maze.getData()[curCharRow][curCharCol + 1] == 0){
                        charPosition.setRow(curCharRow- 1);
                        charPosition.setColumn(curCharCol + 1);
                    }
                }
                break;
        }

        if(charPosition.equals(maze.getGoalPosition())){
            arrivedToGoal = true;
        }
        setChanged();
        notifyObservers();
    }

    public Position getCharPosition() {
        return charPosition;
    }

    @Override
    public void assignObserver(Observer obs) {
        this.addObserver(obs);
    }

    private void solveMaze() {
        solveMazeClient();
        setChanged();
        notifyObservers();
    }

    @Override
    public Solution getSolution() {
        return this.solution;
    }

    public void generateMaze(int row, int col){
        this.arrivedToGoal = false;
        generateMazeClient(row, col);
        this.charPosition = maze.getStartPosition();
        solveMaze();
        setChanged();
        notifyObservers("generated");
    }



    private void generateMazeClient(int rows, int cols) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        Maze maze = new Maze(decompressedMaze);
                        setMaze(maze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void solveMazeClient() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        setSolution(mazeSolution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private void setSolution(Solution solution) {
        this.solution=solution;
    }

    private void setMaze(Maze maze) {
        this.maze=maze;
    }


    public Maze getMaze() {
        return maze;
    }

    @Override
    public void loadGame(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            ObjectInputStream objIn = new ObjectInputStream(in);
            Maze loadedMaze = (Maze) objIn.readObject();
            objIn.close();
            in.close();
            if (loadedMaze != null) {
                setMaze(loadedMaze);
                this.charPosition = this.maze.getStartPosition();
                setChanged();
                notifyObservers("loaded");
            }else{
                setChanged();
                notifyObservers("load failed");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void saveGame(String path) {
        if (maze != null) {
            try {
                FileOutputStream out = new FileOutputStream(path);
                ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.flush();
                objOut.writeObject(maze);
                objOut.close();
                out.close();
                setChanged();
                notifyObservers("saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setChanged();
            notifyObservers("save failed");
        }
    }



    @Override
    public void stopServers() {
        this.solveMazeServer.stop();
        this.generateMazeServer.stop();
    }


}
