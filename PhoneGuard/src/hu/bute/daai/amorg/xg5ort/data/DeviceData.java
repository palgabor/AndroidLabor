package hu.bute.daai.amorg.xg5ort.data;

public class DeviceData
{
	private String imei = "N.A.";
	private String imsi = "N.A.";
	private String operatorName = "N.A.";
	
	private static DeviceData instance = null;
	
	private DeviceData()
	{
		
	}
	
	public static DeviceData getInstance()
	{
		if(instance == null)
		{
			instance = new DeviceData();
		}
		
		return instance;
	}

	public String getImei() {
		return imei;
	}

	public String getImsi() {
		return imsi;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
}
