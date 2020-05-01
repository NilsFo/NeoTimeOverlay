package uhrzeit;

import uhrzeit.data.DisplayData;
import uhrzeit.data.PingCalculator;
import uhrzeit.data.UhrzeitTimer;
import uhrzeit.data.persistency.Einstellungen;
import uhrzeit.data.persistency.FileManager;
import uhrzeit.window.frame.UhrzeitFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class NeoUhrzeit {
	
	public static final String AUTHOR = "Nils Förster";
	public static final String DATE = "13.11.2015";
	public static final String TITLE = "Neo Uhrzeit Overlay";
	public static final String VERSION = "V 1.0";
	
	private static FileManager filemanager;
	private static Einstellungen einstellungen;
	private static UhrzeitFrame frame;
	private static DisplayData data;
	private static PingCalculator pingCalculator;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			filemanager = new FileManager();
		} catch (IOException e1) {
			e1.printStackTrace();
			// TODO what do?
		}
		
		try {
			einstellungen = new Einstellungen(filemanager.getSettingsFile());
		} catch (Exception e1) {
			// e1.printStackTrace();
			einstellungen = Einstellungen.getDefaultEinstellungen();
		}
		
		data = new DisplayData();
		pingCalculator = new PingCalculator(data);
		new UhrzeitTimer(data);
		
		frame = new UhrzeitFrame();
		data.addObserver(frame);
		data.addObserver(frame.getVollbildFrame());
	}
	
	public static void browse(URI uri) {
		try {
			Desktop d = Desktop.getDesktop();
			d.browse(uri);
		} catch (IOException e) {
			e.printStackTrace();
			showErrormessage("Fehler! Das ausgewählte Objekt kann nicht geöffnet werden.");
		}
	}
	
	public static void showErrormessage(String message, Component parent) {
		JOptionPane.showMessageDialog(parent, message, "Fehler!", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showErrormessage(String message) {
		showErrormessage(message, getMainframe());
	}
	
	public static void showErrormessage(String message, Exception e) {
		showErrormessage(message, e, getMainframe());
	}
	
	public static void showErrormessage(String message, Exception e, Component parent) {
		String m = message.trim() + "\n\n" + e.getClass().getName() + ":\n'" + e.getMessage() + "'";
		showErrormessage(m, parent);
	}
	
	public static void showInformationMessage(String message, Component parent) {
		JOptionPane.showMessageDialog(parent, message, "Information!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showInformationMessage(String message) {
		showInformationMessage(message, getMainframe());
	}
	
	public static boolean showConfirmMessage(String message, Component parent) {
		int i = JOptionPane.showConfirmDialog(parent, message, "Bestätigen", JOptionPane.YES_NO_OPTION);
		return i == JOptionPane.YES_OPTION;
	}
	
	public static boolean showConfirmMessage(String message) {
		return showConfirmMessage(message, getMainframe());
	}
	
	public static boolean isNumeric(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static DisplayData getDisplayData() {
		return data;
	}
	
	public static Einstellungen getEinstellungen() {
		return einstellungen;
	}
	
	public static void setEinstellungen(Einstellungen einstellungen) {
		NeoUhrzeit.einstellungen = einstellungen;
	}
	
	public static FileManager getFilemanager() {
		return filemanager;
	}
	
	public static UhrzeitFrame getMainframe() {
		return frame;
	}
	
	public static PingCalculator getPingCalculator() {
		return pingCalculator;
	}
	
}
