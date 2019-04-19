package rocks.voss.beatthemeat.sources.grilleye;

import java.util.Arrays;

public enum UUIDS {
    ALARM(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 6)),
    COOL_DOWN(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 13, (byte) 5)),
    FIRMWARE(UUIDS.constructUuidArray((byte) 2, (byte) 10, (byte) 2, (byte) 6)),
    LOG(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 13, (byte) 3)),
    LOG_LENGTH(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 13, (byte) 2)),
    MAXTEMP(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 2)),
    MINTEMP(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 3)),
    NAMES(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 1)),
    SERIAL(UUIDS.constructUuidArray((byte) 2, (byte) 10, (byte) 2, (byte) 5)),
    SETTINGS(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 13, (byte) 6)),
    SIGNAL(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 14, (byte) 3)),
    TEMP(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 4)),
    TIMER(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 15, (byte) 5)),
    TIME_STAMP(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 13, (byte) 1)),
    UPDATE(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 14, (byte) 5)),
    WIFI_NAME(UUIDS.constructUuidArray((byte) 15, (byte) 15, (byte) 14, (byte) 4));

    private static UUIDS[] vals = values();
    private final byte[] uuidArray;

    UUIDS(byte[] byteArray) {
        this.uuidArray = byteArray;
    }

    public static final UUIDS getUUIDS(byte[] bytes) {
        UUIDS test = UUIDS.ALARM;
        do {
            if (Arrays.equals(test.getUuidArray(), bytes)) {
                return test;
            }
            test = test.next();
        } while (test != UUIDS.ALARM);
        return null;
    }

    private static final byte[] constructUuidArray(byte byte1, byte byte2, byte byte3, byte byte4) {
        byte[] array = new byte[4];
        array[0] = byte1;
        array[1] = byte2;
        array[2] = byte3;
        array[3] = byte4;

        return array;
    }

    public UUIDS next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public final byte[] getUuidArray() {
        return this.uuidArray;
    }
}
