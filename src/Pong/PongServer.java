package Pong;// Fig. 27.5: Server.java
// Server portion of a client/server stream-socket connection. 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class PongServer
{
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private Point ball = new Point(350, 40),
           paddleLeft = new Point(30, 280),
           paddleRight = new Point(620, 280);
   private double ball_dx = 3, ball_dy = 3;
   private final int BALL_DIAMETER = 40;
   private final int PADDLE_WIDTH = 30;
   private final int PADDLE_HEIGHT = 140;
   private int player2Score = 0;
   private int player1Score = 0;
   private int connectionNumber = 0;
   ClientThread[] players = new ClientThread[2];

   // set up GUI
   public PongServer()
   {
      runServer();
   }

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
               startGame();
            } // end try
            catch ( EOFException eofException )
            {
               eofException.printStackTrace();
            } // end catch
            finally 
            {
               closeConnection(); //  close connection
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   private void startGame() {
      Random random = new Random();
      Timer ballUpdater = new Timer(5, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            ball.translate((int) ball_dx,(int) ball_dy);
            if(((ball.x < 60 && ball.x > 0) &&
                    ((ball.y < paddleLeft.y + PADDLE_HEIGHT) && (ball.y > paddleLeft.y))
                    && ball_dx < 0)
                    || ((ball.x > 580 && ball.x < 645) &&
                    ((ball.y < paddleRight.y + PADDLE_HEIGHT) && (ball.y > paddleRight.y))
                    && ball_dx > 0))
            {
               ball_dx = -ball_dx;
            }
            if(ball.x < 0) {
               player2Score++;
               ball.x = random.nextInt(200) + 400;
               if(player2Score >= 10) {
                  System.out.println("Player 2 wins!");
                  closeConnection();
                  System.exit(0);
               }
            }
            if (ball.x > 645) {
               player1Score++;
               ball.x = random.nextInt(200);
               if(player1Score >= 10) {
                  System.out.println("Player 1 wins!");
                  closeConnection();
                  System.exit(0);
               }
            }
            if(ball.y < 0 || ball.y > 620) {
               ball_dy = -ball_dy;
            }
         }
      });
      ballUpdater.start();
   }

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {

      while (connectionNumber < 2) {
         try {
            connection = server.accept();
         } catch (IOException e) {
            e.printStackTrace();
         }
         players[connectionNumber] = new ClientThread(connection, connectionNumber);
         connectionNumber++;
      }
      players[0].start();
      players[1].start();

   } // end method waitForConnection

   // close streams and socket
   private void closeConnection() 
   {
      try 
      {
         for(int i = 0; i < players.length; i++) {
            players[i].close();
         }
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection


   private class ClientThread extends Thread {
      private Socket socket;
      private ObjectInputStream inputStream;
      private ObjectOutputStream outputStream;
      private int playerID;
      public ClientThread(Socket s, int id) {
         this.socket = s;
         playerID = id;
      }

      public void run() {
         try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
         } catch (IOException e) {
            e.printStackTrace();
         }
         String inputString = "";
         while (true) {

            try {
               outputStream.writeObject(String.valueOf(ball.x));
               outputStream.writeObject(String.valueOf(ball.y));
               outputStream.writeObject(String.valueOf(paddleLeft.x));
               outputStream.writeObject(String.valueOf(paddleLeft.y));
               outputStream.writeObject(String.valueOf(paddleRight.x));
               outputStream.writeObject(String.valueOf(paddleRight.y));
               outputStream.flush();
            } catch (IOException e) {
               e.printStackTrace();
            }

            try {
               inputString = (String) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
               e.printStackTrace();
            }

            if (inputString.equals("UP") && playerID == 0) {
               if (paddleRight.y > 0) {
                  paddleRight.translate(0, -50);
               }
            }
            if (inputString.equals("DOWN") && playerID == 0) {
               if (paddleRight.y < 500) {
                  paddleRight.translate(0, 50);
               }
            }

            if (inputString.equals("UP") && playerID == 1) {
               if (paddleLeft.y > 0) {
                  paddleLeft.translate(0, -50);
               }
            }
            if (inputString.equals("DOWN") && playerID == 1) {
               if (paddleLeft.y < 500) {
                  paddleLeft.translate(0, 50);
               }
            }
         }
      }


      public void close() throws IOException {
         this.inputStream.close();
         this.outputStream.close();
         this.socket.close();
      }
   }
} // end class Server

/**************************************************************************
 * (C) Copyright 1992-2010 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/