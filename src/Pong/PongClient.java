package Pong;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PongClient extends PongParent {
    int paddleLeftY;
    int newBallX;
    int newBallY;
    Point newBallLocation;

    public PongClient( String host )
    {
        super( Modes.CLIENT, host );

    }

    @Override
    public void runConnection()
    {
        try
        {
            connect();
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


    @Override
    protected void connect() throws IOException
    {
        displayMessage( "Attempting connection\n" );

        connection = new Socket( InetAddress.getByName( pongClient ), 12345 );

        displayMessage( "Connected to: " +
                connection.getInetAddress().getHostName() );
    }

    private void processConnection() throws IOException
    {
        do
        {
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
                getPlayer1().setText(player1Score);
                getPlayer2().setText(player2Score);
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


        } while ( !(getPlayer1().getText().equals("10") || getPlayer2().getText().equals("10")) );
    }

    @Override
    protected void sendData()
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

}
