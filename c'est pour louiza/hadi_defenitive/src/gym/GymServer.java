/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gym;

/**
 *
 * @author E14
 */

import gym.Request;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class GymServer {
    private static final int PORT = 12345;
    private static final int MAX_THREADS = 10;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bd_defenitive?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté: " + clientSocket.getInetAddress());
                
                // Créer un nouveau gestionnaire de client dans un thread séparé
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Erreur du serveur: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

   public static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                
                while (true) {
                    // Recevoir la requête du client
                    Request request = (Request) in.readObject();
                  
                    // Traiter la requête
                    Response response = processRequest(request);
                    
                    // Envoyer la réponse au client
                    out.writeObject(response);
                    out.flush();
                }
            } catch (EOFException e) {
                System.out.println("Client déconnecté: " + clientSocket.getInetAddress());
            } catch (IOException | ClassNotFoundException | SQLException e) {
                System.err.println("Erreur avec le client " + clientSocket.getInetAddress() + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Erreur lors de la fermeture du socket: " + e.getMessage());
                }
            }
        }

        private Response processRequest(Request request) throws SQLException {
    Response response = new Response();
    Connection conn = null;
        
    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        switch (request.getType()) {
            case "LOAD_CLIENT_DATA":
                return loadClientData(conn, request.getClientEmail());
            case "LOAD_ABONNEMENT_DATA":
                return loadAbonnementData(conn, request.getClientId());
            case "INSCRIRE_ABONNEMENT":
                return inscrireAbonnement(conn, request);
            case "UPDATE_CLIENT":
                return updateClient(conn, request);
            case "LOAD_PAYMENT_HISTORY":
                return loadPaymentHistory(conn, request.getClientId());
            case "HAS_ACTIVE_SUBSCRIPTION":
                return checkActiveSubscription(conn, request.getClientId());
            case "AUTHENTICATE_CLIENT":
                return authenticateClient(conn, 
                    (String)request.getData().get("email"),
                    (String)request.getData().get("password"));
            case "REGISTER_CLIENT":
                return registerClient(
                conn,
                (String)request.getData().get("nom"),
                (String)request.getData().get("prenom"),
                (String)request.getData().get("adresse"),
                (String)request.getData().get("dateNaissance"),
                (String)request.getData().get("tel"),
                (String)request.getData().get("email"),
                (String)request.getData().get("motDePasse"),
                (String)request.getData().get("sexe"));
            case "LOAD_ADMIN_DASHBOARD":
                return loadAdminDashboard(conn);
            case "LOAD_SUBSCRIPTION_SUMMARY":
                return loadSubscriptionSummary(conn);
            case "LOAD_EQUIPEMENTS":
                return loadEquipements(conn);
            case "LOAD_PAIEMENTS":
                return loadPaiements(conn);
            case "LOAD_PERSONNEL":
                return loadPersonnel(conn);
            case "LOAD_CLIENTS_ADMIN":
                return loadClientsAdmin(conn);
            case "UPDATE_PERSONNEL":
                return updatePersonnel(
                    conn,
                    (int)request.getData().get("id"),
                    (String)request.getData().get("field"),
                    request.getData().get("value")
                );
            case "UPDATE_CLIENT_ADMIN":
                return updateClientAdmin(
                    conn,
                    (int)request.getData().get("id"),
                    (String)request.getData().get("field"),
                    request.getData().get("value")
                );
            case "UPDATE_COACH":
            return updateCoach(
                conn,
                (int)request.getData().get("coachId"),
                (String)request.getData().get("field"),
                request.getData().get("value")
            );
            default:
                response.setSuccess(false);
                response.setMessage("Type de requête inconnu");
                return response;
        }
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erreur fermeture connexion DB: " + e.getMessage());
            }
        }
    }
}

        //////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////
        private Response loadClientData(Connection conn, String clientEmail) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        String sql = "SELECT id, Nom, Prenom, Adresse, `date de naissance`, Email, `Mot de passe`, sexe, solde " +
                     "FROM client WHERE Email = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, clientEmail);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            Map<String, Object> data = new HashMap<>();
            data.put("clientId", rs.getInt("id"));
            data.put("nom", rs.getString("Nom"));
            data.put("prenom", rs.getString("Prenom"));
            data.put("adresse", rs.getString("Adresse"));
            data.put("dateNaissance", rs.getString("date de naissance"));
            data.put("email", rs.getString("Email"));
            data.put("motDePasse", rs.getString("Mot de passe"));
            data.put("sexe", rs.getString("sexe"));
            data.put("clientSolde", rs.getInt("solde"));

            response.setSuccess(true);
            response.setData(data);
        } else {
            response.setSuccess(false);
            response.setMessage("Client non trouvé");
        }
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }

    return response;
}

private Response loadAbonnementData(Connection conn, int clientId) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    try {
        String sql = "SELECT type, `Date de debut`, `Date du fin`, Prix, Status " +
                     "FROM abonnement WHERE client_id = ? AND Status = 'actif' " +
                     "ORDER BY `Date du fin` DESC LIMIT 1";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            Map<String, Object> data = new HashMap<>();
            String type = rs.getString("type");
            Date dateDebut = rs.getDate("Date de debut");
            Date dateFin = rs.getDate("Date du fin");
            int prix = rs.getInt("Prix");
            String status = rs.getString("Status");

            data.put("type", type);
            data.put("dateDebut", dateDebut != null ? dateDebut.toLocalDate().format(displayDateFormatter) : "");
            data.put("dateFin", dateFin != null ? dateFin.toLocalDate().format(displayDateFormatter) : "");
            data.put("prix", prix);
            data.put("status", status);

            // Stocker aussi les données brutes pour le renouvellement
            data.put("rawType", type);
            data.put("rawPrix", prix);
            data.put("rawDateDebut", dateDebut);
            data.put("rawDateFin", dateFin);

            response.setSuccess(true);
            response.setData(data);
        } else {
            response.setSuccess(true); // Succès mais pas d'abonnement actif
            response.setMessage("Aucun abonnement actif");
        }
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }

    return response;
}

private Response inscrireAbonnement(Connection conn, Request request) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmtUpdateOld = null;
    PreparedStatement pstmtInsertNew = null;
    PreparedStatement pstmtUpdateSolde = null;
    PreparedStatement pstmtInsertPayment = null;

    try {
        conn.setAutoCommit(false); // Début de la transaction

        int clientId = request.getClientId();
        String type = (String) request.getData().get("type");
        int prix = (int) request.getData().get("prix");
        int duree = (int) request.getData().get("duree");

        // 1. Désactiver les anciens abonnements
        String sqlUpdateOld = "UPDATE abonnement SET Status = 'pas actif' WHERE client_id = ? AND Status = 'actif'";
        pstmtUpdateOld = conn.prepareStatement(sqlUpdateOld);
        pstmtUpdateOld.setInt(1, clientId);
        pstmtUpdateOld.executeUpdate();

        // 2. Insérer le nouvel abonnement
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusDays(duree);
        String sqlInsertNew = "INSERT INTO abonnement (client_id, type, `Date de debut`, `Date du fin`, Prix, Status) " +
                             "VALUES (?, ?, ?, ?, ?, 'actif')";
        pstmtInsertNew = conn.prepareStatement(sqlInsertNew, Statement.RETURN_GENERATED_KEYS);
        pstmtInsertNew.setInt(1, clientId);
        pstmtInsertNew.setString(2, type);
        pstmtInsertNew.setDate(3, Date.valueOf(dateDebut));
        pstmtInsertNew.setDate(4, Date.valueOf(dateFin));
        pstmtInsertNew.setInt(5, prix);
        pstmtInsertNew.executeUpdate();

        // 3. Mettre à jour le solde du client
        // D'abord récupérer le solde actuel
        int currentSolde = getClientSolde(conn, clientId);
        int newSolde = currentSolde - prix;
        
        String sqlUpdateSolde = "UPDATE client SET solde = ? WHERE id = ?";
        pstmtUpdateSolde = conn.prepareStatement(sqlUpdateSolde);
        pstmtUpdateSolde.setInt(1, newSolde);
        pstmtUpdateSolde.setInt(2, clientId);
        pstmtUpdateSolde.executeUpdate();

        // 4. Enregistrer le paiement
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String sqlInsertPayment = "INSERT INTO paiment (Client, montant, date, heure) VALUES (?, ?, ?, ?)";
        pstmtInsertPayment = conn.prepareStatement(sqlInsertPayment);
        pstmtInsertPayment.setInt(1, clientId);
        pstmtInsertPayment.setInt(2, prix);
        pstmtInsertPayment.setDate(3, new Date(now.getTime()));
        pstmtInsertPayment.setTime(4, new Time(now.getTime()));
        pstmtInsertPayment.executeUpdate();

        conn.commit(); // Validation de la transaction

        // Préparer la réponse
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("newSolde", newSolde);
        responseData.put("typeAbonnement", type);
        responseData.put("dateFin", dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        response.setSuccess(true);
        response.setMessage("Abonnement enregistré avec succès");
        response.setData(responseData);

    } catch (SQLException e) {
        try {
            conn.rollback();
            response.setSuccess(false);
            response.setMessage("Erreur lors de l'inscription: " + e.getMessage());
        } catch (SQLException ex) {
            response.setSuccess(false);
            response.setMessage("Erreur lors du rollback: " + ex.getMessage());
        }
    } finally {
        conn.setAutoCommit(true);
        if (pstmtUpdateOld != null) pstmtUpdateOld.close();
        if (pstmtInsertNew != null) pstmtInsertNew.close();
        if (pstmtUpdateSolde != null) pstmtUpdateSolde.close();
        if (pstmtInsertPayment != null) pstmtInsertPayment.close();
    }

    return response;
}

private int getClientSolde(Connection conn, int clientId) throws SQLException {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT solde FROM client WHERE id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getInt("solde");
        }
        return 0;
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }
}

private Response updateClient(Connection conn, Request request) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    
    try {
        Map<String, Object> data = request.getData();
        String sql = "UPDATE client SET Nom = ?, Prenom = ?, Adresse = ?, `Date De Naissance` = ?, " +
                     "Email = ?, `Mot de passe` = ?, Sexe = ? WHERE id = ?";
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, (String) data.get("nom"));
        pstmt.setString(2, (String) data.get("prenom"));
        pstmt.setString(3, (String) data.get("adresse"));
        pstmt.setString(4, (String) data.get("dateNaissance"));
        pstmt.setString(5, (String) data.get("email"));
        pstmt.setString(6, (String) data.get("motDePasse"));
        pstmt.setString(7, (String) data.get("sexe"));
        pstmt.setInt(8, request.getClientId());
        
        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected > 0) {
            response.setSuccess(true);
            response.setMessage("Informations client mises à jour avec succès");
        } else {
            response.setSuccess(false);
            response.setMessage("Aucune ligne mise à jour - client non trouvé");
        }
    } finally {
        if (pstmt != null) pstmt.close();
    }
    
    return response;
}

private Response loadPaymentHistory(Connection conn, int clientId) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<Map<String, Object>> payments = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    try {
        String sql = "SELECT id, Client, montant, date, heure FROM paiment WHERE Client = ? ORDER BY date DESC, heure DESC";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            Map<String, Object> payment = new HashMap<>();
            payment.put("id", rs.getInt("id"));
            payment.put("clientId", rs.getInt("Client")); // Changé de "Client" à "clientId"
            payment.put("montant", rs.getInt("montant"));
            
            Date paymentDate = rs.getDate("date");
            Time paymentTime = rs.getTime("heure");
            
            payment.put("date", paymentDate != null ? paymentDate.toLocalDate().format(dateFormatter) : "N/A");
            payment.put("heure", paymentTime != null ? paymentTime.toLocalTime().format(timeFormatter) : "N/A");
            
            payments.add(payment);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("payments", payments);
        
        response.setSuccess(true);
        response.setData(responseData);
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }

    return response;
}

private Response checkActiveSubscription(Connection conn, int clientId) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT COUNT(*) as count FROM abonnement " +
                     "WHERE client_id = ? AND Status = 'actif' AND `Date du fin` >= CURDATE()";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, clientId);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            boolean hasActive = rs.getInt("count") > 0;
            Map<String, Object> data = new HashMap<>();
            data.put("hasActiveSubscription", hasActive);
            
            response.setSuccess(true);
            response.setData(data);
        } else {
            response.setSuccess(false);
            response.setMessage("Erreur lors de la vérification de l'abonnement");
        }
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }
    
    return response;
}

private Response authenticateClient(Connection conn, String email, String password) throws SQLException {
    Response response = new Response();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT id FROM client WHERE Email = ? AND `Mot de passe` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, email);
        pstmt.setString(2, password); // À remplacer par une vérification de hash en production
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            response.setSuccess(true);
            response.setMessage("Authentification réussie");
        } else {
            response.setSuccess(false);
            response.setMessage("Email ou mot de passe incorrect");
        }
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
    }
    
    return response;
}
//////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

    private Response registerClient(Connection conn, String nom, String prenom, String adresse, 
                                  String dateNaissance, String tel, String email, 
                                  String motDePasse, String sexe) throws SQLException {
        Response response = new Response();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Vérifier si l'email existe déjà
            String checkSql = "SELECT id FROM client WHERE Email = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                response.setSuccess(false);
                response.setMessage("Cet email est déjà utilisé");
                return response;
            }

            // Insérer le nouveau client
            String insertSql = "INSERT INTO client (Nom, Prenom, Adresse, `date de naissance`, `N tel`, Email, `Mot de passe`, sexe) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, adresse);
            pstmt.setString(4, dateNaissance);
            pstmt.setString(5, tel);
            pstmt.setString(6, email);
            pstmt.setString(7, motDePasse); // À hasher en production
            pstmt.setString(8, sexe);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                response.setSuccess(true);
                response.setMessage("Client enregistré avec succès");
            } else {
                response.setSuccess(false);
                response.setMessage("Échec de l'enregistrement");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }

        return response;
    }
        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response loadAdminDashboard(Connection conn) throws SQLException {
    Response response = new Response();
    Map<String, Object> data = new HashMap<>();
    
    try (Statement stmt = conn.createStatement()) {
        // Nombre de clients
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM client");
        data.put("totalClients", rs.next() ? rs.getInt(1) : 0);
        
        // Nombre de coachs actifs
        rs = stmt.executeQuery("SELECT COUNT(*) FROM coach WHERE Etat = 'Actif'");
        data.put("totalCoaches", rs.next() ? rs.getInt(1) : 0);
        
        // Nombre d'équipements
        rs = stmt.executeQuery("SELECT COUNT(*) FROM euipement");
        data.put("totalEquipements", rs.next() ? rs.getInt(1) : 0);
        
        // Bénéfices totaux
        rs = stmt.executeQuery("SELECT SUM(montant) FROM paiment");
        data.put("totalBenefices", rs.next() ? rs.getInt(1) : 0);
        
        response.setSuccess(true);
        response.setData(data);
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}
        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response loadSubscriptionSummary(Connection conn) throws SQLException {
    Response response = new Response();
    List<Map<String, Object>> subscriptions = new ArrayList<>();
    
    String sql = "SELECT type, Prix, COUNT(*) as nombre FROM abonnement " +
                 "WHERE Status = 'actif' AND `Date du fin` >= CURDATE() " +
                 "GROUP BY type, Prix";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Map<String, Object> sub = new HashMap<>();
            sub.put("type", rs.getString("type"));
            sub.put("prix", rs.getInt("Prix"));
            sub.put("nombreClients", rs.getInt("nombre"));
            subscriptions.add(sub);
        }
        
        response.setSuccess(true);
        response.setData(Map.of("subscriptions", subscriptions));
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}   /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response updatePersonnel(Connection conn, int id, String field, Object value) throws SQLException {
    Response response = new Response();
    
    // Sécurité: vérifier que le champ est autorisé
    if (!List.of("nom", "prenom", "adresse", "n tel", "etat").contains(field.toLowerCase())) {
        response.setSuccess(false);
        response.setMessage("Champ non autorisé");
        return response;
    }
    
    String sql = "UPDATE coach SET " + field + " = ? WHERE id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setObject(1, value);
        pstmt.setInt(2, id);
        
        int rows = pstmt.executeUpdate();
        response.setSuccess(rows > 0);
        response.setMessage(rows > 0 ? "Mise à jour réussie" : "Aucune ligne mise à jour");
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}/////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response updateClientAdmin(Connection conn, int clientId, String field, Object value) throws SQLException {
    Response response = new Response();
    
    // Validation des champs autorisés
    List<String> allowedFields = Arrays.asList("nom", "prenom", "adresse", "n tel", "email", "sexe", "solde");
    if (!allowedFields.contains(field.toLowerCase())) {
        response.setSuccess(false);
        response.setMessage("Champ non autorisé");
        return response;
    }

    String sql = "UPDATE client SET " + field + " = ? WHERE id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setObject(1, value);
        pstmt.setInt(2, clientId);
        
        int rows = pstmt.executeUpdate();
        response.setSuccess(rows > 0);
        response.setMessage(rows > 0 ? "Mise à jour réussie" : "Aucun client trouvé");
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}/////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response loadEquipements(Connection conn) throws SQLException {
    Response response = new Response();
    List<Map<String, Object>> equipements = new ArrayList<>();
    
    String sql = "SELECT Nom, Type, Marque, Status FROM euipement";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Map<String, Object> eq = new HashMap<>();
            eq.put("nom", rs.getString("Nom"));
            eq.put("type", rs.getString("Type"));
            eq.put("marque", rs.getString("Marque"));
            eq.put("status", rs.getString("Status"));
            equipements.add(eq);
        }
        
        response.setSuccess(true);
        response.setData(Map.of("equipements", equipements));
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}
        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        

private Response loadPaiements(Connection conn) throws SQLException {
    Response response = new Response();
    List<Map<String, Object>> paiements = new ArrayList<>();
    
    String sql = "SELECT p.id, c.Nom, c.Prenom, p.montant, p.date, p.heure " +
                 "FROM paiment p JOIN client c ON p.Client = c.id " +
                 "ORDER BY p.date DESC";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", rs.getInt("id"));
            p.put("nomClient", rs.getString("Nom"));
            p.put("prenomClient", rs.getString("Prenom"));
            p.put("montant", rs.getInt("montant"));
            p.put("date", rs.getDate("date").toString());
            p.put("heure", rs.getTime("heure").toString());
            paiements.add(p);
        }
        
        response.setSuccess(true);
        response.setData(Map.of("paiements", paiements));
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}
/////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response loadPersonnel(Connection conn) throws SQLException {
    Response response = new Response();
    List<Map<String, Object>> personnelList = new ArrayList<>();
    
    String sql = "SELECT id, Nom, Prenom, Adresse, `N tel`, Etat FROM coach ORDER BY id";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Map<String, Object> coach = new HashMap<>();
            coach.put("id", rs.getInt("id"));
            coach.put("nom", rs.getString("Nom"));
            coach.put("prenom", rs.getString("Prenom"));
            coach.put("adresse", rs.getString("Adresse"));
            coach.put("tel", rs.getString("N tel"));
            coach.put("etat", rs.getString("Etat"));
            personnelList.add(coach);
        }
        
        response.setSuccess(true);
        response.setData(Map.of("personnel", personnelList));
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur lors du chargement du personnel: " + e.getMessage());
    }
    
    return response;
}
        /////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        private Response loadClientsAdmin(Connection conn) throws SQLException {
    Response response = new Response();
    List<Map<String, Object>> clients = new ArrayList<>();
    
    String sql = "SELECT id, Nom, Prenom, Adresse, `N tel`, Email, sexe, solde FROM client ORDER BY id";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            Map<String, Object> client = new HashMap<>();
            client.put("id", rs.getInt("id"));
            client.put("nom", rs.getString("Nom"));
            client.put("prenom", rs.getString("Prenom"));
            client.put("adresse", rs.getString("Adresse"));
            client.put("tel", rs.getString("N tel"));
            client.put("email", rs.getString("Email"));
            client.put("sexe", rs.getString("sexe"));
            client.put("solde", rs.getInt("solde"));
            clients.add(client);
        }
        
        response.setSuccess(true);
        response.setData(Map.of("clients", clients));
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur lors du chargement des clients: " + e.getMessage());
    }
    
    return response;
}
        private Response updateCoach(Connection conn, int coachId, String field, Object value) throws SQLException {
    Response response = new Response();
    
    // Validation des champs autorisés
    List<String> allowedFields = Arrays.asList("nom", "prenom", "adresse", "n tel", "etat");
    if (!allowedFields.contains(field.toLowerCase())) {
        response.setSuccess(false);
        response.setMessage("Champ non autorisé");
        return response;
    }

    String sql = "UPDATE coach SET " + field + " = ? WHERE id = ?";
    
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setObject(1, value);
        pstmt.setInt(2, coachId);
        
        int rows = pstmt.executeUpdate();
        response.setSuccess(rows > 0);
        response.setMessage(rows > 0 ? "Mise à jour réussie" : "Aucun coach trouvé");
    } catch (SQLException e) {
        response.setSuccess(false);
        response.setMessage("Erreur DB: " + e.getMessage());
    }
    
    return response;
}
        
        
        // Implémentation des méthodes de traitement des requêtes (loadClientData, loadAbonnementData, etc.)
        // Ces méthodes doivent extraire les données de la base et les mettre dans l'objet Response
    }
}