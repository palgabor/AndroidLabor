package hu.bute.daai.amorg.xg5ort.phoneguard.data;

import java.util.ArrayList;
import java.util.List;

public class LocationData
{
	private static LocationData instance = null;
	
	int cellId = 0, lac = 0;
	double latitude = 0, longitude = 0;
	float speed = 0, accuracy = 0;
	List<Integer> neighboringCellIds;
	String address = "N.A.";
	
	
	private LocationData()
	{
		neighboringCellIds = new ArrayList<Integer>();
	}
	
	public static LocationData getInstance()
	{
		if(instance == null)
		{
			instance = new LocationData();
		}
		
		return instance;
	}
	
	public void add(int cellId)
	{
		neighboringCellIds.add(cellId);
	}

	public int getCellId() {
		return cellId;
	}

	public int getLac() {
		return lac;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getSpeed() {
		return speed;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public List<Integer> getNeighboringCellIds() {
		return neighboringCellIds;
	}
	
	public String getAddress() {
		return address;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
}
