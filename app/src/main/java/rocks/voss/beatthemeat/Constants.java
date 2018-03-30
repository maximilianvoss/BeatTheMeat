package rocks.voss.beatthemeat;

/**
 * Created by voss on 24.03.18.
 */

public class Constants {
    public final static int SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID = 1;
    public final static int SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID = 2;

    public final static String NOTIFICIATION_CHANNEL_ID = "Beat the Meat Channel Id";
    public static final String NOTIFICATION_TITLE = "Beat The Meat Alarm";
    public static final String NOTIFICATION_TEXT = "Check the meat!";
    public static final String NOTIFICATION_DISMISS = "Dismiss";
    public final static int NOTIFICIATION_TEMPERATURE_ID = 1;

    public static final String SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL = "temperatureWebserviceURL";
    public static final String SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL = "temperatureWebserviceInterval";
    public static final String SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL = "thermometerSettingsWebserviceURL";
    public static final String SETTING_GENERAL_ALARM = "alarm";

    public static final String SETTING_TEMPERATURE_CURRENT = "temperatureCurrent";
    public static final String SETTING_TEMPERATURE_IS_RANGE = "isRange";
    public static final String SETTING_TEMPERATURE_CATEGORY = "temperatureCategory";
    public static final String SETTING_TEMPERATURE_STYLE = "temperatureStyle";
    public static final String SETTING_TEMPERATURE_MIN = "temperatureMin";
    public static final String SETTING_TEMPERATURE_MAX = "temperatureMax";

    public static final String THERMOMETER_CANVAS_ID = "rocks.voss.beatthemeat.widgets.ThermometerCanvas.id";
    public static final String JSON_TEMPERATURES_OBJECT = "temperatures";
}
