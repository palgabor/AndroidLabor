package hu.bute.daai.amorg.xg5ort.phoneguard.parser;


import hu.bute.daai.amorg.xg5ort.data.SharedPreferencesConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SmsParser
{
	private static SmsParser instance = null;
	
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

	private final String SEPARATOR = "#";
	private final String SMS_STARTER = "[PG]";
	public static final String ACTION_EMERGENCY_SMS = "emer";
	public static final String ACTION_STOP_EMERGENCY_SMS = "stop emer";
	public static final String ACTION_SETTINGS_SMS = "set";
	
	public static final String HOUR = "h";
	public static final String MINUTE = "m";
	
	private String password; 
	private static Context context;
	
	private SmsParser()
	{
		fetchPassword();
	}
	
	public static SmsParser getInstance(Context c)
	{
		context = c;
		if(instance == null)
		{
			instance = new SmsParser();
		}
		
		return instance;
	}
	
	public void fetchPassword()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		password = preferences.getString(SharedPreferencesConstants.PASSWORD, "");
	}
	
	public int parse(String body)
	{
		if(body.contains(SMS_STARTER + SEPARATOR))
		{
			if(body.contains(password + SEPARATOR))
			{
				return PHONE_GUARD_SMS;
			}
			else
			{
				return BAD_PASSWORD;
			}
		}
		else
		{
			return NON_PHONE_GUARD_SMS;
		}
	}
	
	public int findAction(String body)
	{
		if(body.contains(ACTION_STOP_EMERGENCY_SMS))
		{
			return ACTION_STOP_EMERGENCY;
		}
		else if(body.contains(ACTION_EMERGENCY_SMS))
		{
			return getTimeValue(body);
		}
		else if(body.contains(ACTION_SETTINGS_SMS))
		{
			return ACTION_SETTINGS;
		}
		else
		{
			return ACTION_UNKNOWN;
		}
	}
	
	private int getTimeValue(String msg)
	{
		Pattern p = Pattern.compile("([1-9]|[1-9]\\d+)(" + HOUR + "|" + MINUTE + ")");
		Matcher m = p.matcher(msg);
		
		if(msg.equals(SMS_STARTER + SEPARATOR + password + SEPARATOR + ACTION_EMERGENCY_SMS))
		{
			return ACTION_EMERGENCY_WITHOUT_TIME;
		}else if(m.find() == false)
		{
			return ACTION_UNKNOWN;
		}
		else
		{
			String time = m.group();
			int timeNum = 0;
			if(time.contains(HOUR))
			{
				timeNum = Integer.parseInt(time.substring(0, time.length()-HOUR.length()));
				return ACTION_EMERGENCY_TIME_BASE + timeNum*60;
			}
			else if(time.contains(MINUTE))
			{
				timeNum = Integer.parseInt(time.substring(0, time.length()-MINUTE.length()));
				return ACTION_EMERGENCY_TIME_BASE + timeNum;
			}
			return 0;
		}
	}
}
