import com.example.miniprojekt.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.CallableStatement;

public class DodajPostajoWindow extends JFrame {

    public DodajPostajoWindow(GlavniWindowAdmin parent) {
        setTitle("Dodaj novo radijsko postajo");
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(8, 2));

        // Ustvari polja za vnos podatkov
        JTextField imeField = new JTextField();
        JTextField frekvencaField = new JTextField();
        JTextField kanalField = new JTextField();
        JTextField veljaDoField = new JTextField();
        JTextField telefonField = new JTextField();
        JTextField emailField = new JTextField();

        // Dodaj oznake in polja za vnos
        add(new JLabel("Ime:"));
        add(imeField);

        add(new JLabel("Frekvenca:"));
        add(frekvencaField);

        add(new JLabel("Kanal:"));
        add(kanalField);

        add(new JLabel("Velja do (YYYY-MM-DD):"));
        add(veljaDoField);

        add(new JLabel("Telefon:"));
        add(telefonField);

        add(new JLabel("Email:"));
        add(emailField);

        // Gumb za shranjevanje
        JButton shraniButton = new JButton("Shrani");
        shraniButton.addActionListener(e -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                // Klic funkcije dodaj_radio iz baze
                String sql = "{ call dodaj_radio(?, ?, ?, ?, ?, ?, NULL) }";
                // NULL za kraj_id, ker ga v UI nimaš (ali ga lahko dodaš, če želiš)

                CallableStatement stmt = conn.prepareCall(sql);

                stmt.setString(1, imeField.getText());
                stmt.setDouble(2, Double.parseDouble(frekvencaField.getText()));
                stmt.setString(3, kanalField.getText());
                stmt.setDate(4, java.sql.Date.valueOf(veljaDoField.getText()));
                stmt.setString(5, telefonField.getText());
                stmt.setString(6, emailField.getText());

                stmt.execute();

                JOptionPane.showMessageDialog(this, "Postaja uspešno dodana in zabeležena v logu!");
                dispose();
                parent.napolniTabelo(); // Osveži tabelo v glavnem oknu
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Napaka: " + ex.getMessage());
            }
        });

        add(new JLabel("")); // prazen prostor
        add(shraniButton);

        // Gumb za vrnitev na glavno okno
        JButton backButton = new JButton("Nazaj na seznam postaj");
        backButton.addActionListener(e -> {
            dispose(); // Zapri trenutno okno
            new GlavniWindowAdmin(); // Odpri glavno okno
        });

        add(backButton);

        setVisible(true);
    }
}
