package Pong;// Fig. 27.5: Server.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class PongServer extends JFrame {
   private JTextField enterField; // inputs message from user
   private JTextArea displayArea; // display information to user
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private int counter = 1; // counter of number of connections
   private final int BALL_DIAMETER = 40;
   private final int PADDLE_WIDTH = 30;
   private final int PADDLE_HEIGHT = 140;
   private Point ball = new Point(350, 40),
           paddleLeft = new Point(30, 280),
           paddleRight = new Point(620, 280);
   private double ball_dx = 3, ball_dy = 3;
   private GamePanel gamePanel = new GamePanel();
   private JLabel player1 = new JLabel("0"), player2 = new JLabel("0");
   private MyScoreboard scoreboard;


   // set up GUI
   public PongServer()
   {
      super( "Server" );

      enterField = new JTextField(); // create enterField
      enterField.setEditable( false );
      enterField.addActionListener(
              new ActionListener()
              {
                 // send message to client
                 public void actionPerformed( ActionEvent event )
                 {
                    sendData( event.getActionCommand() );
                    enterField.setText( "" );
                 } // end method actionPerformed
              } // end anonymous inner class
      ); // end call to addActionListener

      add( enterField, BorderLayout.NORTH );

      displayArea = new JTextArea(); // create displayArea
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );

      setSize( 300, 150 ); // set size of window
      setVisible( true ); // show window
   } // end Server constructor

   // set up and run server
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while ( true )  // infinite loop
         {
            try
            {
               waitForConnection(); // wait for a connection from Client
               getStreams(); // get input & output streams
               processConnection(); // process connection
            } // end try
            catch ( EOFException eofException )
            {
               displayMessage( "\nServer terminated connection" );
            } // end catch
            finally
            {
               closeConnection(); //  close connection
               ++counter;
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      // blocking call (synchronous call.... not asynchronous (= on demand...where your code hets called back like an event handler)
      connection = server.accept(); // allow server to accept connection
      displayMessage( "Connection " + counter + " received from: " +
              connection.getInetAddress().getHostName() );
      startGame();
   } // end method waitForConnection

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() ); // Decorator DP
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with client
   private void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); // send connection successful message

      // enable enterField so server user can send messages
      setTextFieldEditable( true );

      do // process messages sent from client
      {
         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message
            displayMessage( "\n" + message ); // display message
         } // end try
         catch ( ClassNotFoundException classNotFoundException )
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   } // end method processConnection

   // close streams and socket
   private void closeConnection()
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); // disable enterField

      try
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   private void sendData( String message )
   {
      try // send object to client
      {
         output.writeObject( paddleLeft.x );
         output.writeObject( paddleLeft.y );
         output.flush(); // flush output to client
         displayMessage( "\nSERVER>>> " + message );
      } // end try
      catch ( IOException ioException )
      {
         displayArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData

   // manipulates displayArea in the event-dispatch thread
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater( // call on the EDT - Event Dispatch Thread -- only thread safe to update GUI because Swing is not thread safe
              new Runnable()
              {
                 public void run() // updates displayArea
                 {
                    displayArea.append( messageToDisplay ); // append message
                 } // end method run
              } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage

   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
              new Runnable()
              {
                 public void run() // sets enterField's editability
                 {
                    enterField.setEditable( editable );
                 } // end method run
              }  // end inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method setTextFieldEditable

   private void startGame() {
      Random random = new Random();
      scoreboard = new MyScoreboard(player1, player2);
      Timer ballUpdater = new Timer(5, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
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
               if (player2.getText().equals("10.0")) {
                  System.out.println("Player 2 wins!");
                  System.exit(0);
               }
            }
            if (ball.x > 645) {
               scoreboard.changeScore(player1);
               ball.x = random.nextInt(200);
               if (player2.getText().equals("10.0")) {
                  System.out.println("Player 2 wins!");
                  System.exit(0);
               }
            }
            if (ball.y < 0 || ball.y > 620) {
               ball_dy = -ball_dy;
            }
            repaint();
         }
      });

      addKeyListener(new KeyListener() {
         @Override
         public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == '+') {
               ball_dx *= 1.05;
               ball_dy *= 1.05;
            }
            if (e.getKeyChar() == '-') {
               ball_dx *= 0.95;
               ball_dy *= 0.95;
            }
         }

         @Override
         public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
               if (paddleRight.y > 0) {
                  paddleRight.translate(0, -50);
               }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
               if (paddleRight.y < 500) {
                  paddleRight.translate(0, 50);
               }
            }
         }

         @Override
         public void keyReleased(KeyEvent e) {
         }
      });
   }

   static class MyScoreboard extends JPanel {
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
         } catch (NumberFormatException e) {
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
}

