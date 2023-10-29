package uhrzeit.data.persistency;

import uhrzeit.NeoUhrzeit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Properties;

public class Einstellungen {
	
	public static final String ENABLE_FULLSCREEN_QUICK_HIDE = "fullscreenQuickHide";
	public static final String ENABLE_FULLSCREEN_QUICK_SHOW = "fullscreenQuickShow";
	public static final String ENABLE_PINGLOG = "enablePinglog";
	public static final String ENABLE_PINGTEST = "enablePingtest";
	public static final String FONTSIZE = "fontsize";
	public static final String HEIGHT = "height";
	public static final String ONTOP = "alwaysontop";
	public static final String PING_TARGET = "ping_target";
	public static final String PING_THRESHOLD = "ping_threshold";
	public static final String POS_X = "pos_x";
	public static final String POS_Y = "pos_y";
	public static final String PRAEFIX_DATUM = "datumpraefix";
	public static final String PRAEFIX_UHRZEIT = "uhrzeitpraefix";
	public static final String PROPERTIES_COMMENT = NeoUhrzeit.TITLE + " - " + NeoUhrzeit.VERSION;
	public static final String RESIZEABLE = "resizeable";
	public static final String STYLE_DATUM = "datumstyle";
	public static final String STYLE_UHRZEIT = "uhrzeitstyle";
	public static final String STYLE_UHRZEIT_FULLSCREN = "uhrzeitstyleFullscreen";
	public static final String WIDTH = "width";
	private Properties properties;
	
	private Einstellungen() {
		properties = getDefaults();
		properties.list(System.out);
	}
	
	public Einstellungen(File inputFile) throws IOException, NumberFormatException {
		properties = new Properties();
		FileInputStream fis = new FileInputStream(inputFile);
		properties.loadFromXML(fis);
	}
	
	public boolean save(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}
		System.out.println(
				"Speichern der Einstellungen steht unmittelbar bevor! Einträge: " + properties.keySet().size());
		FileOutputStream fos = new FileOutputStream(file);
		properties.storeToXML(fos, PROPERTIES_COMMENT);
		return true;
	}
	
	public boolean getBoolean(String key) {
		String s = properties.getProperty(key);
		// System.out.println("get bool: " + s + " -> " +
		// Boolean.getBoolean(s));
		
		return s.toLowerCase().equals("true");
	}
	
	public int getInteger(String key) {
		String s = properties.getProperty(key);
		return Integer.parseInt(s);
	}
	
	public String getString(String key) {
		return properties.getProperty(key);
	}
	
	public void set(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public static Einstellungen getDefaultEinstellungen() {
		System.out.println("Requesting default Einstellungen.");
		return new Einstellungen();
	}
	
	public static Properties getDefaults() {
		// TODO Properties hier
		Properties p = new Properties();
		
		p.setProperty(POS_X, "-1");
		p.setProperty(POS_Y, "-1");
		p.setProperty(WIDTH, "-1");
		p.setProperty(HEIGHT, "-1");
		p.setProperty(FONTSIZE, "13");
		p.setProperty(PING_THRESHOLD, "120");
		p.setProperty(STYLE_DATUM, DateFormat.FULL + "");
		p.setProperty(STYLE_UHRZEIT, DateFormat.MEDIUM + "");
		p.setProperty(STYLE_UHRZEIT_FULLSCREN, DateFormat.SHORT + "");
		
		p.setProperty(ONTOP, "true");
		p.setProperty(RESIZEABLE, "true");
		p.setProperty(PRAEFIX_DATUM, "false");
		p.setProperty(PRAEFIX_UHRZEIT, "true");
		p.setProperty(ENABLE_PINGTEST, "true");
		p.setProperty(ENABLE_PINGLOG, "true");
		p.setProperty(ENABLE_FULLSCREEN_QUICK_SHOW, "true");
		p.setProperty(ENABLE_FULLSCREEN_QUICK_HIDE, "false");
		
		p.setProperty(PING_TARGET, "8.8.8.8");
		
		return p;
	}
}
