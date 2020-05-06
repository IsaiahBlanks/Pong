package Pong;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PongParent extends JFrame {
    protected GamePanel gamePanel = new GamePanel();
    protected JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
    protected MyScoreboard scoreboard = new MyScoreboard(player1, player2);
    protected JLabel displayArea = new JLabel();
    protected ObjectOutputStream output;
    protected ObjectInputStream input;
    protected Socket connection;


    public PongParent(String title) {
        super(title);
        setSize(700, 730);
        setLayout(new BorderLayout());
        add(gamePanel);
        add(scoreboard, BorderLayout.NORTH);
        add(displayArea, BorderLayout.SOUTH);
        displayArea.setSize(700, 10);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void run() {}

    public void getStreams() throws IOException
    {

        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush();

        input = new ObjectInputStream( connection.getInputStream() );

        displayArea.setText("Got I/O streams" );
    }

    public void closeConnection()
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

    public void sendData() {}

    public void processConnection() throws IOException {}





}
