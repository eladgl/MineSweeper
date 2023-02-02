package mines;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Mines {
	private boolean[][] mineField; //false - no mine, true - there is a mine
	private boolean[][] flags; //false - no flag, true - yes flag
	private boolean[][] opens; //false - not open, true - open
	private int numMines, openTiles, width, height;
	private boolean showAll;
	//private Set<Tile> mines = new TreeSet<>();
	private Random r = new Random();
	//constructor for Mine field, initializes game with exactly numMines and screen at given height and width
	public Mines(int height,int width,int numMines) {
		int row,col;
		mineField = new boolean[height][width];
		this.width = width;
		this.height = height;
		flags = new boolean[height][width];
		opens = new boolean[height][width];
		while(this.numMines < numMines) { //add random mines and add until all mines were add randomly
			row = r.nextInt(height);
			col = r.nextInt(width);
			addMine(row,col);
		}
	}
	//add mine at given location, return true if mine was there, if was added or if range not OK - return false
	public boolean addMine(int i, int j) {
		if(!checkRange(i, j) &&  opens[i][j])
			return false;
		if(mineField[i][j])
			return true;
		mineField[i][j] = true;
		numMines++;
		return false;
	}
	//signals that user opened a tile, returns true if not mine else false. recursively open adjacent tiles which aren't mines
	public boolean open(int i, int j) {
		Tile temp = new Tile(i,j);
		if(!checkRange(i,j) || flags[i][j]) //if not in range and there is a flag on the tile or the tile already open
			return false;
		else if(opens[i][j]) 				//if is already open return true
			return true;
		else {
			if(mineField[i][j])
				return false; 		//it is a mine
			openTiles++; 			//begin opening all 8 sides around a give point, checkRange will tell us if the given i,j is in range
			opens[i][j] = true; 	//mark the tile as open
			
			if(temp.mineInNeighbors()) 				//check if there is a mine in the neighbors
				return true;
			for(Tile tile : temp.getNeighbors())	//iterate neighbors
				open(tile.getX(), tile.getY());
		}
		return true;
	}

	//toggle the flag, put 2 if no flag, put 1 if there is a flag, if tile is already open don't do anything
	public void toggleFlag(int x, int y) {
		if(checkRange(x, y) && !opens[x][y]) //if tile is in range and yet to be opened then you can toggle its flag
			flags[x][y] = !flags[x][y];
	}

	//return true if all non mine tiles are open, else false
	public boolean isDone() {
		return openTiles == (height * width - numMines);
	}
	//returns string representation of the point(i,j). 
	//if tile is not open and has a flag will return "F"
	//if tile is not open and does not have a flag will return "."
	//if a tile is open and is a mine will return "X"
	//else number of mines neighboring it will be returned
	public String get(int i, int j) {
		int num;
		if(!showAll)
			if(!opens[i][j]) //if tile was not opened yet
				return flags[i][j] ? "F" : ".";		
		if(mineField[i][j])
			return "X";
		num = numAround(i,j);
		if(num == 0)
			return " ";
		return Integer.toString(num);
	}
	//checks if a point is in correct range, to not duplicate code
	private boolean checkRange(int i, int j) {
		return (0 <= i && i < height) &&( 0 <= j && j < width);
	}
	//setter for showAll
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
	//returns number of mines that are neighboring a tile
	private int numAround(int i, int j) {
		int count = 0; //initialize a counter
		for(Tile tile : new Tile(i,j).getNeighbors())
			if(mineField[tile.getX()][tile.getY()])
				count++;
		return count;
	}
	//returns a string representation of the mineField
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++)
				str.append(get(row,col));
			str.append("\n");
		}
		return str.toString();
	}
	//inner class 
	private class Tile implements Comparable<Tile>{
		private int x, y;
		//constructor for tile
		public Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override //compareTo for Tile
		public int compareTo(Tile o) {
			return o.toString().compareTo(toString()); //use String compare to for this

		}
		@Override //toString implementation 
		public String toString() {
			return String.format("(%d,%d)", x,y);
		}
		public int getX() { //getter for x
			return x;
		}
		public int getY() { //getter for y
			return y;
		}
		//returns a TreeSet of a Tile's neighbors
		public Set<Tile> getNeighbors(){
			Set<Tile> neighbors = new TreeSet<Tile>();
			for (int row = x - 1; row <= x + 1; row++) { //check if at least one of the sides have a mine, if so return false and don't continue
				for (int col = y - 1; col <= y + 1; col++) {
					if(row == x && col == y) //if same as tile continue
						continue;
					if(checkRange(row,col))
						neighbors.add(new Tile(row, col));
				}
			}
			return neighbors;
		}
		//checks if there is a mine among the neighbors
		private boolean mineInNeighbors() {
			for(Tile tile : getNeighbors())
				if(mineField[tile.getX()][tile.getY()])
					return true;
			return false;
		}
	}
}
