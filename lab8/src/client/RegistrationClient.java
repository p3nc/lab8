package client;

import conference.Conferee;
import conference.Registerable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistrationClient extends JFrame {
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField affiliationField;
    private JTextField emailField;
    private JTextField paperTitleField;
    private JButton registerButton;
    private JButton getConfereeListButton;
    private JTextArea outputTextArea;

    private Registerable registrationService;

    public RegistrationClient() {
        super("Реєстрація учасників конференції");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Ім'я:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Прізвище:"));
        surnameField = new JTextField();
        inputPanel.add(surnameField);
        inputPanel.add(new JLabel("Організація:"));
        affiliationField = new JTextField();
        inputPanel.add(affiliationField);
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Назва доповіді:"));
        paperTitleField = new JTextField();
        inputPanel.add(paperTitleField);
        registerButton = new JButton("Зареєструватися");
        inputPanel.add(registerButton);
        getConfereeListButton = new JButton("Отримати список");
        inputPanel.add(getConfereeListButton);
        add(inputPanel, BorderLayout.NORTH);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        add(scrollPane, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerConferee();
            }
        });

        getConfereeListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getConfereeList();
            }
        });

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registrationService = (Registerable) registry.lookup("RegistrationService");
        } catch (Exception ex) {
            System.err.println("Помилка клієнта: " + ex.toString());
            ex.printStackTrace();
        }

        setVisible(true);
    }

    private void registerConferee() {
        try {
            Conferee conferee = new Conferee(
                    nameField.getText(),
                    surnameField.getText(),
                    affiliationField.getText(),
                    emailField.getText(),
                    paperTitleField.getText()
            );
            int count = registrationService.registerConferee(conferee);
            outputTextArea.append("Учасник зареєстровано. Загальна кількість учасників: " + count + "\n");
        } catch (Exception ex) {
            System.err.println("Помилка реєстрації: " + ex.toString());
            ex.printStackTrace();
        }
    }

    private void getConfereeList() {
        try {
            String confereeList = registrationService.getConfereeList();
            outputTextArea.append("Список учасників:\n" + confereeList + "\n");
        } catch (Exception ex) {
            System.err.println("Помилка отримання списку учасників: " + ex.toString());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RegistrationClient();
    }
}