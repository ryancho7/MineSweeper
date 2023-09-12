import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton {
        private int row;
        private int col;

        public MineTile(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private int tileSize = 70;
    private int numRows = 8;
    private int numCols = numRows;
    private int boardWidth = numCols * tileSize;
    private int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount = 10;
    Random random = new Random();
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;

    int tilesClicked = 0;
    boolean gameOver = false;

    Minesweeper() {

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        //boardPanel.setBackground(Color.green);
        frame.add(boardPanel);
        
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                MineTile tile = new MineTile(row, col);
                board[row][col] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                //tile.setText("1");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        // left click
                        if(e.getButton() == MouseEvent.BUTTON1) {
                            if(tile.getText() == "") {
                                if(mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.row, tile.col);
                                }
                            }
                        } else if(e.getButton() == MouseEvent.BUTTON3) { // right click
                            if(tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if(tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);

        setMines();

    }

    public void setMines() {
        mineList = new ArrayList<MineTile>();
        int mineLeft = mineCount;
        while(mineLeft > 0) {
            int randRow = random.nextInt(numRows);
            int randCol = random.nextInt(numCols);
            MineTile tile = board[randRow][randCol];
            if(!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    public void revealMines() {
        for(int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over!");
    }

    public void checkMine(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            return;
        }

        MineTile tile = board[row][col];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        // top three squares
        minesFound += countMine(row - 1, col - 1); // top left
        minesFound += countMine(row - 1, col); // top middle
        minesFound += countMine(row - 1, col + 1); // top right
        // left + right squares
        minesFound += countMine(row, col - 1); // left
        minesFound += countMine(row, col + 1); // right
        // bottom three squares
        minesFound += countMine(row + 1, col - 1); // bottom left
        minesFound += countMine(row + 1, col); // bottome middle
        minesFound += countMine(row + 1, col + 1); //bottom right

        if(minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            // top three squares
            checkMine(row - 1, col - 1); // top left
            checkMine(row - 1, col); // top middle
            checkMine(row - 1, col + 1); // top right
            // left and right squares
            checkMine(row, col - 1); // left
            checkMine(row, col + 1); // right
            //bottom three squares
            checkMine(row + 1, col - 1); // bottom left
            checkMine(row + 1, col); // bottom middle
            checkMine(row + 1, col + 1); // bottom right
        }

        if(tilesClicked == (numRows * numCols) - mineList.size()) {
            gameOver = true;
            textLabel.setText("All Mines Cleared!");
        }
    }

    public int countMine(int row, int col) {
        if(row < 0 || row >= numRows || col < 0 || col >= numCols) {
            return 0;
        }
        if(mineList.contains(board[row][col])) {
            return 1;
        }
        return 0;
    }
}
