package rocks.voss.beatthemeat;

/**
 * Created by voss on 24.03.18.
 */

public class Constants {
    public final static int SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID = 1;
    public final static int SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID = 2;
    public final static int SERVICE_HISTORY_TEMPERATURE_SERVICE_ID = 3;

    public final static String NOTIFICATION_ALERT_TYPE = "rocks.voss.beatthemeat.services.NotificationSoundService.alarmType";
    public final static String NOTIFICATION_DISMISS_TYPE = "rocks.voss.beatthemeat.services.NotificationSoundService.dismissType";
    public final static String NOTIFICIATION_CHANNEL_ID = "Beat the Meat Channel Id";
    public static final String NOTIFICATION_DISMISS = "Dismiss";
    public static final String NOTIFICATION_SNOOZE = "Snooze";
    public final static int NOTIFICIATION_ID = 1;
    public static final String NOTIFICATION_TEMPERATURE_TITLE = "Beat The Meat Alarm";
    public static final String NOTIFICATION_TEMPERATURE_TEXT = "Check the meat!";
    public static final String NOTIFICATION_WEBSERVICE_TITLE = "Beat The Meat Connection Alarm";
    public static final String NOTIFICATION_WEBSERVICE_TEXT = "Check the Network connection";

    public static final String SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED = "temperatureWebserviceEnabled";
    public static final String SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL = "temperatureWebserviceURL";
    public static final String SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL = "temperatureWebserviceInterval";
    public static final String SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL = "thermometerSettingsWebserviceURL";
    public static final String SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL_DEFAULT = "https://maximilian.voss.rocks/bin/thermometer.json";
    public static final String SETTING_GENERAL_ALARM = "alarm";

    public static final String SETTING_TEMPERATURE_CATALOG = "temperatureCatalog";
    public static final String SETTING_TEMPERATURE_CATEGORY = "temperatureCategory";
    public static final String SETTING_TEMPERATURE_STYLE = "temperatureStyle";
    public static final String SETTING_TEMPERATURE_IS_RANGE = "isRange";
    public static final String SETTING_TEMPERATURE_MIN = "temperatureMin";
    public static final String SETTING_TEMPERATURE_MAX = "temperatureMax";
    public static final int FALLBACK_VALUE_TEMPERATURE_NOT_SET = -9999;

    public static final String THERMOMETER_CANVAS_ID = "rocks.voss.beatthemeat.widgets.ThermometerCanvas.id";

    public static final String JSON_TEMPERATURES_OBJECT = "temperatures";

    public static final String JSON_THERMOMETER_SETTINGS_CATALOGS = "catalogs";
    public static final String JSON_THERMOMETER_SETTINGS_CATEGORIES = "categories";
    public static final String JSON_THERMOMETER_SETTINGS_PROPERTY_NAME = "name";
    public static final String JSON_THERMOMETER_SETTINGS_STYLES = "styles";
    public static final String JSON_THERMOMETER_SETTINGS_PROPERTY_IS_RANGE = "temperatureIsRange";
    public static final String JSON_THERMOMETER_SETTINGS_PROPERTY_TEMP_MIN = "temperatureMin";
    public static final String JSON_THERMOMETER_SETTINGS_PROPERTY_TEMP_MAX = "temperatureMax";

    public static final String DATABASE_NAME = "temperatures";

    public static final int STANDARD_PADDING = 16;
}
