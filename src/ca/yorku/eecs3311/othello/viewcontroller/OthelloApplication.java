package ca.yorku.eecs3311.othello.viewcontroller;
import ca.yorku.eecs3311.othello.model.*;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * JavaFX front-end controller for Othello.
 * Acts as the View/Controller in MVC and uses Strategy to swap player behaviors
 * (Human/Random/Greedy) at runtime via UI selections.
 * Patterns:
 * - MVC: acts as View/Controller; owns the Othello model and refreshes the UI.
 * - Strategy: selects Player implementations (Human/Random/Greedy) at runtime from UI choices.
 * - Command: uses OthelloMoveCommand with undo/redo stacks to navigate move history.
 * - Memento (persistence): save/load persists model state to a file.
 */
public class OthelloApplication extends Application {
	private static final String TEST_STATE_FILE_PROPERTY = "othello.test.stateFile";
	// REMEMBER: To run this in the lab put 
	// --module-path "/usr/share/openjfx/lib" --add-modules javafx.controls,javafx.fxml
	// in the run configuration under VM arguments.
	// You can import the JavaFX.prototype launch configuration and use it as well.

	@Override
	public void start(Stage stage) throws Exception {
		// Create and hook up the Model, View and the controller
		
		// MODEL
		Othello[] othelloRef = new Othello[]{new Othello()};
		Deque<OthelloMoveCommand> undoStack = new ArrayDeque<>();
		Deque<OthelloMoveCommand> redoStack = new ArrayDeque<>();
		Label status = new Label();
		status.setId("statusLabel");
		Button[][] cells = new Button[Othello.DIMENSION][Othello.DIMENSION];
		// Keep a flat list of all buttons so we can enable/disable on game over or restart
		java.util.List<Button> allCells = new java.util.ArrayList<>();
		ComboBox<String> p1Choice = new ComboBox<>(FXCollections.observableArrayList("Human", "Random", "Greedy"));
		ComboBox<String> p2Choice = new ComboBox<>(FXCollections.observableArrayList("Human", "Random", "Greedy"));
		p1Choice.setId("player1Choice");
		p2Choice.setId("player2Choice");
		p1Choice.getSelectionModel().selectFirst();
		p2Choice.getSelectionModel().selectFirst();

		// CONTROLLER
		// CONTROLLER->MODEL hookup
	
		// VIEW
		// VIEW->CONTROLLER hookup
		// MODEL->VIEW hookup
		
		GridPane grid = new GridPane();
		grid.setId("boardGrid");
		for (int r = 0; r < Othello.DIMENSION; r++) 
			{
			for (int c = 0; c < Othello.DIMENSION; c++) 
				{
				Button cell = new Button();
				cell.setPrefSize(50, 50);
				cell.setId("cell-" + r + "-" + c);
				final int row = r;
				final int col = c;
				cell.setOnAction(e -> {
					// Only allow manual click when the current strategy is Human
					if (isHumanTurn(othelloRef[0].getWhosTurn(), p1Choice, p2Choice)) 
						{
						Othello before = othelloRef[0].copy();
						if (othelloRef[0].move(row, col)) 
						{
							Othello after = othelloRef[0].copy();
							undoStack.push(new OthelloMoveCommand(before, after));
							redoStack.clear();
							refreshBoard(othelloRef[0], cells);
							updateStatus(othelloRef[0], status);
							runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells);
							handleGameOver(othelloRef[0], allCells, status);
						}
					}
				});
				cells[r][c] = cell;
				grid.add(cell, c, r);
				allCells.add(cell);
			}
		}
		applyInitialStateFromProperty(othelloRef);
		refreshBoard(othelloRef[0], cells);
		updateStatus(othelloRef[0], status);
		p1Choice.setOnAction(e -> runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells));
		p2Choice.setOnAction(e -> runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells));
		runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells);
		handleGameOver(othelloRef[0], allCells, status);

		HBox selectors = new HBox();
		selectors.setId("playerSelectors");
		selectors.setSpacing(10);
		selectors.getChildren().addAll(new Label("P1:"), p1Choice, new Label("P2:"), p2Choice);

		// Save/load chooser for picking files
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Othello save", "*.txt"));
		chooser.setInitialFileName("othello_save.txt");

		Button undoBtn = iconButton("Undo", "Undo.png");
		Button redoBtn = iconButton("Redo", "Redo.png");
		Button saveBtn = iconButton("Save", "Save.png");
		Button loadBtn = iconButton("Load", "Load.png");
		undoBtn.setId("undoButton");
		redoBtn.setId("redoButton");
		saveBtn.setId("saveButton");
		loadBtn.setId("loadButton");

		// undo button
		undoBtn.setOnAction(e -> {
			if (!undoStack.isEmpty()) 
			{
				OthelloMoveCommand cmd = undoStack.pop();
				redoStack.push(cmd);
				othelloRef[0] = cmd.undo();
				refreshBoard(othelloRef[0], cells);
				updateStatus(othelloRef[0], status);
				handleGameOver(othelloRef[0], allCells, status);
			}
		});

		//redo button
		redoBtn.setOnAction(e -> {
			if (!redoStack.isEmpty()) {
				OthelloMoveCommand cmd = redoStack.pop();
				othelloRef[0] = cmd.execute();
				undoStack.push(cmd);
				refreshBoard(othelloRef[0], cells);
				updateStatus(othelloRef[0], status);
				runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells);
				handleGameOver(othelloRef[0], allCells, status);
			}
		});

		//save button
		saveBtn.setOnAction(e -> {
			try 
			{
				// Let user pick a file to save
				java.io.File f = chooser.showSaveDialog(stage);
				if (f != null) 
				{
					saveState(othelloRef[0], f.toPath());
					status.setText("Saved: " + f.getName());
				}
			} 
			catch (IOException ex) 
			{
				status.setText("Save failed");
			}
		});

		//Load Button
		loadBtn.setOnAction(e -> {
			try 
			{
				// Let user pick a file to load
				java.io.File f = chooser.showOpenDialog(stage);
				if (f != null) 
				{
					Othello loaded = loadState(f.toPath());
					if (loaded != null) 
					{
						othelloRef[0] = loaded;
						undoStack.clear();
						redoStack.clear();
						refreshBoard(othelloRef[0], cells);
						updateStatus(othelloRef[0], status);
						runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells);
						status.setText("Loaded: " + f.getName());
						handleGameOver(othelloRef[0], allCells, status);
					} 
					else 
					{
						status.setText("Load failed");
					}
				}
			} 
			catch (IOException ex) 
			{
				status.setText("Load failed");
			}
		});

		//Restart Button
		Button restartBtn = iconButton("Restart", "Restart.png");
		restartBtn.setId("restartButton");
		restartBtn.setOnAction(e -> {
			othelloRef[0] = new Othello();
			undoStack.clear();
			redoStack.clear();
			enableBoard(allCells, true);
			refreshBoard(othelloRef[0], cells);
			updateStatus(othelloRef[0], status);
			runComputerMoves(othelloRef, cells, status, p1Choice, p2Choice, undoStack, redoStack, allCells);
		});

		HBox historyBar = new HBox(10, undoBtn, redoBtn, restartBtn, saveBtn, loadBtn);
		historyBar.setId("historyBar");

		VBox root = new VBox(selectors, status, historyBar, grid);
		root.setId("othelloRoot");
		root.setSpacing(10);
		root.setPadding(new Insets(10));

		Scene scene = new Scene(root);
		stage.setTitle("Othello");
		stage.setScene(scene);
				
		// LAUNCH THE GUI
		stage.show();
	}

	public static void main(String[] args) {
		OthelloApplication view = new OthelloApplication();
		launch(args);
	}

	/**
	 * Refresh button text to match current board tokens (via Visitor traversal).
	 */
	private void refreshBoard(Othello othello, Button[][] cells) 
	{
		othello.accept((r, c, token) -> {
			Button cell = cells[r][c];
			cell.setGraphic(null);
			cell.setText(token == OthelloBoard.EMPTY ? "" : String.valueOf(token));
		});
	}

	/**
	 * Update status label with counts and whose turn.
	 */
	private void updateStatus(Othello othello, Label status) 
	{
		status.setText("X:" + othello.getCount(OthelloBoard.P1) + " O:" + othello.getCount(OthelloBoard.P2)
				+ " | turn: " + othello.getWhosTurn());
	}

	/**
	 * Check if the current player is controlled by a human strategy.
	 */
	private boolean isHumanTurn(char who, ComboBox<String> p1Choice, ComboBox<String> p2Choice) 
	{
		if (who == OthelloBoard.P1) 
		{
			return "Human".equals(p1Choice.getValue());
		}
		if (who == OthelloBoard.P2) 
		{
			return "Human".equals(p2Choice.getValue());
		}
		return false;
	}

	/**
	 * Create a Player strategy instance based on the UI selection for the given side.
	 */
	private Player createPlayer(char who, ComboBox<String> p1Choice, ComboBox<String> p2Choice, Othello o) 
	{
		String choice = who == OthelloBoard.P1 ? p1Choice.getValue() : p2Choice.getValue();
		if ("Random".equals(choice)) 
		{
			return new PlayerRandom(o, who);
		}
		if ("Greedy".equals(choice))
		{
			return new PlayerGreedy(o, who);
		}
		return new PlayerHuman(o, who);
	}

	/**
	 * Auto-play AI turns until it becomes a human turn or the game ends.
	 */
	private void runComputerMoves(Othello[] othelloRef, Button[][] cells, Label status, ComboBox<String> p1Choice, ComboBox<String> p2Choice,
			Deque<OthelloMoveCommand> undoStack, Deque<OthelloMoveCommand> redoStack, java.util.List<Button> allCells) 
	{
		while (!othelloRef[0].isGameOver() && !isHumanTurn(othelloRef[0].getWhosTurn(), p1Choice, p2Choice)) 
		{
			Player ai = createPlayer(othelloRef[0].getWhosTurn(), p1Choice, p2Choice, othelloRef[0]);
			Move m = ai.getMove();
			if (m != null) 
			{
				Othello before = othelloRef[0].copy();
				if (othelloRef[0].move(m.getRow(), m.getCol())) 
				{
					Othello after = othelloRef[0].copy();
					undoStack.push(new OthelloMoveCommand(before, after));
					redoStack.clear();
					refreshBoard(othelloRef[0], cells);
					updateStatus(othelloRef[0], status);
					handleGameOver(othelloRef[0], allCells, status);
				} 
				else 
				{
					break;
				}
			} 
			else 
			{
				break;
			}
		}
	}

	/**
	 * Save current game state to a simple text file.
	 */
	private void saveState(Othello othello, Path path) throws IOException 
	{
		StringBuilder strb = new StringBuilder();
		strb.append("TURN:").append(othello.getWhosTurn()).append("\n");
		strb.append("MOVES:").append(othello.getNumMoves()).append("\n");
		strb.append("DIM:").append(Othello.DIMENSION).append("\n");
		for (int r = 0; r < Othello.DIMENSION; r++) 
		{
			for (int c = 0; c < Othello.DIMENSION; c++) 
			{
				char t = othello.getToken(r, c);
				strb.append(t == OthelloBoard.EMPTY ? '.' : t);
			}
			strb.append("\n");
		}
		Files.writeString(path, strb.toString(), StandardCharsets.UTF_8);
	}

	/**
	 * Load game state from file; returns null on parse error.
	 */
	private Othello loadState(Path path) throws IOException 
	{
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		if (lines.size() < 3 + Othello.DIMENSION) return null;
		char turn = lines.get(0).charAt("TURN:".length());
		int moves = Integer.parseInt(lines.get(1).substring("MOVES:".length()));
		char[][] grid = new char[Othello.DIMENSION][Othello.DIMENSION];
		for (int r = 0; r < Othello.DIMENSION; r++) 
		{
			String row = lines.get(3 + r);
			for (int c = 0; c < Othello.DIMENSION && c < row.length(); c++) 
			{
				char ch = row.charAt(c);
				grid[r][c] = (ch == '.') ? OthelloBoard.EMPTY : ch;
			}
		}
		return new Othello(new OthelloBoard(grid), turn, moves);
	}

	/**
	 * Allow tests to launch the UI from a saved state without touching the normal flow.
	 */
	private void applyInitialStateFromProperty(Othello[] othelloRef) 
	{
		String stateFile = System.getProperty(TEST_STATE_FILE_PROPERTY);
		if (stateFile == null || stateFile.isBlank()) 
		{
			return;
		}
		try 
		{
			Othello loaded = loadState(Path.of(stateFile));
			if (loaded == null) 
			{
				throw new IllegalStateException("Unable to load test state from " + stateFile);
			}
			othelloRef[0] = loaded;
		} 
		catch (IOException e) 
		{
			throw new IllegalStateException("Unable to load test state from " + stateFile, e);
		}
	}

	/**
	 * Disable/enable the board based on game state and show winner on game over.
	 */
	private void handleGameOver(Othello othello, java.util.List<Button> allCells, Label status) 
	{
		if (othello.isGameOver()) 
		{
			enableBoard(allCells, false);
			status.setText(status.getText() + " | game over, winner: " + winnerLabel(othello));
		} 
		else 
		{
			enableBoard(allCells, true);
		}
	}

	/**
	 * Enable or disable all board buttons.
	 */
	private void enableBoard(java.util.List<Button> allCells, boolean enabled) 
	{
		for (Button b : allCells) 
		{
			b.setDisable(!enabled);
		}
	}

	/**
	 * Return a winner label (X/O or Tie).
	 */
	private String winnerLabel(Othello othello) 
	{
		char w = othello.getWinner();
		if (w == OthelloBoard.EMPTY) 
		{
				return "Tie";
		}
		return String.valueOf(w);
	}
	
	/**
	 * Create a button with an optional icon loaded from src/ca/yorku/eecs3311/Image.
	 */
	private Button iconButton(String text, String fileName) 
	{
		Button b = new Button(text);
		try {
			Image img = new Image("file:src/ca/yorku/eecs3311/Image/" + fileName);
			ImageView view = new ImageView(img);
			view.setFitWidth(20);
			view.setFitHeight(20);
			view.setPreserveRatio(true);
			b.setGraphic(view);
		} 
		catch (Exception e) 
		{
			// If loading fails, just keep text.
		}
		return b;
	}
}
