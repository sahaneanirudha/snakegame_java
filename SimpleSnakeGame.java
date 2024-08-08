import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SimpleSnakeGame extends JPanel implements ActionListener {
    private static final int GRID_SIZE = 20;
    private static final int TILE_SIZE = 20;
    private static final int BOARD_SIZE = GRID_SIZE * TILE_SIZE;
    private static final int INITIAL_LENGTH = 3;
    private static final int DELAY = 100; // milliseconds

    private ArrayList<Point> snake;
    private Point food;
    private int direction; // 0=left, 1=up, 2=right, 3=down
    private boolean gameOver;
    private Timer timer;
    private JButton retryButton;
    private JPanel buttonPanel;

    public SimpleSnakeGame() {
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 2) direction = 0;
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 3) direction = 1;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 0) direction = 2;
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 1) direction = 3;
                        break;
                }
            }
        });

        // Create and configure the retry button
        retryButton = new JButton("Retry");
        retryButton.addActionListener(e -> restartGame());
        retryButton.setFocusable(false);

        // Create and configure the button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(retryButton);

        // Set up the layout
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);

        initGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initGame() {
        snake = new ArrayList<>();
        for (int i = 0; i < INITIAL_LENGTH; i++) {
            snake.add(new Point(INITIAL_LENGTH - i, 0));
        }
        direction = 2;
        spawnFood();
        gameOver = false;
        retryButton.setVisible(false);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void restartGame() {
        initGame();
        timer.start();
        repaint();
    }

    private void spawnFood() {
        Random rand = new Random();
        int x = rand.nextInt(GRID_SIZE);
        int y = rand.nextInt(GRID_SIZE);
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", BOARD_SIZE / 2 - 30, BOARD_SIZE / 2);
            retryButton.setVisible(true);
            return;
        }

        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 0: newHead.x--; break;
            case 1: newHead.y--; break;
            case 2: newHead.x++; break;
            case 3: newHead.y++; break;
        }

        if (newHead.equals(food)) {
            snake.add(0, food);
            spawnFood();
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }

        if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE ||
            snake.subList(1, snake.size()).contains(newHead)) {
            gameOver = true;
            timer.stop(); // Stop the game when it's over
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            moveSnake();
            repaint();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple Snake Game");
        SimpleSnakeGame game = new SimpleSnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
