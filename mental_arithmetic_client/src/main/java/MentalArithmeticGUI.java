import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@Getter@Setter
public class MentalArithmeticGUI {
    private JFrame frame;
    private CardLayout cards;
    private Player player;
    private ByteProtocol protocol;
    private String sequence;
    private JLabel opponentNameLabel;
    private JTextField answerField;
    private JTextField resultField;
    private JTextField trueAnswerField;

    public MentalArithmeticGUI() {
        FlatMacLightLaf.setup();
        frame = new JFrame("Mental Arithmetic");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cards = new CardLayout();
        frame.setLayout(cards);

        protocol = new ByteProtocol();

        frame.add(createServerIPPanel(), "SERVER_IP_PANEL");
        frame.add(createMainPanel(), "MAIN_PANEL");
        frame.add(createUserNameInputPanel(), "USERNAME_PANEL");
        frame.add(createResultPanel(),"RESULT_PANEL");
        frame.add(createReconnectPanel(),"RECONNECT_PANEL");


        frame.setVisible(true);
    }

    public JPanel createServerIPPanel() {
        JPanel serverIPPanel = new JPanel();
        serverIPPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("ДОБРО ПОЖАЛОВАТЬ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setBorder(new EmptyBorder(150, 0, 0, 0));
        serverIPPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBorder(new EmptyBorder(50, 0, 10, 0));

        JLabel arithmeticsLabel = new JLabel("Mental Arithmetics:", SwingConstants.CENTER);
        arithmeticsLabel.setFont(new Font("SansSerif", Font.PLAIN, 25));
        arithmeticsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(arithmeticsLabel);

        JLabel duelLabel = new JLabel("\"The Duel\"", SwingConstants.CENTER);
        duelLabel.setFont(new Font("SansSerif", Font.PLAIN, 23));
        duelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(duelLabel);

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        errorPanel.add(errorLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(gamePanel);
        centerPanel.add(errorPanel);

        serverIPPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel instructionLabel = new JLabel("Введите IP адрес");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(instructionLabel);

        JTextField serverIPField = new JTextField(35);
        inputPanel.add(serverIPField);

        JButton connectButton = new JButton("Connect");
        connectButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inputPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = serverIPField.getText();
                if (!serverIP.isEmpty()) {
                    try {
                        player = new Player(serverIP, MentalArithmeticGUI.this);
                        player.startCommandHandler();
                        cards.show(frame.getContentPane(),"MAIN_PANEL");
                    } catch (IOException ex) {
                        errorLabel.setText("Подключение не удалось, попробуйте снова");
                    }
                }
            }
        });

        serverIPPanel.add(inputPanel, BorderLayout.SOUTH);

        return serverIPPanel;
    }

    public JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel waitingLabel = new JLabel("Ожидание подключения второго игрока", SwingConstants.CENTER);
        waitingLabel.setFont(new Font("SansSerif", Font.PLAIN, 27));
        mainPanel.add(waitingLabel, BorderLayout.CENTER);

        return mainPanel;
    }

    public JPanel createUserNameInputPanel() {
        JPanel userNameInputPanel = new JPanel();
        userNameInputPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JLabel instructionLabel = new JLabel("Введите username: ");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        inputPanel.add(instructionLabel);

        JTextField userNameField = new JTextField(20);
        userNameField.setFont(new Font("SansSerif", Font.PLAIN, 22));
        inputPanel.add(userNameField);

        userNameInputPanel.setBorder(new EmptyBorder(150,0,0,0));

        userNameInputPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel feedbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel feedbackLabel = new JLabel("");
        feedbackLabel.setFont(new Font("SansSerif",Font.HANGING_BASELINE,18));
        feedbackPanel.setBorder(new EmptyBorder(50,0,0,0));
        feedbackPanel.add(feedbackLabel);
        userNameInputPanel.add(feedbackPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Готов");
        submitButton.setFont(new Font("SansSerif",Font.PLAIN,30));
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameField.getText();
                if (userName.isBlank()) {
                    feedbackLabel.setText("Вы ввели пустой юзернейм");
                } else {
                    player.setUsername(userName);
                    player.getHandler().sendCommand(2,userName);
                    feedbackLabel.setText("Юзернейм отправлен, ожидаем другого игрока");
                }
            }
        });
        buttonPanel.add(submitButton);
        userNameInputPanel.add(buttonPanel, BorderLayout.SOUTH);

        return userNameInputPanel;
    }


    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());

        opponentNameLabel = new JLabel("Имя оппонента", SwingConstants.CENTER);
        opponentNameLabel.setFont(new Font("SansSerif",Font.HANGING_BASELINE,25));
        gamePanel.add(opponentNameLabel, BorderLayout.NORTH);

        JLabel sequenceLabel = new JLabel("", SwingConstants.CENTER);
        sequenceLabel.setFont(new Font("SansSerif",Font.BOLD,80));
        gamePanel.add(sequenceLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextField answerField = new JTextField();
        answerField.setFont(new Font("SansSerif",Font.PLAIN,25));
        bottomPanel.add(answerField, BorderLayout.CENTER);

        JButton submitButton = new JButton("Отправить ответ");
        submitButton.setFont(new Font("SansSerif",Font.BOLD,20));
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String answer = answerField.getText();
                player.getHandler().sendCommand(4,answer);
            }
        });
        bottomPanel.add(submitButton, BorderLayout.EAST);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        if (sequence != null) {
            String[] numbers = sequence.split("\\|");
            Timer sequenceTimer = new Timer(1000, new ActionListener() {
                int index = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (index < numbers.length) {
                        sequenceLabel.setText(numbers[index]);
                        index++;
                    } else {
                        ((Timer)e.getSource()).stop();
                        answerField.setEnabled(true);
                        JLabel timerLabel = new JLabel("Время для ввода ответа: 10", SwingConstants.CENTER);
                        timerLabel.setFont(new Font("SansSerif",Font.PLAIN,25));
                        bottomPanel.add(timerLabel, BorderLayout.WEST);
                        Timer answerTimer = new Timer(1000, new ActionListener() {
                            int timeLeft = 10;

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (timeLeft > 0) {
                                    timeLeft--;
                                    timerLabel.setText("Время для ввода ответа: " + String.valueOf(timeLeft));
                                } else {
                                    answerField.setEnabled(false);
                                    ((Timer)e.getSource()).stop();
                                }
                            }
                        });
                        answerTimer.start();
                    }
                }
            });
            sequenceTimer.setInitialDelay(5000);
            sequenceTimer.start();
        }

        return gamePanel;
    }
    public void updateGamePanel() {
        frame.add(createGamePanel(), "GAME_PANEL");
    }
    public JPanel createResultPanel() {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(2, 1));

        answerField = new JTextField();
        answerField.setFont(new Font("SansSerif",Font.PLAIN,35));
        answerField.setHorizontalAlignment(JTextField.CENTER);
        answerField.setEditable(false);
        fieldPanel.add(answerField);

        resultField = new JTextField();
        resultField.setFont(new Font("SansSerif",Font.PLAIN,32));
        resultField.setHorizontalAlignment(JTextField.CENTER);
        resultField.setEditable(false);
        fieldPanel.add(resultField);

        resultPanel.add(fieldPanel, BorderLayout.CENTER);

        trueAnswerField = new JTextField();
        trueAnswerField.setText("Верный ответ: ");
        trueAnswerField.setEditable(false);
        trueAnswerField.setFont(new Font("SansSerif",Font.PLAIN,25));
        trueAnswerField.setHorizontalAlignment(JTextField.CENTER);
        resultPanel.add(trueAnswerField, BorderLayout.SOUTH);

        return resultPanel;
    }


    public JPanel createReconnectPanel() {
        JPanel reconnectPanel = new JPanel();
        reconnectPanel.setLayout(new BorderLayout());

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextArea textArea = new JTextArea("Сервер или ваш оппонент отключились, попробуйте снова");
        textArea.setEditable(false);
        textArea.setFont(new Font("SansSerif",Font.BOLD,25));
        textPanel.add(textArea);
        reconnectPanel.add(textPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton reconnectButton = new JButton("Подключиться заново");
        reconnectButton.setFont(new Font("SansSerif",Font.PLAIN,35));
        reconnectButton.addActionListener(e -> {
            recreatePanels();
            cards.show(frame.getContentPane(),"SERVER_IP_PANEL");
        });
        buttonPanel.add(reconnectButton);
        reconnectPanel.add(buttonPanel, BorderLayout.SOUTH);

        return reconnectPanel;
    }


    public void recreatePanels() {
        frame.remove(createServerIPPanel());
        frame.remove(createMainPanel());
        frame.remove(createUserNameInputPanel());
        frame.remove(createResultPanel());
        frame.remove(createReconnectPanel());

        frame.add(createServerIPPanel(), "SERVER_IP_PANEL");
        frame.add(createMainPanel(), "MAIN_PANEL");
        frame.add(createUserNameInputPanel(), "USERNAME_PANEL");
        frame.add(createResultPanel(),"RESULT_PANEL");
        frame.add(createReconnectPanel(),"RECONNECT_PANEL");
    }

    public static void main(String[] args) {
        new MentalArithmeticGUI();
    }
}
