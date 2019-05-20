package rocks.voss.beatthemeat.sources.grilleye;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.utils.ByteUtils;

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
            webSocket = new WebSocketFactory().createSocket(url, timeout * 1000);
            webSocket.getSocket().setSoTimeout(timeout * 1000);
            webSocket.addExtension("permessage-deflate");
            webSocket.addListener(listener);
            webSocket.connect();
            connectionChecker(timeout);
        }
    }

    public void sendTempRequest() {
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendBinary(UUIDS.TEMP.getUuidArray());
        }
    }

    public boolean isConnected() {
        if (webSocket == null) {
            return false;
        }
        if (webSocket.getSocket() == null) {
            return false;
        }
        return webSocket.getState() != WebSocketState.OPEN && webSocket.getSocket().isConnected() && !webSocket.getSocket().isClosed();
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.disconnect(WebSocketCloseCode.NORMAL, null, 0);
        }
    }

    public void setMinTemperatures(List<Thermometer> thermometers) {
        List<Byte> byteList = new ArrayList<>(16);
        for (Thermometer thermometer : thermometers) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byte[] data;
            if (thermometer.isRange) {
                data = byteBuffer.putShort((short) thermometer.temperatureMin).array();
            } else {
                data = byteBuffer.putShort((short) -50).array();
            }
            byteList.add(data[0]);
            byteList.add(data[1]);
        }
        sendData(UUIDS.MINTEMP, byteList);
    }

    public void setMaxTemperatures(List<Thermometer> thermometers) {
        List<Byte> byteList = new ArrayList<>(16);
        for (Thermometer thermometer : thermometers) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byte[] data = byteBuffer.putShort((short) thermometer.temperatureMax).array();
            byteList.add(data[0]);
            byteList.add(data[1]);
        }
        sendData(UUIDS.MAXTEMP, byteList);
    }

    public void setName(List<Thermometer> thermometers) {
        for (int i = 0; i < thermometers.size(); i++) {
            List<Byte> byteList = new ArrayList<>();
            Thermometer thermometer = thermometers.get(i);
            byteList.add(Byte.valueOf((byte) (i + 1)));

            String part1 = getSixCharString(thermometer.cut);
            String part2 = getSixCharString(thermometer.cooking);
            String text = part1 + part2;

            byteList.addAll(ByteUtils.toByteList(text.getBytes(Charset.forName("UTF-8"))));
            sendData(UUIDS.NAMES, byteList);
        }
    }

    private String getSixCharString(String input) {
        input = input + "      ";
        return input.toUpperCase().substring(0, 6);
    }

    private void sendData(UUIDS command, List<Byte> message) {
        int uuidLength = command.getUuidArray().length;
        int messageLength = message.size();

        byte[] data = new byte[uuidLength + messageLength];
        for (int i = 0; i < uuidLength; i++) {
            data[i] = command.getUuidArray()[i];
        }
        for (int i = 0; i < messageLength; i++) {
            data[uuidLength + i] = message.get(i).byteValue();
        }

        if (isConnected()) {
            webSocket.sendBinary(data);
        }
    }

    private void connectionChecker(int timeout) {
        ConnectionChecker connectionChecker = new ConnectionChecker(webSocket, timeout);
        connectionChecker.start();
    }
}
