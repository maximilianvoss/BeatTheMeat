package rocks.voss.beatthemeat.grilleye;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GrillEyePro {
    private static Map<String, GrillEyePro> connections = new HashMap<>();
    private WebSocket webSocket = null;
    private String url = null;

    public GrillEyePro(String url) {
        this.url = url;
    }

    public static synchronized GrillEyePro getConnection(String url) {
        GrillEyePro grillEyePro = connections.get(url);
        if (grillEyePro == null) {
            grillEyePro = new GrillEyePro(url);
            connections.put(url, grillEyePro);
        }
        return grillEyePro;
    }

    public static void stop() {
        for (String key : connections.keySet()) {
            connections.get(key).disconnect();
            connections.remove(key);
        }
    }

    public synchronized void connect(int timeout, WebSocketListener listener) throws WebSocketException, IOException {
        if (!isConnected()) {
            webSocket = new WebSocketFactory().createSocket(url, timeout);
            webSocket.addExtension("permessage-deflate");
            webSocket.addListener(listener);
            webSocket.connect();
        }
    }

    public boolean isConnected() {
        if (webSocket == null) {
            return false;
        }
        if (webSocket.getSocket() == null) {
            return false;
        }
        return webSocket.getSocket().isConnected();
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.disconnect();
        }
    }
}
