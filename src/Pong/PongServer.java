package Pong;
// Server portion of a client/server stream-socket connection.
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import javax.swing.*;

public class PongServer extends PongParent {

    private ServerSocket server;
    private int counter = 1;
    private final int PADDLE_HEIGHT = 140;
    private Point ball = new Point(350, 40),
            paddleLeft = new Point(30, 280),
            paddleRight = new Point(620, 280);
    private double ball_dx = 3, ball_dy = 3;

    public PongServer()
    {
        super( "Server" );
    }

    @Override
    public void run()
    {
        try
        {
            server = new ServerSocket( 12345, 100 );

            while ( true )
            {
                try
                {
                    waitForConnection();
                    getStreams();
                    processConnection();
                }
                catch ( EOFException eofException )
                {
                    displayArea.setText("Server terminated connection" );
                }
                finally
                {
                    closeConnection();
                    ++counter;
                }
            }
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }


    private void waitForConnection() throws IOException
    {
        displayArea.setText( "Waiting for connection" );
        // blocking call (synchronous call.... not asynchronous (= on demand...where your code hets called back like an event handler)
        connection = server.accept();
        displayArea.setText( "Connection " + counter + " received from: " +
                connection.getInetAddress().getHostName() );

    }

    @Override
    public void processConnection()
    {
        displayArea.setText("About to start game");
        startGame();

        do
        {
            try
            {
                paddleRight.y = input.readInt();
                gamePanel.setPaddleRight(paddleRight);// read new message
            }
            catch (IOException e) {
                displayArea.setText("Issue receiving data! " + e );
            }

        } while ( !(player1.getText().equals("10.0") || player2.getText().equals("10.0")) );

        if(player1.getText().equals("10.0")) {
            System.out.println("Player 1 wins!");
        }
        else {
            System.out.println("Player 2 wins!");
        }
        System.exit(0);
    }

    @Override
    public void sendData()
    {
        try
        {
            displayArea.setText("Writing data!");
            output.writeInt( paddleLeft.y );
            output.writeInt(ball.x);
            output.writeInt(ball.y);
            output.writeObject(player1.getText());
            output.writeObject(player2.getText());
            output.flush();
        }
        catch ( IOException ioException )
        {
            displayArea.setText("Error writing object" );
        }
    }

    private void startGame() {
        Random random = new Random();
        Timer ballUpdater = new Timer(5, e -> {
            ball.translate((int) ball_dx, (int) ball_dy);
            if (((ball.x < 60 && ball.x > 0) &&
                    ((ball.y < paddleLeft.y + PADDLE_HEIGHT) && (ball.y > paddleLeft.y))
                    && ball_dx < 0)
                    || ((ball.x > 580 && ball.x < 645) &&
                    ((ball.y < paddleRight.y + PADDLE_HEIGHT) && (ball.y > paddleRight.y))
                    && ball_dx > 0)) {
                ball_dx = -ball_dx;
            }
            if (ball.x < 0) {
                scoreboard.changeScore(player2);
                ball.x = random.nextInt(200);
                ball.x += 400;
            }
            if (ball.x > 645) {
                scoreboard.changeScore(player1);
                ball.x = random.nextInt(200);
            }
            if (ball.y < 0 || ball.y > 620) {
                ball_dy = -ball_dy;
            }
            gamePanel.setBall(ball);
            gamePanel.setPaddleLeft(paddleLeft);
            gamePanel.setPaddleRight(paddleRight);
            gamePanel.repaint();
            sendData();
        });

        ballUpdater.start();
    }
}