package Pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;


public class PongClient extends PongParent {

    private String pongClient;


    //For reading into the client from the server
    int paddleLeftY;
    int newBallX;
    int newBallY;
    Point newBallLocation;


    public PongClient( String host )
    {
        super( "Client" );


        pongClient = host;

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if (gamePanel.getPaddleRight().y > 0) {
                        gamePanel.getPaddleRight().translate(0, -50);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (gamePanel.getPaddleRight().y < 500) {
                        gamePanel.getPaddleRight().translate(0, 50);
                    }
                }
                sendData();
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });


    }


    @Override
    public void run()
    {
        try
        {
            connectToServer();
            getStreams();
            processConnection();
        }
        catch ( EOFException eofException )
        {
            displayMessage( "\nClient terminated connection" );
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
        finally
        {
            closeConnection();
        }
    }


    private void connectToServer() throws IOException
    {
        displayMessage( "Attempting connection\n" );


        connection = new Socket( InetAddress.getByName( pongClient ), 12345 );


        displayMessage( "Connected to: " +
                connection.getInetAddress().getHostName() );
    }

    @Override
    public void processConnection() throws IOException
    {
        do {
            try
            {
                paddleLeftY = input.readInt();
                gamePanel.setPaddleLeftY(paddleLeftY);

                newBallX = input.readInt();
                newBallY = input.readInt();
                newBallLocation = new Point(newBallX, newBallY);
                gamePanel.setBall(newBallLocation);

                String player1Score = input.readObject().toString();
                String player2Score = input.readObject().toString();
                player1.setText(player1Score);
                player2.setText(player2Score);
                gamePanel.repaint();

            }
            catch ( ClassNotFoundException classNotFoundException)
            {
                displayMessage( "\nUnknown object type received" );
            }
            catch (NumberFormatException e)
            {
                displayMessage("\nReceived score which is NaN");
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
            output.writeInt(gamePanel.getPaddleRight().y);
            output.flush();
        }
        catch ( IOException ioException )
        {
            displayMessage( "\nError writing object" );
        }
    }

        private void displayMessage( final String messageToDisplay )
    {


        SwingUtilities.invokeLater(
                () -> displayArea.setText(messageToDisplay)
        );
    }
}