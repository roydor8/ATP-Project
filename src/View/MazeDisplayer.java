package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.MazeState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int row_player;
    private int col_player;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNameWin = new SimpleStringProperty();

    private double canvasWidth;
    private double canvasHeight;

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public StringProperty imageFileNameGoalProperty() {
        return imageFileNameGoal;
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public String getImageFileNameWin() {
        return imageFileNameWin.get();
    }

    public StringProperty imageFileNameWinProperty() {
        return imageFileNameWin;
    }

    public void setImageFileNameWin(String imageFileNameWin) {
        this.imageFileNameWin.set(imageFileNameWin);
    }


//    public String getImageFileNameWin() {
//        return imageFileNameWin.get();
//    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }


    public int getRow_player() {
        return row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public void set_player_position(Position position){
        this.row_player = position.getRowIndex();
        this.col_player = position.getColumnIndex();
        draw();
    }


    public void drawMaze(Maze maze)
    {
        this.maze = maze;
        row_player = this.maze.getStartPosition().getRowIndex();
        col_player = this.maze.getStartPosition().getColumnIndex();
        draw();
    }

    public void draw()
    {
        if( maze!=null)
        {
            canvasHeight = getHeight();
            canvasWidth = getWidth();
            int row = maze.getData().length;
            int col = maze.getData()[0].length;
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            graphicsContext.setFill(Color.RED);
            double w,h;
            //Draw Maze
            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no file....");
            }
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(maze.getData()[i][j] == 1) // Wall
                    {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null){
                            graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }else{
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }

                }
            }

            double h_player = getRow_player() * cellHeight;
            double w_player = getCol_player() * cellWidth;
            Image playerImage = null;
            try {
                playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }
            graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);

            double h_goal = maze.getGoalPosition().getRowIndex() * cellHeight;
            double w_goal = maze.getGoalPosition().getColumnIndex() * cellWidth;
            Image goalImage = null;
            try {
                goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image Goal....");
            }
            graphicsContext.drawImage(goalImage,w_goal,h_goal,cellWidth,cellHeight);

        }
    }

    public void clearCanvas(){
        GraphicsContext graphicsContext = getGraphicsContext2D();
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
    }

    public void win()
    {
        try {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            GraphicsContext gc = getGraphicsContext2D();
            Image SolStepImage=new Image(new FileInputStream(getImageFileNameWin()));
            gc.drawImage(SolStepImage, 0, 0, canvasWidth, canvasHeight);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fillSolutionPath(ArrayList<MazeState> path){
        GraphicsContext graphicsContext = getGraphicsContext2D();
        canvasHeight = getHeight();
        canvasWidth = getWidth();
        int row = maze.getData().length;
        int col = maze.getData()[0].length;
        double cellHeight = canvasHeight/row;
        double cellWidth = canvasWidth/col;
        for (MazeState mazeState : path) {
            Position pos = mazeState.getNode();
            if(!pos.equals(maze.getStartPosition()) && !pos.equals(maze.getGoalPosition())){
                int rowInd = pos.getRowIndex();
                int colInd = pos.getColumnIndex();
                double h = rowInd * cellHeight;
                double w = colInd * cellWidth;
                graphicsContext.setFill(Color.WHITE);
                graphicsContext.fillRect(w,h,cellWidth,cellHeight);
            }
        }
    }
}
