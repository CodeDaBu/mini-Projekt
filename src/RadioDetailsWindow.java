import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RadioDetailsWindow extends JFrame {

    public RadioDetailsWindow(String radioName) {
        setTitle("Podrobnosti o radijski postaji");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 5, 5));

        try (Connection conn = com.example.miniprojekt.db.DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM pridobi_podrobnosti_radia(?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, radioName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                panel.add(new JLabel("Ime:"));
                panel.add(new JLabel(rs.getString("ime")));

                panel.add(new JLabel("Frekvenca:"));
                panel.add(new JLabel(String.valueOf(rs.getDouble("frekvenca"))));

                panel.add(new JLabel("Kanal:"));
                panel.add(new JLabel(rs.getString("channel")));

                panel.add(new JLabel("Velja do:"));
                panel.add(new JLabel(rs.getDate("valid_until").toString()));

                panel.add(new JLabel("Telefon:"));
                panel.add(new JLabel(rs.getString("phone")));

                panel.add(new JLabel("Email:"));
                panel.add(new JLabel(rs.getString("email")));
            } else {
                JOptionPane.showMessageDialog(this, "Radijska postaja ni najdena.");
                dispose();
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri nalaganju podrobnosti: " + e.getMessage());
        }

        // Prazen element za estetiko
        panel.add(new JLabel(""));

        add(panel);
    }
}
