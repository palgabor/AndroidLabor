package amorg.labo11.service;

import java.util.Date;
import java.text.SimpleDateFormat;
import android.location.Location;

// entitas osztaly a foldrengesek tarolasara
public class Quake {

	private Date date; // datum
	private String details; // reszletes informaciok
	private Location location; // helyszin
	private double magnitude; // erosseg
	private String link; // hivatkozas

	public Date getDate() {
		return date;
	}

	public String getDetails() {
		return details;
	}

	public Location getLocation() {
		return location;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public String getLink() {
		return link;
	}

	public Quake(Date _d, String _det, Location _loc, double _mag, String _link) {
		date = _d;
		details = _det;
		location = _loc;
		magnitude = _mag;
		link = _link;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
		String dateString = sdf.format(date);
		return dateString + ": " + magnitude + " " + details;
	}
}