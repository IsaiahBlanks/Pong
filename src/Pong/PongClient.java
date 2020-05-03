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
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int directionPressed;
    private String pongClient;
    private Socket client;
    private double ball_dx = 3, ball_dy = 3;
    GamePanel gamePanel = new GamePanel();
    private MyScoreboard scoreboard = new MyScoreboard(player1, player2);
    private JLabel myMessageBox = new JLabel();


    int paddleLeftY;
    int newBallX;
    int newBallY;
    Point newBallLocation;


    public PongClient(String host) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 700);
        setLayout(new BorderLayout());
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        add(myMessageBox, BorderLayout.SOUTH);
        setVisible(true);
        pongClient = host;

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    directionPressed = -50;
                    if (gamePanel.getPaddleRight().y > 0) {
                        gamePanel.getPaddleRight().translate(0, -50);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    directionPressed = 50;
                    if (gamePanel.getPaddleRight().y < 500) {
                        gamePanel.getPaddleRight().translate(0, 50);
                    }
                }
                gamePanel.setBall(gamePanel.getBall());
                gamePanel.setPaddleLeft(gamePanel.getPaddleLeft());
                gamePanel.setPaddleRight(gamePanel.getPaddleRight());
                gamePanel.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    // connect to server and process messages from server
    public void runClient() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        } catch (EOFException eofException) {
            displayMessage("\nClient terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeConnection();
        }
    }


    // connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");


        client = new Socket(InetAddress.getByName(pongClient), 12345);


        displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
    }

    // get streams to send and receive data
    private void getStreams() throws IOException {

        output = new ObjectOutputStream(client.getOutputStream());
        output.writeInt(directionPressed);
        output.flush();


        input = new ObjectInputStream(client.getInputStream());

        displayMessage("\nGot I/O streams\n");
    }

    // process connection with server
    private void processConnection() throws IOException {
        do {
            try {
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

            } catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            } catch (NumberFormatException e) {
                displayMessage("\nReceived score which is NaN");
            }


        } while (!(player1.getText().equals("10") || player2.getText().equals("10")));
    }

    // close streams and socket
    private void closeConnection() {
        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    // send message to server
    private void sendData(String message) {
        try {
            output.writeObject("CLIENT>>> " + message);
            output.flush();
            displayMessage("\nCLIENT>>> " + message);
        } catch (IOException ioException) {
            displayArea.append("\nError writing object");
        }
    }

    // manipulates displayArea in the event-dispatch thread
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        myMessageBox.setText(messageToDisplay);
                    }
                }
        );
    }
}

class Main {
    public static void main(String[] args) {
        new PongClient("Client");
    }
}