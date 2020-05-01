package Pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.Border;

public class PongClient extends JFrame {

    private JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
    private JTextArea displayArea; // display information to user
    private ObjectOutputStream output; // output stream to server
    private ObjectInputStream input; // input stream from server
    private int directionPressed;
    private String pongClient; // host server for this application
    private Socket client; // socket to communicate with server
    private double ball_dx = 3, ball_dy = 3;
    GamePanel gamePanel = new GamePanel();
    private MyScoreboard scoreboard = new MyScoreboard(player1, player2);

    //For reading into the client from the server
    int paddleLeftY;
    int newBallX;
    int newBallY;
    Point newBallLocation;

    // initialize chatServer and set up GUI
    public PongClient( String host )
    {
        super( "Client" );
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        pongClient = host; // set server to which this client connects

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
                    directionPressed = -50;
                    if (gamePanel.getPaddleRight().y > 0) {
                        gamePanel.getPaddleRight().translate(0, -50);
                    }
                    }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    directionPressed = 50;
                    if (gamePanel.getPaddleRight().y < 500) {
                        gamePanel.getPaddleRight().translate(0, 50);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        setSize( 300, 150 ); // set size of window
        setVisible( true ); // show window
    } // end Client constructor

    // connect to server and process messages from server
    public void runClient()
    {
        try // connect to server, get streams, process connection
        {
            connectToServer(); // create a Socket to make connection
            getStreams(); // get the input and output streams
            processConnection(); // process connection
        } // end try
        catch ( EOFException eofException )
        {
            displayMessage( "\nClient terminated connection" );
        } // end catch
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
        finally
        {
            closeConnection(); // close connection
        } // end finally
    } // end method runClient

    // connect to server
    private void connectToServer() throws IOException
    {
        displayMessage( "Attempting connection\n" );

        // create Socket to make connection to server
        client = new Socket( InetAddress.getByName( pongClient ), 12345 );

        // display connection information
        displayMessage( "Connected to: " +
                client.getInetAddress().getHostName() );
    } // end method connectToServer

    // get streams to send and receive data
    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream( client.getOutputStream() );
        output.writeInt(directionPressed);
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream( client.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    } // end method getStreams

    // process connection with server
    private void processConnection() throws IOException
    {
        do // process messages sent from server
        {
            try // read message and display it
            {
                paddleLeftY = input.readInt();
                gamePanel.setPaddleLeftY(paddleLeftY);

                newBallX = input.readInt();
                newBallY = input.readInt();
                newBallLocation = new Point(newBallX, newBallY);
                gamePanel.setBall(newBallLocation);

                String player1Score = (String) input.readObject();
                String player2Score = (String) input.readObject();
                player1.setText(player1Score);
                player2.setText(player2Score);

            } // end try
            catch ( ClassNotFoundException classNotFoundException)
            {
                displayMessage( "\nUnknown object type received" );
            } // end catch
            catch (NumberFormatException e)
            {
                displayMessage("\nReceived score which is NaN");
            }


        } while ( !(player1.getText().equals("10") || player2.getText().equals("10")) );
    } // end method processConnection

    // close streams and socket
    private void closeConnection()
    {
        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
        } // end try
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        } // end catch
    } // end method closeConnection

    // send message to server
    private void sendData( String message )
    {
        try // send object to server
        {
            output.writeObject( "CLIENT>>> " + message );
            output.flush(); // flush data to output
            displayMessage( "\nCLIENT>>> " + message );
        } // end try
        catch ( IOException ioException )
        {
            displayArea.append( "\nError writing object" );
        } // end catch
    } // end method sendData

    // manipulates displayArea in the event-dispatch thread
    private void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        displayArea.append( messageToDisplay );
                    } // end method run
                }  // end anonymous inner class
        ); // end call to SwingUtilities.invokeLater
    } // end method displayMessage
    // manipulates enterField in the event-dispatch thread
} // end class PongClient


class Main {
    public static void main(String[] args) {
        new PongClient("Client");
    }
}