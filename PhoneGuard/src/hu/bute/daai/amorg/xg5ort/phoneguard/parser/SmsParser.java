package hu.bute.daai.amorg.xg5ort.phoneguard.parser;


import hu.bute.daai.amorg.xg5ort.phoneguard.data.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SmsParser
{
	private static SmsParser instance = null;
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
		password = preferences.getString(Constants.SP_PASSWORD, "");
	}
	
	public int parse(String body)
	{
		if(body.contains(Constants.SMS_STARTER + Constants.SEPARATOR))
		{
			if(body.contains(password + Constants.SEPARATOR))
			{
				return Constants.PHONE_GUARD_SMS;
			}
			else
			{
				return Constants.BAD_PASSWORD;
			}
		}
		else
		{
			return Constants.NON_PHONE_GUARD_SMS;
		}
	}
	
	public int findAction(String body)
	{
		if(body.contains(Constants.ACTION_STOP_EMERGENCY_SMS))
		{
			return Constants.ACTION_STOP_EMERGENCY;
		}
		else if(body.contains(Constants.ACTION_EMERGENCY_SMS))
		{
			return getTimeValue(body);
		}
		else if(body.contains(Constants.ACTION_SETTINGS_SMS))
		{
			return Constants.ACTION_SETTINGS;
		}
		else
		{
			return Constants.ACTION_UNKNOWN;
		}
	}
	
	private int getTimeValue(String msg)
	{
		Pattern p = Pattern.compile("([1-9]|[1-9]\\d+)(" + Constants.HOUR + "|" + Constants.MINUTE + ")");
		Matcher m = p.matcher(msg);
		
		if(msg.equals(Constants.SMS_STARTER + Constants.SEPARATOR + password + 
						Constants.SEPARATOR + Constants.ACTION_EMERGENCY_SMS))
		{
			return Constants.ACTION_EMERGENCY_WITHOUT_TIME;
		}else if(m.find() == false)
		{
			return Constants.ACTION_UNKNOWN;
		}
		else
		{
			String time = m.group();
			int timeNum = 0;
			if(time.contains(Constants.HOUR))
			{
				timeNum = Integer.parseInt(time.substring(0, time.length()-Constants.HOUR.length()));
				return Constants.ACTION_EMERGENCY_TIME_BASE + timeNum*60;
			}
			else if(time.contains(Constants.MINUTE))
			{
				timeNum = Integer.parseInt(time.substring(0, time.length()-Constants.MINUTE.length()));
				return Constants.ACTION_EMERGENCY_TIME_BASE + timeNum;
			}
			return 0;
		}
	}
}
