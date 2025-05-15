import java.sql.*;
import com.example.miniprojekt.db.DatabaseManager;

public class Login {

    // Vrne ID uporabnika ali -1, če prijava ni uspešna
    public int loginUser(String username, String password) {
        String query = "SELECT prijava_uporabnika(?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password); // geslo v plain text, preverja se v SQL funkciji

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // ID uporabnika ali -1
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Registracija uporabnika - vrne true, če uspe
    public boolean registerUser(String username, String password) {
        String query = "SELECT registracija_uporabnika(?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password); // geslo plain text, SQL ga šifrira

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1); // true, če uspe
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
