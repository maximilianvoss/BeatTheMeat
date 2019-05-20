package rocks.voss.beatthemeat.sources.grilleye;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;

import org.threeten.bp.OffsetDateTime;

import rocks.voss.androidutils.utils.TimeUtil;

import static org.threeten.bp.temporal.ChronoUnit.SECONDS;

public class ConnectionChecker extends Thread {
    private WebSocket webSocket;
    private int timeout;
    private OffsetDateTime lastHit = null;

    public ConnectionChecker(WebSocket webSocket, int timeout) {
        this.timeout = timeout;
        this.webSocket = webSocket;

        lastHit = TimeUtil.getNow();
        webSocket.addListener(new WebSocketAdapter() {
            public void onBinaryMessage(WebSocket webSocket, byte[] message) {
                lastHit = TimeUtil.getNow();
            }
        });
    }

    public void run() {
        while (true) {
            try {
                OffsetDateTime now = TimeUtil.getNow();
                long secondsBetween = SECONDS.between(now, lastHit);
                if (secondsBetween < 0) {
                    secondsBetween *= -1;
                }
                Log.d(this.getClass().toString(), "Times between: " + secondsBetween);
                if (secondsBetween > timeout) {
                    webSocket.disconnect(WebSocketCloseCode.NORMAL, null, 0);
                    return;
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(this.getClass().toString(), "InterruptedException", e);
            }
        }
    }
}
