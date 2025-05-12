/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gym;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author E14
 */
public class NetworkManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    
    public static Response sendRequest(Request request) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Response errorResponse = new Response();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Erreur de communication avec le serveur");
            return errorResponse;
        }
    }
}
