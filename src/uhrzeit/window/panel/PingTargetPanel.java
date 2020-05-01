package uhrzeit.window.panel;

import uhrzeit.NeoUhrzeit;
import uhrzeit.data.persistency.Einstellungen;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingTargetPanel extends JPanel {
	
	private static final long serialVersionUID = -8058833334814304849L;
	private JTextField textField;
	private JButton checkBT;
	private PingTargetPanel frame;
	
	private ExecutorService service;
	private JSpinner spinner;
	
	public PingTargetPanel() {
		Einstellungen e = NeoUhrzeit.getEinstellungen();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 146, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("Neues Ping-Ziel:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		add(textField, gbc_textField);
		textField.setColumns(10);
		textField.setText(e.getString(Einstellungen.PING_TARGET));
		
		JButton btnGooglePingIp = new JButton("Google Ping IP");
		btnGooglePingIp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textField.setText("8.8.8.8");
			}
		});
		GridBagConstraints gbc_btnGooglePingIp = new GridBagConstraints();
		gbc_btnGooglePingIp.insets = new Insets(0, 0, 5, 0);
		gbc_btnGooglePingIp.gridx = 2;
		gbc_btnGooglePingIp.gridy = 0;
		add(btnGooglePingIp, gbc_btnGooglePingIp);
		
		JPanel panel = new JPanel();
		panel.setBorder(
				new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Adresse überprüfen [Beta]",
						TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 70, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblTimeout = new JLabel("Timeout:");
		GridBagConstraints gbc_lblTimeout = new GridBagConstraints();
		gbc_lblTimeout.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeout.gridx = 0;
		gbc_lblTimeout.gridy = 0;
		panel.add(lblTimeout, gbc_lblTimeout);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(5000), new Integer(500), null, new Integer(1)));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 0, 5);
		gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		panel.add(spinner, gbc_spinner);
		
		JLabel lblMs = new JLabel("ms");
		GridBagConstraints gbc_lblMs = new GridBagConstraints();
		gbc_lblMs.insets = new Insets(0, 0, 0, 5);
		gbc_lblMs.gridx = 2;
		gbc_lblMs.gridy = 0;
		panel.add(lblMs, gbc_lblMs);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 3;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		
		frame = this;
		service = Executors.newSingleThreadExecutor();
		
		checkBT = new JButton("Errecihbarkeit prüfen");
		checkBT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = (int) spinner.getValue();
				test(i);
			}
		});
		panel_1.add(checkBT);
		
		textField.requestFocus();
	}
	
	public synchronized void test(int timeout) {
		service.submit(new Runnable() {
			@Override
			public void run() {
				spinner.setEnabled(false);
				checkBT.setEnabled(false);
				
				String ip = getText();
				boolean b = false;
				
				Socket socket = null;
				try {
					socket = new Socket(ip, 80);
				} catch (IOException e) {
					NeoUhrzeit.showErrormessage("Konnte keinen Socket zur Internetverbindung öffnen.", e);
				} finally {
					if (socket != null)
						try {
							socket.close();
							return;
						} catch (IOException e) {
							e.printStackTrace();
							NeoUhrzeit.showErrormessage("Konnte den Socket zur Internetverbindung nicht schliessen.",
									e);
							e.printStackTrace();
							return;
						}
				}
				
				try {
					b = InetAddress.getByName(ip).isReachable(timeout);
				} catch (UnknownHostException e) {
					NeoUhrzeit.showErrormessage("Unbekannter Host.", frame);
				} catch (Exception e) {
					e.printStackTrace();
					NeoUhrzeit.showErrormessage("Unerwarteter Fehler.", e, frame);
				}
				
				if (b) {
					NeoUhrzeit.showInformationMessage("Ziel adresse existiert und ist erreichbar.", frame);
				} else {
					NeoUhrzeit.showErrormessage(
							"Ziel-Adresse konnte nicht erreicht werden.\n\nIst die Adresse korrekt? Ist das Gerät mit dem Internet verbunden?",
							frame);
				}
				
				spinner.setEnabled(true);
				checkBT.setEnabled(true);
			}
		});
	}
	
	public String getText() {
		return textField.getText();
	}
	
}
