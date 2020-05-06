package Pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class PongParent extends JFrame {
    enum Modes {SERVER {
        @Override
        public String toString() {
            return "Server";
        }
    }, CLIENT {
        @Override
        public String toString() {
            return "Client";
        }
    }}
    private JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
    GamePanel gamePanel = new GamePanel();
    protected MyScoreboard scoreboard = new MyScoreboard(player1, player2);
    private Point paddle;
    String  pongClient;
    private JLabel displayArea = new JLabel("");
    protected Socket connection;
    protected ObjectOutputStream output;
    protected ObjectInputStream input;


    public PongParent(Modes mode) {
        this(mode, "");
    }

    public PongParent(Modes mode, String host) {
        super( mode.toString());
        setSize( 700, 735 );
        setVisible( true );
        setLayout(new BorderLayout());
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        add(displayArea, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        if(mode == Modes.CLIENT) {
            pongClient = host;
            paddle = gamePanel.getPaddleRight();
        } else {
            paddle = gamePanel.getPaddleLeft();
        }
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if (paddle.y > 0) {
                        paddle.translate(0, -50);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (paddle.y < 500) {
                        paddle.translate(0, 50);
                    }
                }
                if(mode == Modes.CLIENT) {
                    sendData();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    protected abstract void connect() throws IOException;

    protected abstract void runConnection();

    protected abstract void sendData();

    protected void closeConnection()
    {
        try
        {
            output.close();
            input.close();
            connection.close();
        }
        catch ( IOException ioException )
        {
            ioException.printStackTrace();
        }
    }

    protected void getStreams() throws IOException
    {
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush();
        input = new ObjectInputStream( connection.getInputStream() );

        displayMessage( "\nGot I/O streams\n" );
    }

    protected void displayMessage( final String messageToDisplay )
    {
        SwingUtilities.invokeLater(
                () -> displayArea.setText( messageToDisplay )
        );
    }

    protected JLabel getPlayer1() {
        return player1;
    }

    protected JLabel getPlayer2() {
        return player2;
    }


}
