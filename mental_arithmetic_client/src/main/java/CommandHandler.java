import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class CommandHandler {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final ByteProtocol protocol;
    private MentalArithmeticGUI gui;

    public CommandHandler(Socket socket, MentalArithmeticGUI gui) throws IOException {
        this.socket = socket;
        this.gui = gui;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.protocol = new ByteProtocol();
    }

    public void handleCommands() {
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] commandBytes = Arrays.copyOfRange(buffer, 0, bytesRead);
                handleCommand(commandBytes);
            }
        } catch (SocketException e) {
            System.out.println("сервер отключился, соединение разорвано");
            SwingUtilities.invokeLater(() -> {
                gui.getCards().show(gui.getFrame().getContentPane(), "RECONNECT_PANEL");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(byte[] commandBytes) {
        int command = protocol.parseCommand(commandBytes);
        String[] args = protocol.parseArgs(commandBytes);

        switch (command) {
            case 1: // SESSION_LOADED
                handleSessionLoaded(args);
                break;
            case 3: // NUMBERS
                handleNumbers(args);
                break;
            case 5: // RESULT
                handleResult(args);
                break;
            case 6: // CONNECTION_RESET
                handleConnectionReset(args);
                break;
            default:
                throw new IllegalArgumentException("Unknown command");
        }
    }

    private void handleSessionLoaded(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui.getCards().show(gui.getFrame().getContentPane(), "USERNAME_PANEL");
        });
    }

    private void handleNumbers(String[] args) {
        String opponentName = args[0];
        String sequence = args[1];
        System.out.println(opponentName + " " + sequence);
        gui.setSequence(sequence);
        gui.updateGamePanel();
        gui.getOpponentNameLabel().setText("Имя оппонента: " + opponentName);
        SwingUtilities.invokeLater(() -> {
            gui.getCards().show(gui.getFrame().getContentPane(), "GAME_PANEL");
        });
    }

    private void handleResult(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui.getAnswerField().setText(args[0]);
            gui.getResultField().setText(args[1]);
            gui.getTrueAnswerField().setText("Верный ответ: " + args[2]);
            gui.getCards().show(gui.getFrame().getContentPane(), "RESULT_PANEL");
        });
    }

    private void handleConnectionReset(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui.getCards().show(gui.getFrame().getContentPane(), "RECONNECT_PANEL");
        });
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
