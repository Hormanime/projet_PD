/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gym;

/**
 *
 * @author E14
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request implements Serializable {
    private String type;
    private String clientEmail;
    private int clientId;
    private Map<String, Object> data;

    // Constructeur par défaut
    public Request() {
        this.data = new HashMap<>();
    }

    // Getters et Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Méthode utilitaire pour ajouter des données
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    // Méthode utilitaire pour récupérer une donnée
    public Object getData(String key) {
        return this.data.get(key);
    }
}
