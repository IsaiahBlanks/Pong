package Pong;
import javax.swing.*;

class MyScoreboard extends JPanel {
    JLabel player1, player2;
    final int WIDTH = 50;
    final int HEIGHT = 500;
    MyScoreboard(JLabel p1, JLabel p2) {
        this.setSize(WIDTH, HEIGHT);
        JLabel divider = new JLabel(" : ");
        player1 = p1;
        player2 = p2;
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
        } catch(NumberFormatException e) {
            System.out.println("Invalid conversion of points. Expected number and got NaN");
        }
    }

    void setScorePlayer1(String score) {
        player1.setText(score);
    }

    int getScorePlayer1() {
        return Integer.parseInt(player1.getText());
    }

    void setScorePlayer2(String score) {
        player2.setText(score);
    }

    int getScorePlayer2() {
        return Integer.parseInt(player2.getText());
    }

}