import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteProtocol {
    public static final int SESSION_LOADED = 1;
    public static final int USERNAME = 2;
    public static final int NUMBERS = 3;
    public static final int ANSWER = 4;
    public static final int RESULT = 5;
    public static final byte ARG_SEPARATOR = (byte) ';';
    public static final byte CMD_SEPARATOR = (byte) ':';
    public byte[] createCommand(byte command, String... args) {

        int totalSize = 2;
        if (args.length != 0) {
            for (String arg : args) {
                totalSize += arg.getBytes(StandardCharsets.UTF_8).length + 1;
            }
            totalSize--;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);

        buffer.put(command);
        buffer.put(CMD_SEPARATOR);

        for (int i = 0; i < args.length; i++) {
            buffer.put(args[i].getBytes(StandardCharsets.UTF_8));
            if (i < args.length - 1) {
                buffer.put(ARG_SEPARATOR);
            }
        }

        return buffer.array();
    }

    public int parseCommand(byte[] commandBytes) {
        int command = commandBytes[0];
        if (command < 1 || command > 5) {
            throw new IllegalArgumentException("Unknown command");
        }
        return command;
    }
    public String[] parseArgs(byte[] commandBytes) {
        int separatorIndex = -1;
        for (int i = 0; i < commandBytes.length; i++) {
            if (commandBytes[i] == ':') {
                separatorIndex = i;
                break;
            }
        }
        if (separatorIndex == -1) {
            throw new IllegalArgumentException("Invalid command format");
        }
        String argsString = new String(commandBytes, separatorIndex + 1, commandBytes.length - separatorIndex - 1, StandardCharsets.UTF_8);
        return argsString.split(";");
    }
}
