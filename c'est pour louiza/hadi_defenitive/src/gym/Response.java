/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gym;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author E14
 */
public class Response implements Serializable {
    private boolean success;
    private String message;
    private Map<String, Object> data;

    // Constructeur par défaut
    public Response() {
        this.data = new HashMap<>();
    }

    // Getters et Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    // Méthode utilitaire pour créer une réponse d'erreur
    public static Response error(String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // Méthode utilitaire pour créer une réponse de succès
    public static Response success(String message) {
        Response response = new Response();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }
}