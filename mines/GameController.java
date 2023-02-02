package mines;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;


public class GameController{
	private Stage stage;
	private Mines mineMap;
	private TileButton[][] btns;
	private int width, height, numMines;
	private GridPane grid;
	private boolean startPlay = false, stopFlag = false;
	private Timer tm;
	
	@FXML
	private TextField map_height;

	@FXML
	private TextField map_width;

	@FXML
	private TextField num_of_mines;

	@FXML
	private Button reset;

	@FXML
	private BorderPane root;

	@FXML
	private Label timeLabel;
	

	
	@FXML //resetGame on action event
	void ResetGame(ActionEvent event) {
		if(!checkInputs())
			return;
		cancelTimer();
		timeLabel.setText("0");
		CreateMap();	
	}
	
	
	//generate map and create the game
	private void CreateMap() {
		//timeLabel.textProperty().bind(stopwatch.getSspTime());
		List<RowConstraints> rows = new ArrayList<>();
		List<ColumnConstraints> cols = new ArrayList<>();
		width = Integer.parseInt(map_width.getText());
		height = Integer.parseInt(map_height.getText());
		numMines = Integer.parseInt(num_of_mines.getText());
		mineMap = new Mines(height, width, numMines);  		//create a new mine field map
		btns = new TileButton[height][width];				//create 2d array of TileButtons
		initTileButtons();
		grid = new GridPane();
		//grid.setpre
		grid.setPadding(new Insets(10,10,10,10));
		initGrid(rows, cols);
		begin();
		grid.setGridLinesVisible(true);
		grid.getRowConstraints().addAll(rows);
		grid.getColumnConstraints().addAll(cols);
		root.setCenter(grid);
		root.centerProperty();
		grid.setAlignment(Pos.CENTER);
		root.setMinWidth(grid.getPrefWidth());
		root.autosize();
		stage.sizeToScene();
	}
	//add buttons to the grid
	private void begin() {
		for(int row = 0; row < height; row++)
			for(int col = 0; col < width; col++)
				grid.add(btns[row][col], col, row);
	}

	@FXML //make sure all is initialized and had time to load
	void initialize() {
		assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'MinesFX.fxml'.";
		assert map_height != null : "fx:id=\"map_height\" was not injected: check your FXML file 'MinesFX.fxml'.";
		assert map_width != null : "fx:id=\"map_width\" was not injected: check your FXML file 'MinesFX.fxml'.";
		assert num_of_mines != null : "fx:id=\"num_of_mines\" was not injected: check your FXML file 'MinesFX.fxml'.";
		assert reset != null : "fx:id=\"reset\" was not injected: check your FXML file 'MinesFX.fxml'.";
		assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'MinesFX.fxml'.";
	}

	//setter for stage
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	//begin initializations
	//create places for the buttons in the grid
	private void initGrid(List<RowConstraints> r, List<ColumnConstraints> c) {
		int i;
		for(i = 0; i< height; i++)
			r.add(new RowConstraints());
		for(i = 0; i < width; i++) 
			c.add(new ColumnConstraints());
	}
	//update tile with appropriate image
	private void Update() {
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				if(!btns[row][col].getFlag()) { //only unflagged flags
					String  letter = mineMap.get(row, col);
					if(letter != "X" && letter != " " && letter != "F" && letter != ".")
						putImage(row,col, new Image("/img/"+letter+".png"));
					else
						btns[row][col].setText(mineMap.get(row, col));
				}
			}
		}
	}
	//construct buttons
	private void initTileButtons() {
		for(int row = 0; row<height; row++) {
			for(int col = 0; col<width; col++) {
				final int currentRow = row, currentCol = col;	 //in order to save old values to be passed to event handlers
				btns[row][col] = new TileButton(row,col, false); //sending to constructor
				btns[row][col].setMaxWidth(50);					 //setting width an height per each button
				btns[row][col].setMaxHeight(50);
				btns[row][col].autosize();						 //resizing it automatically for screen
				btns[row][col].setPrefHeight(Double.MAX_VALUE);
				btns[row][col].setPrefWidth(Double.MAX_VALUE);
				btns[row][col].setText(mineMap.get(row, col));   //getting initial text to show on each button
				btns[row][col].setStyle("-fx-font-size: 15px;");
				btns[row][col].setOnMouseClicked(new EventHandler<MouseEvent>() {	//add each button an event handler
					@Override
					public void handle(MouseEvent event) {
						if(event.getButton() == MouseButton.PRIMARY)			//if left click - both were split to improve readability 
							LeftClick(currentRow, currentCol);
						else if(event.getButton() == MouseButton.SECONDARY)		//if right click is made
							RightClick(currentRow, currentCol);
					}
				});
			}
		}
	}
	//end initials
	//removing events from all buttons
	private void removeEvents() {
		for(int i=0; i<height; i++)
			for(int j=0; j < width; j++)
				btns[i][j].setEventDispatcher(null);
	}
	//pop-ups a window announcing a win
	private void windowPopUp() {
		HBox secondRoot = new HBox();
		secondRoot.setStyle("-fx-alignment: center;");
		Label winMessage = new Label("OMG You have just won!");
		winMessage.autosize();
		winMessage.setStyle("-fx-font-size: 30px;"
				+ "-fx-text-fill: #333333;"
				+ "-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );"
				+ "-fx-text-alignment: center;");
		winMessage.setMaxWidth(Double.MAX_VALUE);
		winMessage.setAlignment(Pos.CENTER);
		secondRoot.getChildren().addAll(winMessage);
		Scene scene = new Scene(secondRoot, 400, 200);
		Stage secondStage = new Stage();
		secondStage.setScene(scene);
		secondStage.show();
	}
	//show entire board
	private void ShowAll() {
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++)
				if(btns[i][j].getText() != null && mineMap.get(i, j) == "X") //maybe here to change
					putImage(i,j, new Image("/img/bomb.png"));
	}
	//put an image of on a tile
	private void putImage(int i, int j, Image img) {
		btns[i][j].setText("");
		ImageView view = new ImageView(img);
		view.setFitWidth(25);
		view.setFitHeight(25);
		btns[i][j].setGraphic(view);
	}
	//right click method to toggle flag
	private void RightClick(int i, int j) {
		beginTakingTime();
		mineMap.toggleFlag(i, j);
		if(!btns[i][j].getFlag()) { //it was false and now it is not
			btns[i][j].setText("");
			putImage(i, j, new Image("/img/flag.png"));
			btns[i][j].setFlag();
		}
		else if(btns[i][j].getFlag()) {
			btns[i][j].setGraphic(null);		//remove image
			btns[i][j].setFlag();
		}
	}
	//left button click - open tile if mine loose, else check if win
	private void LeftClick(int i, int j) {
		beginTakingTime();
		if(!btns[i][j].getFlag()) {			//don't allow button to be clicked if its flagged
			if(!mineMap.open(i, j)) { 		//this checks if we lost
				putImage(i, j, new Image("/img/bomb.png"));				//put a mine image up
				removeEvents();									//remove all play buttons' events so non will be playable until reset
				mineMap.setShowAll(true);						//show map
				cancelTimer();							    //stop the timer
				ShowAll();
				return;
			}
			Update();										//if in step we did not loose, show rest of screen
			if(mineMap.isDone()) {
				cancelTimer();								//stop the timer
				removeEvents();								//when we win remove events from all buttons
				windowPopUp();								//announce winning
			}
		}
	}
	//create new timer method and schedule it to be ran every second
	private void beginTakingTime() {
		if(!startPlay) {
			tm = new Timer();
			tm.scheduleAtFixedRate(new subtimer(),0,1000);
			startPlay = true;
		}
	}
	//An inner class which is a TimeTask to be run periodically and endlessly
	private class subtimer extends TimerTask {
		int count = 0;
		@Override
		public void run() {
			if(!stopFlag)
			Platform.runLater(() ->{
				timeLabel.setText(Integer.toString(++count));
			});
		}
	}
	//method to cancel the Timer thread and set all the needed flags as false
	private void cancelTimer() {
		startPlay = false;
		stopFlag = false;
		if(tm != null)
			tm.cancel();
	}
	//check values are correct
	private boolean checkInputs(){
		int tmpWidth, tmpHeight, tmpMines;
		try {
			tmpWidth = Integer.parseInt(map_width.getText());
			tmpHeight = Integer.parseInt(map_height.getText());
			tmpMines = Integer.parseInt(num_of_mines.getText());
		}catch(NumberFormatException e) {
			return false;
		}
		if(tmpMines < 0 || tmpMines > tmpWidth * tmpHeight)
			return false;
		if(tmpWidth <= 0 || tmpHeight < 0)
			return false;
		return true;
			
	}
}


