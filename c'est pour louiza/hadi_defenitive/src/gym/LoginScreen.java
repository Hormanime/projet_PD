package gym;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen extends javax.swing.JFrame {

    private String role; // "Client" ou "Administrateur"

    // Composants
    private JLabel titleLabel, emailLabel, passwordLabel;
    private JTextField input_username;
    private JPasswordField input_password;
    private JButton btn_login, btn_back, btn_register;
    private JLabel forgotPassword;

    public LoginScreen(String role) {
        this.role = role;
        initComponents();
        setTitle("Connexion " + role);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        titleLabel = new JLabel("Connexion " + role);
        emailLabel = new JLabel("Email");
        passwordLabel = new JLabel("Mot de passe");
        input_username = new JTextField();
        input_password = new JPasswordField();
        btn_login = new JButton("Se connecter");
        btn_back = new JButton("Retour");
        btn_register = new JButton("S'inscrire");
        forgotPassword = new JLabel("<HTML><U>Mot de passe oublié ?</U></HTML>");
        forgotPassword.setForeground(java.awt.Color.BLUE);
        forgotPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLayout(null);

        // Positionnement
        titleLabel.setBounds(100, 10, 250, 30);
        emailLabel.setBounds(50, 60, 100, 20);
        input_username.setBounds(150, 60, 180, 25);
        passwordLabel.setBounds(50, 100, 100, 20);
        input_password.setBounds(150, 100, 180, 25);
        forgotPassword.setBounds(150, 130, 180, 20);
        btn_login.setBounds(150, 160, 180, 30);
        btn_back.setBounds(50, 210, 100, 25);

        // Si Client, afficher bouton inscription
        if (role.equalsIgnoreCase("Client")) {
            btn_register.setBounds(230, 210, 100, 25);
            add(btn_register);
        }

        // Ajout des composants
        add(titleLabel);
        add(emailLabel);
        add(input_username);
        add(passwordLabel);
        add(input_password);
        add(forgotPassword);
        add(btn_login);
        add(btn_back);

        // Événements
        btn_back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Choixrole().setVisible(true); // retourne à l'écran de rôle
            }
        });

        btn_register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                Inscription in = new Inscription();
                in.setVisible(true);
                in.setLocationRelativeTo(null);
                // new RegisterScreen().setVisible(true); // si tu veux ajouter une vraie classe
                // dispose();
            }
        });

        forgotPassword.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Fonctionnalité de récupération de mot de passe à implémenter.");
            }
        });
//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
         btn_login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = input_username.getText();
                String password = String.valueOf(input_password.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.");
                    return;
                }

                if (role.equalsIgnoreCase("Administrateur")) {
                    // Authentification admin (hardcodée ou via serveur)
                    //authenticateAdmin(username, password);
                } else if (role.equalsIgnoreCase("Client")) {
                    // Authentification client via serveur
                    authenticateClient(username, password);
                }
            }
        });
  ////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////
    }

    /*private void authenticateAdmin(String username, String password) {
        // Version simplifiée (à remplacer par un appel au serveur si nécessaire)
        if (username.equals("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(null, "Connexion administrateur réussie !");
            dispose();
            new AcceuilAdmini().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Identifiants administrateur incorrects !");
        }
    }*/
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    private void authenticateClient(String username, String password) {
        Request request = new Request();
        request.setType("AUTHENTICATE_CLIENT");
        request.addData("email", username);
        request.addData("password", password);
        
        Response response = NetworkManager.sendRequest(request);
        
        if (response != null && response.isSuccess()) {
            JOptionPane.showMessageDialog(null, "Connexion client réussie !");
            dispose();
            try {
                new AcceuilClient(username).setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(LoginScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String error = (response != null) ? response.getMessage() : "Erreur de connexion";
            JOptionPane.showMessageDialog(null, "Échec de l'authentification: " + error);
        }
    }
    
     /////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new LoginScreen("Client").setVisible(true);
        });
    }
}