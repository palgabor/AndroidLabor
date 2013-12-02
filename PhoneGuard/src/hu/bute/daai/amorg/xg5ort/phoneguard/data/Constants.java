package hu.bute.daai.amorg.xg5ort.phoneguard.data;

public class Constants
{
	//Constants for Parse.com
	public static final String PARSE_APPLICATION_ID = "0baLdmQJInwexbzZaqZDf2IVFVbvtI8XY2er4TZk";
	public static final String PARSE_CLIENT_ID = "rVeek9zXKKpKrU6DjztnbH3Xg43xKDssx6tdQwrH";
	
	
	//Shared preferences constants
	public static final String SP_PREFERRED_COMMUNICATION = "PREFERRED_COMMUNICATION_MODE";
	public static final String SP_PHONE_NUMBER = "PHONE_NUMBER";
	public static final String SP_PASSWORD = "PASSWORD";
	public static final String SP_EMERGENCY_FREQUENCY = "EMERGENCY_FREQUENCY";
	
	public static final String SP_IS_EMERGENCY_STATE = "is_emergency_state";
	public static final String SP_IMEI = "imei";
	public static final String SP_IMSI = "imsi";
	public static final String SP_OPERATOR_NAME = "operator_name";
	
	public static final String SP_TIME_TICKS = "time_ticks";
	public static final String SP_IS_LOCATION_UPDATE_SERVICE_RUNNING = "is location update service running";
	
	//EmergencyHandlerService starter actions
	public static final String ACTION_EMERGENCY_SMS = "emer";
	public static final String ACTION_STOP_EMERGENCY_SMS = "stop emer";
	public static final String ACTION_SETTINGS_SMS = "set";
	public static final String ACTION_TIME_TICK = "time tick";

	
	//DatabaseService starter actions
	public static final String ACTION_SMS_ARRIVED = "SMS ARRIVED";
	public static final String ACTION_LOCATION_CHANGED = "LOCATION CHANGED";
	public static final String ACTION_DEVICE_DATA_CHANGED = "DEVICE DATA CHANGED";
	
	
	//SmsParser constants
	public static final int NON_PHONE_GUARD_SMS = 1;
	public static final int PHONE_GUARD_SMS = 2;
	public static final int BAD_PASSWORD = 3;
	public static final int ACTION_UNKNOWN = 4;
	public static final int ACTION_EMERGENCY_WITHOUT_TIME = 5;
	public static final int ACTION_STOP_EMERGENCY = 6;
	public static final int ACTION_SETTINGS = 7;
	public static final int ACTION_EMERGENCY_TIME_BASE = 1000;
	
	public static final String BAD_PASSWORD_STR = "Bad password";
	public static final String ACTION_UNKNOWN_STR = "Action unknown";
	public static final String ACTION_EMERGENCY_WITHOUT_TIME_STR = "Emergency state started with default time";
	public static final String ACTION_STOP_EMERGENCY_STR = "Emergency state stopped";
	public static final String ACTION_SETTINGS_STR= "Settings";
	public static final String ACTION_EMERGENCY_TIME_BASE_STR = "Emergency sate started with given time";

	public static final String SEPARATOR = "#";
	public static final String SMS_STARTER = "[PG]";

	public static final String HOUR = "h";
	public static final String MINUTE = "m";
}
