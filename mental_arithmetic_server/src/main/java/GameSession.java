import lombok.Setter;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
@Setter
public class GameSession implements Runnable {
    private Socket playerOne;
    private Socket playerTwo;
    private CommandHandler handlerOne;
    private CommandHandler handlerTwo;
    private int readyCount = 0;
    private int answerCount = 0;
    private int sumOfSequence;

    public GameSession(Socket playerOne, Socket playerTwo) throws IOException {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.handlerOne = new CommandHandler(playerOne, this);
        this.handlerTwo = new CommandHandler(playerTwo, this);
        handlerOne.setOpponentHandler(handlerTwo);
        handlerTwo.setOpponentHandler(handlerOne);
    }

    @Override
    public void run() {
        new Thread(() -> handlerOne.handleCommands()).start();
        new Thread(() -> handlerTwo.handleCommands()).start();
    }

    public void playerReady() {
        synchronized (new Object()) {
            readyCount++;
            if (readyCount == 2) {
                // Оба игрока готовы, отправляем данные
                String sequence = generateSequence();
                System.out.println("sequence: " + sequence);
                System.out.println("sum of sequence: " + sumOfSequence);
                handlerOne.sendCommand(3, handlerTwo.getUserName(), sequence);
                handlerTwo.sendCommand(3, handlerOne.getUserName(), sequence);
            }
        }
    }

    public void answerReady() {
        synchronized (new Object()) {
            answerCount++;
            if (answerCount != 2) return;

            int resultOne = handlerOne.getResult();
            int resultTwo = handlerTwo.getResult();
            String userOneName = handlerOne.getUserName();
            String userTwoName = handlerTwo.getUserName();

            String messageOne, messageTwo;

            if (resultOne == resultTwo) {
                if (resultOne == 1) {
                    messageOne = "Поздравляем, вы дали верный ответ!";
                    messageTwo = "пользователь %s тоже дал верный ответ, это ничья!";
                    handlerOne.sendCommand(5,messageOne, String.format(messageTwo,userTwoName),
                            String.valueOf(sumOfSequence));
                    handlerTwo.sendCommand(5,messageOne, String.format(messageTwo,userOneName),
                            String.valueOf(sumOfSequence));
                } else {
                    messageOne = "К сожалению, ваш ответ неверный!";
                    messageTwo = "пользователь %s тоже дал неверный ответ, это ничья!";
                    handlerOne.sendCommand(5,messageOne,String.format(messageTwo,userTwoName),
                            String.valueOf(sumOfSequence));
                    handlerTwo.sendCommand(5,messageOne,String.format(messageTwo,userOneName),
                            String.valueOf(sumOfSequence));
                }
            } else {
                if (resultOne == 1) {
                    messageOne = "Поздравляем, ваш ответ верный!";
                    messageTwo = "пользователь " + userTwoName + " дал неверный ответ, вы победили!";
                    handlerOne.sendCommand(5,messageOne,messageTwo,String.valueOf(sumOfSequence));
                    messageOne = "К сожалению, ваш ответ неверный!";
                    messageTwo = "пользователь " + userOneName + " дал верный ответ, вы проиграли!";
                    handlerTwo.sendCommand(5,messageOne,messageTwo,String.valueOf(sumOfSequence));
                } else {
                    messageOne = "К сожалению, ваш ответ неверный!";
                    messageTwo = "пользователь " + userTwoName + " дал верный ответ, вы проиграли!";
                    handlerOne.sendCommand(5,messageOne,messageTwo,String.valueOf(sumOfSequence));
                    messageOne = "Поздравляем, ваш ответ верный!";
                    messageTwo = "пользователь " + userOneName + " дал неверный ответ, вы победили!";
                    handlerTwo.sendCommand(5,messageOne,messageTwo,String.valueOf(sumOfSequence));
                }
            }
        }
    }

    private String generateSequence() {
        Random random = new Random();
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            int number = random.nextInt(61) - 30;
            sumOfSequence += number;
            sequence.append((number >= 0 ? "+" : "")).append(number).append("|");
        }
        handlerOne.setSumOfSequence(sumOfSequence);
        handlerTwo.setSumOfSequence(sumOfSequence);
        return sequence.toString();
    }
}