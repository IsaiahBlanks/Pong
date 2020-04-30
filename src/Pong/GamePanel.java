package Pong;

import javax.swing.*;
import java.awt.*;

class GamePanel extends JPanel {
    private final int BALL_DIAMETER = 40;
    private final int PADDLE_WIDTH = 30;
    private final int PADDLE_HEIGHT = 140;
    private Point ball = new Point(350, 40),
            paddleLeft = new Point(30, 280),
            paddleRight = new Point(620, 280);


    GamePanel() {
        setBackground(new Color(225, 231, 240));
        //setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
    }

    void setPaddleLeft(Point pL) {
        paddleLeft = pL;
    }

    void setPaddleRight(Point pR) {
        paddleRight = pR;
    }

    void setBall(Point b) {
        ball = b;
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
