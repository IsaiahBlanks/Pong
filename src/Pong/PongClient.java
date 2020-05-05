package Pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;


public class PongClient extends JFrame {

    private JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String pongClient;
    private Socket client;
    GamePanel gamePanel = new GamePanel();
    private MyScoreboard scoreboard = new MyScoreboard(player1, player2);
    JLabel displayArea = new JLabel();

    //For reading into the client from the server
    int paddleLeftY;
    int newBallX;
    int newBallY;
    Point newBallLocation;


    public PongClient( String host )
    {
        super( "Client" );
        setSize( 700, 730 );
        setVisible( true );
        setLayout(new BorderLayout());
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        add(displayArea, BorderLayout.SOUTH);
        displayArea.setSize(700, 10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

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


    public void runClient()
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


        client = new Socket( InetAddress.getByName( pongClient ), 12345 );


        displayMessage( "Connected to: " +
                client.getInetAddress().getHostName() );
    }


    private void getStreams() throws IOException
    {

        output = new ObjectOutputStream( client.getOutputStream() );
        output.flush(); // flush output buffer to send header information


        input = new ObjectInputStream( client.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    }


    private void processConnection() throws IOException
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


    private void closeConnection()
    {
        try
        {
            output.close();
            input.close();
            client.close();
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }

    private void sendData()
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