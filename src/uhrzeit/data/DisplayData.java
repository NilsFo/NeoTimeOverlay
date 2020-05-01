package uhrzeit.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Observable;
import java.util.TimeZone;

public class DisplayData extends Observable {
	
	public static final String DEFAULT_DATE = "Datum: ?";
	public static final String DEFAULT_LAUFZEIT = "Laufzeit: -";
	public static final String DEFAULT_PING = "Ping: Noch nicht getestet";
	public static final String DEFAULT_TIME = "Uhrzeit: --:--:--";
	private String datum, uhrzeit;
	private String ping;
	private long laufzeit;
	private String laufzeitText;
	private boolean neueMinute;
	
	private boolean connected = true;
	private int retries = 0;
	private int connectionTime = 0;
	
	private int uhrzeitStyle, datumStyle;
	
	public DisplayData() {
		datum = DEFAULT_DATE;
		uhrzeit = DEFAULT_TIME;
		ping = DEFAULT_PING;
		laufzeitText = DEFAULT_LAUFZEIT;
		neueMinute = false;
	}
	
	public void setDate(Date d) {
		uhrzeit = DateFormat.getTimeInstance(getUhrzeitStyle()).format(d);
		DateFormat df = DateFormat.getDateInstance(getDatumStyle());
		datum = df.format(d);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		int seconds = cal.get(Calendar.SECOND);
		if (seconds == 0) {
			neueMinute = true;
		}
		
		changed();
	}
	
	public void tickLaufzeit() {
		laufzeit++;
		connectionTime++;
		
		long millis = laufzeit * 1000;
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(millis));
		// System.out.println(time);
		laufzeitText = time;
		
		changed();
	}
	
	public void changed() {
		setChanged();
		notifyObservers();
	}
	
	public void resetConnectionTime() {
		connectionTime = 0;
	}
	
	public String getDatum() {
		return datum;
	}
	
	public int getDatumStyle() {
		return datumStyle;
	}
	
	public void setDatumStyle(int datumStyle) {
		DateFormat.getDateInstance(datumStyle);
		this.datumStyle = datumStyle;
		changed();
	}
	
	public String getLaufzeit() {
		return "Laufzeit: " + laufzeitText;
	}
	
	public void setLaufzeit(long laufzeit) {
		this.laufzeit = laufzeit;
	}
	
	public String getPing() {
		return "[" + connectionTime + "] " + ping;
	}
	
	public void setPing(PingResults res) {
		if (res == null) {
			ping = "Ping: Es ist ein Fehler aufgetreten!";
			changed();
			return;
		}
		
		if (!connected && !res.isConnected()) {
			retries++;
		}
		connected = res.isConnected();
		
		if (connected && res.getPing() >= 0 || res.getVerlust() < 100) {
			retries = 0;
			connectionTime = 1;
			
			int verlust = res.getVerlust();
			String s = res.getPing() + "";
			if (verlust != 0) {
				s += " [" + verlust + "% Verlust]";
			}
			ping = "Ping: " + s;
		} else {
			ping = "Keine Verbindung! [" + retries + " RT]";
		}
		changed();
	}
	
	public String getUhrzeit() {
		return uhrzeit;
	}
	
	public int getUhrzeitStyle() {
		return uhrzeitStyle;
	}
	
	public void setUhrzeitStyle(int uhrzeitStyle) {
		DateFormat.getTimeInstance(uhrzeitStyle);
		this.uhrzeitStyle = uhrzeitStyle;
		changed();
	}
	
	public boolean isNeueMinute() {
		if (neueMinute) {
			neueMinute = false;
			return true;
		}
		return false;
	}
	
}
