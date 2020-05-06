package Pong;
// Server portion of a client/server stream-socket connection.

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class PongServer extends PongParent {

   private ServerSocket server; // server socket
   private int counter = 1; // counter of number of connections
   private final int PADDLE_HEIGHT = 140;
   private Point ball = new Point(350, 40),
           paddleLeft = new Point(30, 280),
           paddleRight = new Point(620, 280);
   private double ball_dx = 3, ball_dy = 3;



   public PongServer()
   {
      super( Modes.SERVER );
   }

   @Override
   public void runConnection()
   {
      try
      {
         server = new ServerSocket( 12345, 100 );

         while ( true )
         {
            try
            {
               connect();
               getStreams();
               processConnection();
            }
            catch ( EOFException eofException )
            {
               displayMessage("\nServer terminated connection" );
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

   @Override
   protected void connect() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      // blocking call (synchronous call.... not asynchronous (= on demand...where your code hets called back like an event handler)
      connection = server.accept(); // allow server to accept connection
      System.out.println( "Connection " + counter + " received from: " +
              connection.getInetAddress().getHostName() );

   }

   // process connection with client
   private void processConnection()
   {
      System.out.println("About to start game");
      startGame();

      do // process messages sent from client
      {
         try // read message and display it
         {
            paddleRight.y = input.readInt();
            gamePanel.setPaddleRight(paddleRight);// read new message
         } // end try
         catch (IOException e) {
            displayMessage("Issue receiving data! \n" + e );
         }

      } while ( true );
   } // end method processConnection

   @Override
   protected void sendData()
   {
      try
      {
         System.out.println("Writing data!");
         output.writeInt( paddleLeft.y );
         output.writeInt(ball.x);
         output.writeInt(ball.y);
         output.writeObject(getPlayer1().getText());
         output.writeObject(getPlayer2().getText());
         output.flush();
      }
      catch ( IOException ioException )
      {
         displayMessage("\nError writing object" );
      }
   }

   private void startGame() {
      JLabel player1 = getPlayer1();
      JLabel player2 = getPlayer2();
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
            if (player2.getText().equals("10.0")) {
               System.out.println("Player 2 wins!");
               System.exit(0);
            }
         }
         if (ball.x > 645) {
            scoreboard.changeScore(player1);
            ball.x = random.nextInt(200);
            if (player1.getText().equals("10.0")) {
               System.out.println("Player 1 wins!");
               System.exit(0);
            }
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

