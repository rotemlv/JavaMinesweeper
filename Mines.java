package mines;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/*
Mines: A class that defines the logic for the Minesweeper game.
Defines a minefield of a user-defined size, where each cell can have some specific
states: flagged,opened,mined.
The game runs according to the infamous minesweeper game rules.
This class supports creating a minefield, adding mines, toggling flags, opening cells, and
showing the current state of the grid (minefield).
In addition, this class supports a UI layer for the Minesweeper game.
 */

public class Mines {
    private static final Random rnd = new Random();
    private final int height,width, numMines;
    private boolean showAll;
    Set<Integer> mines, flagged, opened;

    public Mines(int height, int width, int numMines){
        // containers for different mine states:
        mines = new HashSet<>();
        flagged = new HashSet<>();
        opened = new HashSet<>();
        // initialize class variables
        this.height = height;
        this.width = width;
        this.numMines = numMines;
        showAll = false;
        if(numMines > width * height){
            numMines = width * height; // assumed maximum (can't have more mines than cells)
        }
        for(int i=0;i<numMines;i++){
            int x,y;
            do{
                x=rnd.nextInt(height);
                y=rnd.nextInt(width);
            }while(!addMine(x,y));
        }
    }

    // helper method to flatten (2D->1D) coordinates to an int value (for set operations)
    private int idxForSets(int x, int y){
        return x * width + y;
    }

    // protected helper methods used for logic-UI communication
    protected boolean hasMine(int x, int y){
        return mines.contains(idxForSets(x,y)); // check if cell has mine
    }
    protected boolean isOpen(int x, int y){
        return opened.contains(idxForSets(x,y)); // check if cell is open
    }
    protected boolean isFlagged(int x, int y) {
        return flagged.contains(idxForSets(x,y)); // check if cell is flagged
    }

    // protected method for classic minesweeper concept - in UI version:
    // do not allow first click to set a mine
    protected void moveMine(int x, int y){
        // place the mine someplace else
        int i,j;
        // do not allow first click to be a mine,
        // remove it if mines are everywhere
        // else, swap it with an empty location
        do{
            i=rnd.nextInt(height);
            j=rnd.nextInt(width);
        }while(((i==x && j==y) || hasMine(i,j)) && numMines<width*height);

        mines.remove(idxForSets(x,y));
        mines.add(idxForSets(i,j));
    }

    // helper method for opening a cell
    private boolean setCellOpen(int x, int y){
        if (isOpen(x,y))
            return false;
        opened.add(idxForSets(x,y));
        return true;
    }

    // check if a coordinate tuple is inside the grid
    private boolean inBound(int x, int y){
        return x>=0 && x<height && y>=0 && y<width;
    }

    // add a mine to the grid
    public boolean addMine(int i, int j){
        // returns true if succeeded, else (mine already exists) false
        if(hasMine(i,j))
            return false;
        // set mine
        mines.add(idxForSets(i,j));
        return true;
    }

    // helper method for counting the number of mined neighbors
    private int countMinedNeighbors(int x ,int y){
        int count = 0;
        for(int i=x-1;i<x+2;i++){
            for(int j=y-1;j<y+2;j++){
                // if cell exist, is not origin, and is mined
                if(inBound(i,j) && (i!=x || j != y) && hasMine(i,j)){
                    count++; // increment
                }
            }
        }
        return count;
    }

    // helper method for opening all the neighbors of a cell in the grid
    private void openNeighbors(int x , int y){
        for(int i=x-1;i<x+2;i++){
            for(int j=y-1;j<y+2;j++){
                // if cell exists and is not the origin cell
                if(inBound(i,j) && (i!=x || j != y)){
                    // open it
                    open(i,j);
                }
            }
        }
    }
    // open a cell in the specified coordinates
    // -> recursively open adjacent cells
    // if all are found to be free of mines
    public boolean open(int i, int j){
        // this method returns false if cell was already open (requirement)
        if(hasMine(i,j) || !(setCellOpen(i,j)))
            return false;
        // check if any neighbors are mined
        boolean recurse = countMinedNeighbors(i,j) == 0;
        // open neighbors if allowed
        if(recurse){
            openNeighbors(i,j);

        }
        return true;
    }

    // toggle flag on or off
    public void toggleFlag(int x, int y){
        // try to remove cell from flagged set
        // if cell is not flagged
        if(!flagged.remove(idxForSets(x,y)))
            // set it to flagged
            flagged.add(idxForSets(x,y));

    }

    // check if game has been won -> no unopened un-mined cells exist
    public boolean isDone(){
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                // check for each cell the necessary conditions
                if(!(hasMine(i,j)) && !(isOpen(i,j)))
                    return false;
            }
        }
        return true;
    }

    public String get(int i, int j){
        // check if showAll variable is set to true, or a cell has already been opened
        if(showAll || isOpen(i,j)){
            // if mined, show X
            if(hasMine(i,j))
                return "X";
            // count mines
            Integer count = countMinedNeighbors(i,j);
            // show neighbors count or a space
            return (count.equals(0)) ? " " : count.toString();
        }
        else{
            // if cell is closed, hide it with a '.' or show flag
            return isFlagged(i, j) ? "F" : ".";
        }
    }

    // public method to show all grid cells
    public void setShowAll(boolean showAll){
        this.showAll = showAll;
    }

    // public method to get the entire grid as a string
    public String toString(){
        // using StringBuilder
        StringBuilder b = new StringBuilder();
        // for each cell in grid, use the get method
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                b.append(get(i,j));
            }
            b.append("\n");
        }
        return b.toString();
    }

}
