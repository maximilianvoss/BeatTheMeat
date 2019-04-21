package rocks.voss.beatthemeat.threads;

import android.content.Context;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocks.voss.beatthemeat.sources.grilleye.GrillEyePro;
import rocks.voss.beatthemeat.sources.grilleye.UUIDS;
import rocks.voss.beatthemeat.utils.ByteUtils;

public class GrillEyeProThread extends Thread {
    private final List<URL> urls = new ArrayList<>();
    private final Context context;
    private final GrilleyeProDownloadThreadCallback grillEyeProDownloadCallback;
    private int timeout;
    private Map<URL, GrillEyePro> webservices = new HashMap<>();

    public GrillEyeProThread(Context context, int timeout, GrilleyeProDownloadThreadCallback grillEyeProDownloadCallback) {
        this.context = context;
        this.timeout = timeout;
        this.grillEyeProDownloadCallback = grillEyeProDownloadCallback;
    }

    public void addUrl(String url) throws MalformedURLException {
        if (url != null && !url.equals("")) {
            addUrl(new URL(url));
        }
    }

    public void addUrl(URL url) {
        urls.add(url);
    }

    public void addUrls(URL... urls) {
        for (URL url : urls) {
            addUrl(url);
        }
    }

    public void addUrls(String... urls) throws MalformedURLException {
        for (String url : urls) {
            addUrl(url);
        }
    }

    @Override
    public void run() {
        for (URL url : urls) {
            GrillEyePro grillEyePro = GrillEyePro.getConnection(url.toString());
            try {
                grillEyePro.connect(timeout, new WebSocketAdapter() {
                    public void onBinaryMessage(WebSocket webSocket, byte[] message) {
                        int temperatures[] = new int[8];
                        byte[] command = Arrays.copyOfRange(message, 0, 4);
                        byte[] data = Arrays.copyOfRange(message, 4, message.length);
                        UUIDS commandId = UUIDS.getUUIDS(command);
                        if (commandId == UUIDS.TEMP) {
                            for (int i = 0; i < 8; i++) {
                                temperatures[i] = ByteUtils.convertTwoBytesToInt(data[i * 2], data[i * 2 + 1]);
                            }
                        }
                        grillEyeProDownloadCallback.onDownloadComplete(temperatures);
                    }
                });
                grillEyePro.sendTempRequest();
            } catch (WebSocketException e) {
                Log.e(this.getClass().toString(), "WebSocketException", e);
                grillEyeProDownloadCallback.onConnectionFailure(context);
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "IOException", e);
                grillEyeProDownloadCallback.onConnectionFailure(context);
            }
        }
    }

    public interface GrilleyeProDownloadThreadCallback {
        void onDownloadComplete(int[] temperatures);

        void onConnectionFailure(Context context);
    }
}
