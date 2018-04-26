package rocks.voss.beatthemeat.enums;

import org.threeten.bp.OffsetDateTime;

import rocks.voss.beatthemeat.utils.TimeUtil;

public enum HistoryScaleEnum {
    min15,
    min30,
    hrs1,
    hrs3,
    hrs5,
    hrs10;

    private static HistoryScaleEnum[] vals = values();

    public HistoryScaleEnum next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    public HistoryScaleEnum prev() {
        int value = (this.ordinal() - 1) % vals.length;
        if (value < 0) {
            value = vals.length - 1;
        }
        return vals[value];
    }

    public static OffsetDateTime getTime(HistoryScaleEnum scaleEnum) {
        OffsetDateTime time = TimeUtil.getNow();
        switch (scaleEnum) {
            case min15:
                return time.minusMinutes(15);
            case min30:
                return time.minusMinutes(30);
            case hrs1:
                return time.minusHours(1);
            case hrs3:
                return time.minusHours(3);
            case hrs5:
                return time.minusHours(5);
            case hrs10:
                return time.minusHours(10);
        }
        return null;
    }

    public static boolean isCalcInSeconds(HistoryScaleEnum scaleEnum) {
        switch (scaleEnum) {
            case min15:
            case min30:
                return true;
            default:
                return false;
        }
    }

    public static String getScaleName(HistoryScaleEnum scaleEnum) {
        switch (scaleEnum) {
            case min15:
                return "15 Minutes";
            case min30:
                return "30 Minutes";
            case hrs1:
                return "1 Hour";
            case hrs3:
                return "3 Hours";
            case hrs5:
                return "5 Hours";
            case hrs10:
                return "10 Hours";
        }
        return null;
    }
}
