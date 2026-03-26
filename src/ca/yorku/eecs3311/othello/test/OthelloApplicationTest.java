package ca.yorku.eecs3311.othello.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;

import ca.yorku.eecs3311.othello.viewcontroller.OthelloApplication;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class OthelloApplicationTest {
	private static final String TEST_STATE_FILE_PROPERTY = "othello.test.stateFile";

	private final FxRobot robot = new FxRobot();
	private Application application;
	private boolean toolkitStarted;

	@BeforeClass
	public static void setUpHeadlessProperties() {
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
		System.setProperty("glass.platform", "Monocle");
		System.setProperty("monocle.platform", "Headless");
		System.setProperty("prism.order", "sw");
		System.setProperty("java.awt.headless", "true");
	}

	@After
	public void tearDown() throws Exception {
		System.clearProperty(TEST_STATE_FILE_PROPERTY);
		if (!toolkitStarted) {
			return;
		}
		if (application != null) {
			FxToolkit.cleanupApplication(application);
		}
		FxToolkit.cleanupStages();
	}

	@Test
	public void testInitialBoardShowsOpeningDiscs() throws Exception {
		launchApplication(null);

		assertThat(cellText(3, 3)).isEqualTo("X");
		assertThat(cellText(3, 4)).isEqualTo("O");
		assertThat(cellText(4, 3)).isEqualTo("O");
		assertThat(cellText(4, 4)).isEqualTo("X");
	}

	@Test
	public void testInitialStatusShowsOpeningCountsAndTurn() throws Exception {
		launchApplication(null);

		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}

	@Test
	public void testInvalidMoveOnEmptySquareDoesNotChangeBoard() throws Exception {
		launchApplication(null);

		clickCell(0, 0);

		assertThat(cellText(0, 0)).isEmpty();
		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}

	@Test
	public void testInvalidMoveOnOccupiedSquareDoesNotChangeBoard() throws Exception {
		launchApplication(null);

		clickCell(3, 3);

		assertThat(cellText(3, 3)).isEqualTo("X");
		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}

	@Test
	public void testValidMovePlacesDiscOnClickedSquare() throws Exception {
		launchApplication(null);

		clickCell(2, 4);

		assertThat(cellText(2, 4)).isEqualTo("X");
	}

	@Test
	public void testValidMoveFlipsCapturedDisc() throws Exception {
		launchApplication(null);

		clickCell(2, 4);

		assertThat(cellText(3, 4)).isEqualTo("X");
	}

	@Test
	public void testValidMoveUpdatesCountsInStatus() throws Exception {
		launchApplication(null);

		clickCell(2, 4);

		assertThat(statusText()).isEqualTo("X:4 O:1 | turn: O");
	}

	@Test
	public void testValidMoveSwitchesTurnToOtherPlayer() throws Exception {
		launchApplication(null);

		clickCell(2, 4);

		assertThat(statusText()).endsWith("turn: O");
	}

	@Test
	public void testSecondPlayerCanMoveOnNextTurn() throws Exception {
		launchApplication(null);

		clickCell(2, 4);
		clickCell(2, 3);

		assertThat(cellText(2, 3)).isEqualTo("O");
	}

	@Test
	public void testSecondPlayerMoveSwitchesTurnBackToPlayerOne() throws Exception {
		launchApplication(null);

		clickCell(2, 4);
		clickCell(2, 3);

		assertThat(statusText()).isEqualTo("X:3 O:3 | turn: X");
	}

	@Test
	public void testFinalMoveTriggersGameOverMessage() throws Exception {
		launchApplication(createNearEndStateFile());

		clickCell(0, 0);

		assertThat(statusText()).contains("game over");
	}

	@Test
	public void testFinalMoveDisablesBoardButtons() throws Exception {
		launchApplication(createNearEndStateFile());

		clickCell(0, 0);

		assertThat(cellButton(0, 0).isDisabled()).isTrue();
		assertThat(cellButton(7, 7).isDisabled()).isTrue();
	}

	@Test
	public void testFinalMoveReportsWinner() throws Exception {
		launchApplication(createNearEndStateFile());

		clickCell(0, 0);

		assertThat(statusText()).contains("winner: X");
	}

	@Test
	public void testLoadedTieStateReportsTieWinner() throws Exception {
		launchApplication(createTieStateFile());

		assertThat(statusText()).contains("game over");
		assertThat(statusText()).contains("winner: Tie");
		assertThat(cellButton(0, 0).isDisabled()).isTrue();
	}

	private void launchApplication(Path stateFile) throws Exception {
		if (stateFile != null) {
			System.setProperty(TEST_STATE_FILE_PROPERTY, stateFile.toString());
		} else {
			System.clearProperty(TEST_STATE_FILE_PROPERTY);
		}
		FxToolkit.registerPrimaryStage();
		application = FxToolkit.setupApplication(OthelloApplication.class);
		toolkitStarted = true;
		WaitForAsyncUtils.waitForFxEvents();
	}

	private void clickCell(int row, int col) {
		robot.clickOn("#cell-" + row + "-" + col);
		WaitForAsyncUtils.waitForFxEvents();
	}

	private String cellText(int row, int col) {
		return cellButton(row, col).getText();
	}

	private Button cellButton(int row, int col) {
		return robot.lookup("#cell-" + row + "-" + col).queryButton();
	}

	private String statusText() {
		return robot.lookup("#statusLabel").queryAs(Label.class).getText();
	}

	private Path createNearEndStateFile() throws Exception {
		return createStateFile('X', 59,
				".OXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX",
				"XXXXXXXX");
	}

	private Path createTieStateFile() throws Exception {
		return createStateFile(' ', 60,
				"XOXOXOXO",
				"OXOXOXOX",
				"XOXOXOXO",
				"OXOXOXOX",
				"XOXOXOXO",
				"OXOXOXOX",
				"XOXOXOXO",
				"OXOXOXOX");
	}

	private Path createStateFile(char turn, int moves, String... rows) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append("TURN:").append(turn).append("\n");
		builder.append("MOVES:").append(moves).append("\n");
		builder.append("DIM:8\n");
		for (String row : rows) {
			builder.append(row).append("\n");
		}
		Path file = Files.createTempFile("othello-test-state", ".txt");
		Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);
		file.toFile().deleteOnExit();
		return file;
	}

	/* 
	// AI-generated additional GUI regression tests
	@Test
	public void testRestartResetsBoardToOpeningPosition() throws Exception {
		launchApplication(null);

		clickCell(2, 4);
		robot.clickOn("#restartButton");
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(cellText(2, 4)).isEmpty();
		assertThat(cellText(3, 3)).isEqualTo("X");
		assertThat(cellText(3, 4)).isEqualTo("O");
		assertThat(cellText(4, 3)).isEqualTo("O");
		assertThat(cellText(4, 4)).isEqualTo("X");
		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}

	// AI-generated additional GUI regression tests
	@Test
	public void testUndoRestoresPreviousBoardAndTurn() throws Exception {
		launchApplication(null);

		clickCell(2, 4);
		robot.clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(cellText(2, 4)).isEmpty();
		assertThat(cellText(3, 4)).isEqualTo("O");
		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}

	// AI-generated additional GUI regression tests
	@Test
	public void testRedoReappliesUndoneMove() throws Exception {
		launchApplication(null);

		clickCell(2, 4);
		robot.clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		robot.clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(cellText(2, 4)).isEqualTo("X");
		assertThat(cellText(3, 4)).isEqualTo("X");
		assertThat(statusText()).isEqualTo("X:4 O:1 | turn: O");
	}

	// AI-generated additional GUI regression tests
	@Test
	public void testRestartRecoversFromLoadedGameOverState() throws Exception {
		launchApplication(createTieStateFile());

		robot.clickOn("#restartButton");
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(cellButton(0, 0).isDisabled()).isFalse();
		assertThat(cellText(3, 3)).isEqualTo("X");
		assertThat(cellText(3, 4)).isEqualTo("O");
		assertThat(cellText(4, 3)).isEqualTo("O");
		assertThat(cellText(4, 4)).isEqualTo("X");
		assertThat(statusText()).isEqualTo("X:2 O:2 | turn: X");
	}
	*/
}
