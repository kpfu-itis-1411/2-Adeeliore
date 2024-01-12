import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {
    private static final int PORT = 6112;
    private static final int MAX_SESSIONS = 10;
    private static ExecutorService pool = Executors.newFixedThreadPool(MAX_SESSIONS * 2);
    private static BlockingQueue<Socket> waitingPlayers = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket player = listener.accept();
            System.out.println("[SERVER] Connected to player!");
            waitingPlayers.add(player);
            if (waitingPlayers.size() >= 2) {
                Socket playerOne = waitingPlayers.poll();
                Socket playerTwo = waitingPlayers.poll();
                GameSession game = new GameSession(playerOne, playerTwo);
                pool.execute(game);
            }
        }
    }
}
