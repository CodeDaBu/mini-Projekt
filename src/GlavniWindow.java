import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

public class GlavniWindow extends JFrame {

    private int userId;

    public GlavniWindow(int userId) {
        this.userId = userId;

        setTitle("Seznam radijskih postaj");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Ime"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Spodnji panel za gumbe
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Gumb za odjavo
        JButton logoutButton = new JButton("Odjava");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI(); // Če LoginUI nima userId, tega ni treba spreminjati
        });

        // Gumb za komentarje
        JButton commentButton = new JButton("Komentiraj");
        commentButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String radioName = (String) model.getValueAt(selectedRow, 0);
                int radioId = getRadioIdByName(radioName);
                if (radioId != -1) {
                    new CommentsWindow(radioId, radioName, userId).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Ni bilo mogoče najti ID-ja radia.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Najprej izberi radijsko postajo.");
            }
        });

        bottomPanel.add(commentButton);
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);

        // Napolni tabelo
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT ime FROM radio";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("ime")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri branju radijskih postaj: " + e.getMessage());
        }

        // Dvoklik za prikaz podrobnosti
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    String name = (String) model.getValueAt(row, 0);
                    showStationDetails(name);
                }
            }
        });
    }

    private void showStationDetails(String name) {
        new RadioDetailsWindow(name).setVisible(true);
    }

    // Metoda za pridobitev ID-ja postaje po imenu
    private int getRadioIdByName(String name) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT id FROM radio WHERE ime = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri iskanju ID-ja: " + e.getMessage());
        }
        return -1;
    }
}
