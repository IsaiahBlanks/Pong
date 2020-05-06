package Pong;

public class ClientTest {
    public static void main(String[] args) {
        PongClient pong = new PongClient("localhost");
        pong.runConnection();
    }
}
