import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

public class Login {

    private Connection conn;

    public Login() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vrne ID uporabnika, ali -1 če prijava ni uspešna
    public int loginUser(String username, String password) {
        try {
            String query = "SELECT prijava_uporabnika(?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);  // vrne ID ali -1
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean registerUser(String username, String password) {
        try {
            String query = "SELECT registracija_uporabnika(?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
