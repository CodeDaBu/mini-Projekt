import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentsWindow extends JFrame {
    private int radioId;
    private String radioName;
    private int userId; // trenutno prijavljen uporabnik

    private JTextArea commentsArea;
    private JTextField inputField;

    public CommentsWindow(int radioId, String radioName, int userId) {
        this.radioId = radioId;
        this.radioName = radioName;
        this.userId = userId;

        setTitle("Komentarji za: " + radioName);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        commentsArea = new JTextArea();
        commentsArea.setEditable(false);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(commentsArea);

        inputField = new JTextField();
        JButton submitButton = new JButton("Dodaj komentar");

        submitButton.addActionListener(e -> addComment());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        loadComments();
    }

    private void loadComments() {
        commentsArea.setText("");
        try (Connection conn = com.example.miniprojekt.db.DatabaseManager.getConnection()) {
            String sql = """
                SELECT c.comment_text, c.created_at, u.username
                FROM comments c
                JOIN users u ON c.user_id = u.id
                WHERE c.frequency_id = ?
                ORDER BY c.created_at DESC
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, radioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String user = rs.getString("username");
                Timestamp time = rs.getTimestamp("created_at");
                String text = rs.getString("comment_text");

                commentsArea.append("[" + time + "] " + user + ": " + text + "\n\n");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri nalaganju komentarjev: " + e.getMessage());
        }
    }

    private void addComment() {
        String comment = inputField.getText().trim();
        if (comment.isEmpty()) return;

        try (Connection conn = com.example.miniprojekt.db.DatabaseManager.getConnection()) {
            String sql = "INSERT INTO comments (comment_text, frequency_id, user_id, created_at) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, comment);
            stmt.setInt(2, radioId);
            stmt.setInt(3, userId);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            inputField.setText("");
            loadComments();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Napaka pri dodajanju komentarja: " + e.getMessage());
        }
    }
}
