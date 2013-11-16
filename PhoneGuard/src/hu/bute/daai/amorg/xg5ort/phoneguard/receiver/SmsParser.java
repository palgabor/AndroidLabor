package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	public static final int ACTION_EMERGENCY_HOUR_BASE = 1000;
	public static final int ACTION_EMERGENCY_MINUTE_BASE = -1000;
	
	private final String SEPARATOR = "#";
	private final String SMS_STARTER = "[PG]";
	private final String ACTION_EMERGENCY_STR = "emer";
	private final String ACTION_STOP_EMERGENCY_STR = "stop emer";
	private final String ACTION_SETTINGS_STR = "set";
	
	private final String HOUR = "h";
	private final String MINUTE = "m";
	
	private String PASSWORD = "jelszo"; 
	
	private SmsParser()
	{
		
	}
	
	public static SmsParser getInstance()
	{
		if(instance == null)
		{
			instance = new SmsParser();
		}
		
		return instance;
	}
	
	public void setPassword(String pwd)
	{
		PASSWORD = pwd;
	}
	
	public int parse(String body)
	{
		if(body.contains(SMS_STARTER + SEPARATOR))
		{
			if(body.contains(PASSWORD + SEPARATOR))
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
		if(body.contains(ACTION_STOP_EMERGENCY_STR))
		{
			return ACTION_STOP_EMERGENCY;
		}
		else if(body.contains(ACTION_EMERGENCY_STR))
		{
			return getTimeValue(body);
		}
		else if(body.contains(ACTION_SETTINGS_STR))
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
		Pattern p = Pattern.compile("([1-9]|[1-9]\\d+)(" + HOUR + "|" + MINUTE + ") +");
		Matcher m = p.matcher(msg);
		if(m.find() == false)
		{
			return ACTION_UNKNOWN;
		}
		
		String time = m.group();
		int timeNum = 0;
		if(time.contains(HOUR))
		{
			timeNum = Integer.parseInt(time.substring(0, time.length()-HOUR.length() -1));
			return ACTION_EMERGENCY_HOUR_BASE + timeNum;
		}
		else if(time.contains(MINUTE))
		{
			timeNum = Integer.parseInt(time.substring(0, time.length()-MINUTE.length() -1));
			return ACTION_EMERGENCY_MINUTE_BASE - timeNum;
		}
		else
		{
			return ACTION_EMERGENCY_WITHOUT_TIME;
		}
	}
}
