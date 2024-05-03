package test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;


public class FallingObstaclesGame extends JFrame implements ActionListener, KeyListener {
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final int PLAYER_WIDTH = 20;
    private final int PLAYER_HEIGHT = 20;
    private final int OBSTACLE_WIDTH = 20;
    private final int OBSTACLE_HEIGHT = 20;
    private final int PLAYER_SPEED = 5;
    private final int OBSTACLE_SPEED = 2;
    private final int OBSTACLE_INTERVAL = 1000; // milliseconds

    private final int INITIAL_OBSTACLE_SPEED = 2;
    private int obstacleSpeed = INITIAL_OBSTACLE_SPEED;
    private long startTime;
    private long score;
    
    private JLabel scoreLabel;
    
    private Timer timer;
    private int playerX, playerY;
    private ArrayList<Point> obstacles;
    private Random random;
    private Image buffer;
    private Graphics bufferGraphics;
    
    

    public FallingObstaclesGame() {
        setTitle("Falling Obstacles Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setFocusable(true);
        addKeyListener(this);
        
        // Make the frame visible before creating the buffer
        setVisible(true);

        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - PLAYER_HEIGHT - 30;

        obstacles = new ArrayList<>();
        random = new Random();

        // Create the buffer after the frame is visible
        buffer = createImage(WIDTH, HEIGHT);
        bufferGraphics = buffer.getGraphics();

        startTime = System.currentTimeMillis();
        
        // Initialize score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(10, 10, 100, 20); // Adjust position and size as needed
        add(scoreLabel); // Add the score label to the frame
        setVisible(true); // Make the frame visible after all components are added
        
        timer = new Timer(10, this);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        bufferGraphics.clearRect(0, 0, WIDTH, HEIGHT);

        bufferGraphics.setColor(Color.BLUE);
        bufferGraphics.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        bufferGraphics.setColor(Color.RED);
        for (Point obstacle : obstacles) {
            bufferGraphics.fillRect(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        }

        g.drawImage(buffer, 0, 0, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            update();
            repaint();
        }
    }

    private void update() {
        // Move player
        if (playerX < 0)
            playerX = 0;
        else if (playerX > WIDTH - PLAYER_WIDTH)
            playerX = WIDTH - PLAYER_WIDTH;

        // Calculate elapsed time since the start of the game
        long elapsedTime = System.currentTimeMillis() - startTime;
        score = elapsedTime / 1000; // Score in seconds

        
        
        // Increase obstacle speed gradually over time
        obstacleSpeed = INITIAL_OBSTACLE_SPEED + (int)(elapsedTime / 100000); // Increase speed by 1 every 100 seconds

        // Decrease obstacle interval gradually over time
        int obstacleInterval = OBSTACLE_INTERVAL - (int)(elapsedTime / 1000); // Decrease interval by 1 every 1 seconds

        // Increase obstacle count gradually over time
        int obstacleCount = 1 + (int)(elapsedTime / 1000); // Increase count by 1 every 1 seconds

        // Generate obstacles
        for (int j = 0; j < obstacleCount; j++) {
            if (random.nextInt(obstacleInterval) == 0) {
                int obstacleX = random.nextInt(WIDTH - OBSTACLE_WIDTH);
                obstacles.add(new Point(obstacleX, 0));
            }
        }

        // Move obstacles
        Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
        for (int i = 0; i < obstacles.size(); i++) {
            Point obstacle = obstacles.get(i);
            
            // Move obstacle
            obstacle.y += obstacleSpeed;
            Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);

            if (obstacleRect.intersects(playerRect)) {
                gameOver();
                return; // Dont check further collisions if the game is over
            }

            if (obstacle.y > HEIGHT) {
                obstacles.remove(i);
                i--;
            }
            
        // Update score
        scoreLabel.setText("Score: " + score);
        }
    }


    private void gameOver() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score + " seconds",
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            playerX -= PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            playerX += PLAYER_SPEED;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FallingObstaclesGame().setVisible(true));
    }
}
