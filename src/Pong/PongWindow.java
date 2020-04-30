package Pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PongWindow extends JFrame {

    private Random random = new Random();
    private final int BALL_DIAMETER = 40;
    private final int PADDLE_WIDTH = 30;
    private final int PADDLE_HEIGHT = 140;
    private Point ball = new Point(350, 40),
            paddleLeft = new Point(30, 280),
            paddleRight = new Point(620, 280);
    private GamePanel gamePanel = new GamePanel();
    private JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
    MyScoreboard scoreboard = new MyScoreboard(player1, player2);
    private double ball_dx = 3, ball_dy = 3;


    public PongWindow() {
        setSize(700, 700);
        setTitle("Two Player Pong!");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        Timer ballUpdater = new Timer(5, e -> {
            ball.translate((int) ball_dx,(int) ball_dy);
            if(((ball.x < 60 && ball.x > 0) &&
                    ((ball.y < paddleLeft.y + PADDLE_HEIGHT) && (ball.y > paddleLeft.y))
                    && ball_dx < 0)
                    || ((ball.x > 580 && ball.x < 645) &&
                    ((ball.y < paddleRight.y + PADDLE_HEIGHT) && (ball.y > paddleRight.y))
                    && ball_dx > 0))
            {
                ball_dx = -ball_dx;
            }
            if(ball.x < 0) {
                scoreboard.changeScore(player2);
                ball.x = random.nextInt(200) + 400;
                if(player2.getText().equals("10.0")) {
                    System.out.println("Player 2 wins!");
                    System.exit(0);
                }
            }
            if (ball.x > 645) {
                scoreboard.changeScore(player1);
                ball.x = random.nextInt(200);
                if(player1.getText().equals("10.0")) {
                    System.out.println("Player 1 wins!");
                    System.exit(0);
                }
            }
            if(ball.y < 0 || ball.y > 620) {
                ball_dy = -ball_dy;
            }
            repaint();
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == '+') {
                    ball_dx *= 1.05;
                    ball_dy *= 1.05;
                }
                if(e.getKeyChar() == '-') {
                    ball_dx *= 0.95;
                    ball_dy *= 0.95;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if (paddleRight.y > 0) {
                        paddleRight.translate(0, -50);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (paddleRight.y < 500) {
                        paddleRight.translate(0, 50);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        addMouseWheelListener(mouseWheelEvent -> {
            paddleLeft.translate(0, 5 * mouseWheelEvent.getWheelRotation());
            repaint();
        });

        ballUpdater.start();
        setLayout(new BorderLayout());
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        setVisible(true);
    }

    class MyScoreboard extends JPanel {
        final int WIDTH = 50;
        final int HEIGHT = 500;
        MyScoreboard(JLabel player1, JLabel player2) {
            this.setSize(WIDTH, HEIGHT);
            JLabel divider = new JLabel(" : ");
            this.add(player1);
            this.add(divider);
            this.add(player2);
            setVisible(true);
        }

        void changeScore(JLabel playerScoreString) {
            try {
                double playerScoreNumber = Double.parseDouble(playerScoreString.getText());
                playerScoreNumber++;
                playerScoreString.setText(String.valueOf(playerScoreNumber));
            } catch(NumberFormatException e) {
                System.out.println("Invalid conversion of points. Expected number and got NaN");
            }
        }

    }

    class GamePanel extends JPanel {

        GamePanel() {
            setBackground(new Color(225, 231, 240));
            //setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 700, 700);
            g.clearRect(10, 10, 663, 643);
            g.fillOval(ball.x, ball.y, BALL_DIAMETER, BALL_DIAMETER);
            g.fillRect(paddleLeft.x, paddleLeft.y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.fillRect(paddleRight.x, paddleRight.y, PADDLE_WIDTH, PADDLE_HEIGHT);
        }


    }


    public static void main(String[] args) {
        new PongWindow();
    }
}