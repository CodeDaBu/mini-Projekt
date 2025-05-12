import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GlavniWindow extends JFrame {

    private JTable table;

    public GlavniWindow() {
        setTitle("Seznam radijskih postaj");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Ime"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Onemogoči urejanje celic
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        try (Connection conn = com.example.miniprojekt.db.DatabaseManager.getConnection()) {
            String sql = "SELECT id, ime FROM radio";  // Samo ime radijske postaje
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("ime")});
            }

            // Obdelava klikov na ime radijske postaje
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String radioName = (String) table.getValueAt(row, 0);
                        openRadioDetailsWindow(radioName);
                    }
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri branju radijskih postaj: " + e.getMessage());
        }

        // Dodaj gumb za Log Out
        JButton logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> logout());

        JPanel panel = new JPanel();
        panel.add(logOutButton);
        add(panel, BorderLayout.SOUTH);
    }

    private void openRadioDetailsWindow(String radioName) {
        SwingUtilities.invokeLater(() -> new RadioDetailsWindow(radioName).setVisible(true));
    }

    private void logout() {
        this.dispose();
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }

    public static void main(String[] args) {
        new GlavniWindow().setVisible(true);
    }
}
