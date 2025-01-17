package databasesystems.project2;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Toolkit;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginScreen() {
        setTitle("Login/Sign-up");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        // Center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(180, 80, 80, 25);
        panel.add(signUpButton);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addSignUpListener(ActionListener listener) {
        signUpButton.addActionListener(listener);
    }
}
