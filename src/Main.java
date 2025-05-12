import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 200);
        frame.setLocationRelativeTo(null); // center on screen
        frame.setLayout(new GridLayout(4, 1, 10, 10));

        // Username input
        JPanel userPanel = new JPanel(new FlowLayout());
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        userPanel.add(userLabel);
        userPanel.add(userField);

        // Password input
        JPanel passPanel = new JPanel(new FlowLayout());
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        passPanel.add(passLabel);
        passPanel.add(passField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(80, 40));


        // Message label
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

        // Add components to frame
        frame.add(userPanel);
        frame.add(passPanel);
        frame.add(loginButton);
        frame.add(messageLabel);

        // Button action
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if ("admin".equals(username) && "admin".equals(password)) {
                    messageLabel.setText("Login successful!");
                    JOptionPane.showMessageDialog(frame, "Welcome, admin!");
                    // You can open a new window or switch UI here
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            }
        });

        frame.setVisible(true);
    }
}
