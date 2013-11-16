package hu.bute.daai.amorg.xg5ort.phoneguard.receiver;


public class SmsParser
{
	private static SmsParser instance = null;
	
	public static final int NON_PHONE_GUARD_SMS = 1;
	public static final int PHONE_GUARD_SMS = 2;
	public static final int BAD_PASSWORD = 3;

	public static final int ACTION_UNKNOWN = 4;
	public static final int ACTION_EMERGENCY = 5;	
	public static final int ACTION_SETTINGS = 6;
	
	private final String SEPARATOR = "#";
	private final String SMS_STARTER = "[PG]";
	private final String ACTION_EMERGENCY_STR = "emer";
	private final String ACTION_SETTINGS_STR = "set";
	
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
		if(body.contains(ACTION_EMERGENCY_STR))
		{
			return ACTION_EMERGENCY;
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
}
