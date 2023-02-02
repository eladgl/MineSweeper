package mines;
import javafx.scene.control.Button;
//this class is an extension of button, each generated button in the grid will be of this type
//to ease our use of the logical implementation of Mines.Java
public class TileButton  extends Button {
	private int x,y;
	private boolean flag;
	//constructor for TileButton
	public TileButton(int x, int y, boolean flag){
		this.x = x;
		this.y = y;
		this.flag = flag;
	}
	//getter for x
	public int getX() {
		return x;
	}
	//getter for y
	public int getY() {
		return y;
	}
	//getter for flag
	public boolean getFlag() {
		return flag;
	}
	//setter for flag
	public void setFlag() {
		flag = !flag;
	}
}
