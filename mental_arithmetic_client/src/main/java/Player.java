import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;

@Setter@Getter
public class Player {
    private String username;
    private Socket socket;
    private MentalArithmeticGUI gui;
    private CommandHandler handler;

    public Player(String serverAddress, MentalArithmeticGUI gui) throws IOException {
        this.socket = new Socket(serverAddress, 6112);
        this.gui = gui;
    }

    public void startCommandHandler() {
        new Thread(() -> {
            try {
                handler = new CommandHandler(socket,gui);
                handler.handleCommands();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}