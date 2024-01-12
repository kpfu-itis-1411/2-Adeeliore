import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

// result = 1 - win, result = 0 - loose;
@Getter@Setter
public class CommandHandler {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final ByteProtocol protocol;
    private String userName;
    private CommandHandler opponentHandler;
    private GameSession gameSession;
    private int sumOfSequence;
    private int result;
    public CommandHandler(Socket socket, GameSession gameSession) throws IOException {
        this.socket = socket;
        this.gameSession = gameSession;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.protocol = new ByteProtocol();
    }

    public void handleCommands() {
        sendCommand(1);

        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] commandBytes = Arrays.copyOfRange(buffer, 0, bytesRead);
                handleCommand(commandBytes);
            }
        } catch (SocketException e) {
            System.out.println(userName + " отключился, соединение разорвано");
            opponentHandler.sendCommand(6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(byte[] commandBytes) {
        int command = protocol.parseCommand(commandBytes);
        String[] args = protocol.parseArgs(commandBytes);

        switch (command) {
            case 2: // USERNAME
                handleUsername(args);
                break;
            case 4: // ANSWER
                handleAnswer(args);
                break;
            default:
                throw new IllegalArgumentException("Unknown command");
        }
    }

    private void handleUsername(String[] args) {
        userName = args[0];
        gameSession.playerReady();
    }

    private void handleAnswer(String[] args) {
        if (args[0].equals("")) {
            result = 0;
            return;
        }
        int answer = Integer.parseInt(args[0]);
        if (answer == sumOfSequence) {
            result = 1;
            gameSession.answerReady();
        } else {
            result = 0;
            gameSession.answerReady();
        }
    }


    public void sendCommand(int command, String... args) {
        byte[] commandBytes = protocol.createCommand((byte) command, args);
        try {
            out.write(commandBytes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
