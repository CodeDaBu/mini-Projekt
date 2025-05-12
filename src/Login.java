import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

public class Login {

    private Connection conn;

    // Konstruktor za povezavo z bazo
    public Login() {
        connectToDatabase();
    }

    // Povezava z bazo
    private void connectToDatabase() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Preveri, ali uporabnik obstaja v bazi
    public boolean isValidUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {  // Če uporabnik obstaja
                return true;
            } else {
                System.out.println("Neveljavna uporabniško ime ali geslo.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Dodaj uporabnika v bazo
    public boolean registerUser(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;  // Če je uporabnik uspešno vstavljen v bazo
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
