package TwentyFortyEight;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int GRID_SIZE = 4; // 4x4 grid
    public static final int CELLSIZE = 100; // Cell size in pixels
    public static final int CELL_BUFFER = 8; // Space between cells
    public static final int WIDTH = GRID_SIZE * CELLSIZE;
    public static final int HEIGHT = GRID_SIZE * CELLSIZE;
    public static final int FPS = 30;

    private Cell[][] board;

    public static Random random = new Random();

    private PFont font;
    public PImage eight;

    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.
    private Cell[][] board;
    public static Random random = new Random();

    private PFont font;
    public PImage eight;

    private int startTime = 0;
    private boolean gameOver = false;

    public App() {
        this.board = new Cell[4][4];
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player
     * and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);
        // See PApplet javadoc:
        // loadJSONObject(configPath)
        this.eight = loadImage(this.getClass().getResource("8.png").getPath().toLowerCase(Locale.ROOT).replace("%20", ""));
        // " "));

        // create attributes for data storage, eg board
        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2] = new Cell(i2, i);
            }
        }

        spawnRandomTile();
        spawnRandomTile();

        this.font = createFont("Arial", 24, true);
        textFont(font);
        startTime = millis();
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (gameOver) return;

        boolean moved = false;

        switch (event.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_LEFT:
                moved = moveLeft();
                break;
            case java.awt.event.KeyEvent.VK_RIGHT:
                moved = moveRight();
                break;
            case java.awt.event.KeyEvent.VK_UP:
                moved = moveUp();
                break;
            case java.awt.event.KeyEvent.VK_DOWN:
                moved = moveDown();
                break;
            default:
                break;
        }
        if (moved) {
            spawnRandomTile();
            clearMergeFlags();
            if (!canMove()) {
                gameOver = true;
            }
        }
        if (event.getKey() == 'r') {
            restartGame();
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == PConstants.LEFT) {
            Cell current = board[e.getY()/App.CELLSIZE][e.getX()/App.CELLSIZE];
            current.place();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        // draw game board
        background(250);
        this.textSize(40);
        this.strokeWeight(15);
        
        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2].draw(this);
            }
        }

        fill(0);
        textSize(20);
        int seconds = (millis() - startTime) / 1000;
        text("Time: " + seconds + "s", WIDTH - 110, 30);

        if (gameOver) {
            fill(0);
            textSize(50);
            textAlign(CENTER, CENTER);
            text("GAME OVER", WIDTH / 2f, HEIGHT / 2f);
        }
    }

    private void restartGame() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].reset();
            }
        }
        spawnRandomTile();
        spawnRandomTile();
        startTime = millis();
        gameOver = false;
    }

    private void clearMergeFlags() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j].clearMergedFlag();
            }
        }
    }

    private void spawnRandomTile() {
        List<Cell> empty = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getValue() == 0) {
                    empty.add(board[i][j]);
                }
            }
        }
        if (!empty.isEmpty()) {
            empty.get(random.nextInt(empty.size())).place();
        }
    }

    private boolean canMove() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j].getValue() == 0) return true;
                if (j < GRID_SIZE - 1 && board[i][j].getValue() == board[i][j + 1].getValue()) return true;
                if (i < GRID_SIZE - 1 && board[i][j].getValue() == board[i + 1][j].getValue()) return true;
            }
        }
        return false;
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 1; col < GRID_SIZE; col++) {
                if (board[row][col].getValue() == 0) continue;
                int currentCol = col;
                while (currentCol > 0 && board[row][currentCol - 1].getValue() == 0) {
                    board[row][currentCol - 1].setValue(board[row][currentCol].getValue());
                    board[row][currentCol].setValue(0);
                    currentCol--;
                    moved = true;
                }
                if (currentCol > 0 && board[row][currentCol - 1].canMergeWith(board[row][currentCol])) {
                    board[row][currentCol - 1].merge(board[row][currentCol]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = GRID_SIZE - 2; col >= 0; col--) {
                if (board[row][col].getValue() == 0) continue;
                int currentCol = col;
                while (currentCol < GRID_SIZE - 1 && board[row][currentCol + 1].getValue() == 0) {
                    board[row][currentCol + 1].setValue(board[row][currentCol].getValue());
                    board[row][currentCol].setValue(0);
                    currentCol++;
                    moved = true;
                }
                if (currentCol < GRID_SIZE - 1 && board[row][currentCol + 1].canMergeWith(board[row][currentCol])) {
                    board[row][currentCol + 1].merge(board[row][currentCol]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 1; row < GRID_SIZE; row++) {
                if (board[row][col].getValue() == 0) continue;
                int currentRow = row;
                while (currentRow > 0 && board[currentRow - 1][col].getValue() == 0) {
                    board[currentRow - 1][col].setValue(board[currentRow][col].getValue());
                    board[currentRow][col].setValue(0);
                    currentRow--;
                    moved = true;
                }
                if (currentRow > 0 && board[currentRow - 1][col].canMergeWith(board[currentRow][col])) {
                    board[currentRow - 1][col].merge(board[currentRow][col]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = GRID_SIZE - 2; row >= 0; row--) {
                if (board[row][col].getValue() == 0) continue;
                int currentRow = row;
                while (currentRow < GRID_SIZE - 1 && board[currentRow + 1][col].getValue() == 0) {
                    board[currentRow + 1][col].setValue(board[currentRow][col].getValue());
                    board[currentRow][col].setValue(0);
                    currentRow++;
                    moved = true;
                }
                if (currentRow < GRID_SIZE - 1 && board[currentRow + 1][col].canMergeWith(board[currentRow][col])) {
                    board[currentRow + 1][col].merge(board[currentRow][col]);
                    moved = true;
                }
            }
        }
        return moved;
    }

    public static void main(String[] args) {
        PApplet.main("TwentyFortyEight.App");
    }
}
