package uhrzeit.window.frame;

import net.miginfocom.swing.MigLayout;
import uhrzeit.NeoUhrzeit;
import uhrzeit.data.DisplayData;
import uhrzeit.data.PingCalculator;
import uhrzeit.data.persistency.Einstellungen;
import uhrzeit.window.component.ScalingLabel;
import uhrzeit.window.panel.PingTargetPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static uhrzeit.data.DisplayData.*;

public class UhrzeitFrame extends JFrame implements Observer {

    public static final int RESET_MODIFIKATOR = 1;
    private static final long serialVersionUID = 6888138671600015994L;
    private ScalingLabel uhrzeitLB;
    private JLabel datumLB;
    private JLabel pingLB;
    private JLabel laufzeitLB;
    private JMenuBar menuBar;

    private ExtendedPingLogFrame logframe;
    private VollbildUhrzeitFrame vollbildFrame;

    private Timer frameTimer;
    private Dimension startupSize;
    private Dimension resetDimension;
    private Point resetPoint;

    private UhrzeitFrame frame;
    private JCheckBoxMenuItem ontopCB;
    private JCheckBoxMenuItem resizableCB;
    private JMenuItem fintsizeBT;
    private JCheckBoxMenuItem uhrzeitPrafixBT, datumPrafixBT;
    private JMenu fontSizeCB;
    private JCheckBoxMenuItem uhrzeitBorderCB;

    private HashMap<JRadioButtonMenuItem, Integer> styleMap;
    private ButtonGroup datumStyleGroup, uhrzeitStyleGroup, fullscreenStyleGroup;
    private JMenu uhrzeitStyleMN;
    private JMenu uhrzeitStyleFullscreenMN;
    private JMenu datumStyleMN;
    private JMenuItem mntmLaufzeitZurcksetzen;
    private JMenuItem pingZielTF;
    private JCheckBoxMenuItem enablePingtestBT;
    private JMenuItem thresholdBT;
    private JCheckBoxMenuItem activateLogBT;
    private JCheckBoxMenuItem quickShowBT;
    private JCheckBoxMenuItem quickHideBT;

    public UhrzeitFrame() {
        setTitle(NeoUhrzeit.TITLE + " " + NeoUhrzeit.VERSION);
        setVisible(true);
        frame = this;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        datumLB = new JLabel(DEFAULT_DATE);
        datumLB.setFont(new Font("Tahoma", Font.PLAIN, 13));
        uhrzeitLB = new ScalingLabel(DEFAULT_TIME);
        laufzeitLB = new JLabel(DEFAULT_LAUFZEIT);
        laufzeitLB.setFont(new Font("Tahoma", Font.PLAIN, 13));
        pingLB = new JLabel(DEFAULT_PING);
        pingLB.setFont(new Font("Tahoma", Font.PLAIN, 13));
        uhrzeitLB.setPadding(1);

        styleMap = new HashMap<>();
        datumStyleGroup = new ButtonGroup();
        uhrzeitStyleGroup = new ButtonGroup();
        fullscreenStyleGroup = new ButtonGroup();

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnDatei = new JMenu("Datei");
        menuBar.add(mnDatei);

        JMenu mnAngelegteDaten = new JMenu("Angelegte Daten");
        mnDatei.add(mnAngelegteDaten);

        mntmLaufzeitZurcksetzen = new JMenuItem("Laufzeit zurücksetzen");
        mntmLaufzeitZurcksetzen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                DisplayData d = NeoUhrzeit.getDisplayData();
                d.setLaufzeit(0);
            }
        });
        mnDatei.add(mntmLaufzeitZurcksetzen);
        mnDatei.addSeparator();

        JMenuItem mntmAnzeigen = new JMenuItem("Anzeigen");
        mntmAnzeigen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File f = NeoUhrzeit.getFilemanager().getSettingsFile().getParentFile();
                NeoUhrzeit.browse(f.toURI());
            }
        });
        mnAngelegteDaten.add(mntmAnzeigen);

        JMenuItem mntmLschen = new JMenuItem("Löschen");
        mntmLschen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = NeoUhrzeit.showConfirmMessage(
                        "Alle angelegten Daten werden permanent gelöscht.\nDieser Vorgang kann nicht rückgängig gemacht werden." +
                                "\n\nDas Programm wird beendet.\n\nFortfahren?",
                        frame);
                if (!b) {
                    return;
                }

                boolean error = false;
                try {
                    error = !NeoUhrzeit.getFilemanager().delete();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    NeoUhrzeit.showErrormessage("Fehler beim Löschen der Daten.", e1);
                    return;
                }
                if (error) {
                    NeoUhrzeit.showErrormessage("Ein unbekannter und unerwarteter Fehler ist aufgetreten.");
                    return;
                }

                System.exit(0);
            }
        });
        mnAngelegteDaten.add(mntmLschen);

        JMenuItem mntmBeenden = new JMenuItem("Beenden");
        mntmBeenden.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
                System.exit(0);
            }
        });
        mnDatei.add(mntmBeenden);

        JMenu mnBearbeiten = new JMenu("Bearbeiten");
        menuBar.add(mnBearbeiten);

        fontSizeCB = new JMenu("Schriftgröße ändern");
        mnBearbeiten.add(fontSizeCB);

        mnBearbeiten.addSeparator();

        JMenuItem mntmGrer = new JMenuItem("Größer");
        mntmGrer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setFontsize(datumLB.getFont().getSize() + 1);
            }
        });
        fontSizeCB.add(mntmGrer);

        JMenuItem mntmKleiner = new JMenuItem("Kleiner");
        mntmKleiner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFontsize(datumLB.getFont().getSize() - 1);
            }
        });
        fontSizeCB.add(mntmKleiner);
        fontSizeCB.addSeparator();

        fintsizeBT = new JMenuItem("Selbst festlegen");
        fontSizeCB.add(fintsizeBT);

        uhrzeitStyleMN = new JMenu("Uhrzeit-Style ändern [Hauptfenster]");
        mnBearbeiten.add(uhrzeitStyleMN);

        uhrzeitStyleFullscreenMN = new JMenu("Uhrzeit-Style ändern [Vollbild]");
        mnBearbeiten.add(uhrzeitStyleFullscreenMN);

        datumStyleMN = new JMenu("Datum-Style ändern");
        mnBearbeiten.add(datumStyleMN);

        mnBearbeiten.addSeparator();
        JMenu mnVollbildEinstellungen = new JMenu("Vollbild Einstellungen");
        mnBearbeiten.add(mnVollbildEinstellungen);

        quickShowBT = new JCheckBoxMenuItem("Schnell anzeigen");
        quickShowBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NeoUhrzeit.getEinstellungen().set(Einstellungen.ENABLE_FULLSCREEN_QUICK_SHOW,
                        String.valueOf(quickShowBT.isSelected()));
            }
        });
        mnVollbildEinstellungen.add(quickShowBT);

        quickHideBT = new JCheckBoxMenuItem("Schnell verbergen");
        quickHideBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                NeoUhrzeit.getEinstellungen().set(Einstellungen.ENABLE_FULLSCREEN_QUICK_HIDE,
                        String.valueOf(quickHideBT.isSelected()));
            }
        });
        mnVollbildEinstellungen.add(quickHideBT);

        fintsizeBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s = JOptionPane.showInputDialog(frame, "Neue Größe eingeben: ");
                if (NeoUhrzeit.isNumeric(s)) {
                    int i = Integer.parseInt(s);
                    setFontsize(i);
                }
            }
        });

        JMenu mnAnsicht = new JMenu("Ansicht");
        menuBar.add(mnAnsicht);

        JMenu mnZurcksetzen = new JMenu("Zurücksetzen");
        mnAnsicht.add(mnZurcksetzen);

        JMenuItem mntmFenstergre = new JMenuItem("Fenstergröße");
        mntmFenstergre.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                resetDimension = new Dimension(startupSize);
                frameTimer.start();
            }
        });
        mnZurcksetzen.add(mntmFenstergre);

        JMenuItem mntmFensterposition = new JMenuItem("Fensterposition");
        mntmFensterposition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Point p = getResettingLocation();
                resetPoint = new Point(p);
                frameTimer.start();
            }
        });
        mnZurcksetzen.add(mntmFensterposition);
        mnZurcksetzen.addSeparator();

        JMenuItem mntmBeides = new JMenuItem("Beides");
        mntmBeides.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetDimension = new Dimension(startupSize);
                Point p = getResettingLocation();
                resetPoint = new Point(p);
                frameTimer.start();
            }
        });
        mnZurcksetzen.add(mntmBeides);

        JMenuItem mntmBeidessofort = new JMenuItem("Beides (Sofort)");
        mntmBeidessofort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSize(new Dimension(startupSize));
                setLocationRelativeTo(null);
            }
        });
        mnZurcksetzen.add(mntmBeidessofort);

        ontopCB = new JCheckBoxMenuItem("Immer im Vordergrund");
        ontopCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = ontopCB.isSelected();
                setAlwaysOnTop(b);
                NeoUhrzeit.getEinstellungen().set(Einstellungen.ONTOP, String.valueOf(b));
                System.out.println("IM VORDERGRUND");
            }
        });
        mnAnsicht.addSeparator();

        uhrzeitBorderCB = new JCheckBoxMenuItem("Rahmen um \"Uhrzeit\"");
        uhrzeitBorderCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (uhrzeitBorderCB.isSelected()) {
                    uhrzeitLB.setBorder(new LineBorder(Color.BLACK));
                } else {
                    uhrzeitLB.setBorder(null);
                }
            }
        });
        mnAnsicht.add(uhrzeitBorderCB);

        mnAnsicht.addSeparator();
        JMenuItem mntmVollbildmodus = new JMenuItem("Vollbild-Modus");
        mntmVollbildmodus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fullscreenMode();
            }
        });
        mnAnsicht.add(mntmVollbildmodus);
        mnAnsicht.addSeparator();
        mnAnsicht.add(ontopCB);

        resizableCB = new JCheckBoxMenuItem("Nicht fixieren");
        resizableCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = resizableCB.isSelected();
                setResizable(b);
                NeoUhrzeit.getEinstellungen().set(Einstellungen.RESIZEABLE, String.valueOf(b));
            }
        });
        mnAnsicht.add(resizableCB);

        JMenu mnPing = new JMenu("Ping");
        menuBar.add(mnPing);

        enablePingtestBT = new JCheckBoxMenuItem("Pingtest aktivieren");
        enablePingtestBT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Boolean b = enablePingtestBT.isSelected();
                NeoUhrzeit.getPingCalculator().setEnabled(b);
                NeoUhrzeit.getEinstellungen().set(Einstellungen.ENABLE_PINGTEST, String.valueOf(b));
            }
        });
        mnPing.add(enablePingtestBT);
        mnPing.addSeparator();

        JMenuItem mntmPingzielndern = new JMenuItem("Ping-Ziel ändern");
        mntmPingzielndern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                PingTargetPanel p = new PingTargetPanel();
                int i = JOptionPane.showConfirmDialog(frame, p, "Ping ändern", JOptionPane.OK_CANCEL_OPTION);
                if (i == JOptionPane.OK_OPTION) {
                    String s = p.getText();
                    setPingAdress(s);
                }
            }
        });
        mnPing.add(mntmPingzielndern);

        pingZielTF = new JMenuItem("<Ziel>");
        pingZielTF.setEnabled(false);
        mnPing.add(pingZielTF);
        mnPing.addSeparator();

        JMenu mnPingLog = new JMenu("Ping Log");
        mnPing.add(mnPingLog);

        JMenuItem mntmZeigeErweitertenLog = new JMenuItem("Zeige erweiterten Log");
        mntmZeigeErweitertenLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                logframe.zeigen();
            }
        });

        JMenuItem mntmPingthresholdndern = new JMenuItem("Ping-Threshold ändern");
        mntmPingthresholdndern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Einstellungen e = NeoUhrzeit.getEinstellungen();
                String s = JOptionPane.showInputDialog("Neuer Ping-Threshold: ",
                        e.getInteger(Einstellungen.PING_THRESHOLD));
                if (NeoUhrzeit.isNumeric(s)) {
                    int i = Integer.parseInt(s);
                    e.set(Einstellungen.PING_THRESHOLD, String.valueOf(i));
                    updateThresholdBT();
                }
            }
        });

        activateLogBT = new JCheckBoxMenuItem("Ping Log Aktivieren");
        mnPingLog.add(activateLogBT);
        mnPingLog.addSeparator();
        mnPingLog.add(mntmPingthresholdndern);

        thresholdBT = new JMenuItem("[Threshold]");
        thresholdBT.setEnabled(false);
        mnPingLog.add(thresholdBT);
        mnPingLog.addSeparator();
        mnPingLog.add(mntmZeigeErweitertenLog);

        JMenu menu = new JMenu("?");
        menuBar.add(menu);

        JMenuItem mntmber = new JMenuItem("Über");
        mntmber.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NeoUhrzeit.showInformationMessage(NeoUhrzeit.TITLE + " by " + NeoUhrzeit.AUTHOR + "\n"
                        + NeoUhrzeit.VERSION + ", Letztes Update: " + NeoUhrzeit.DATE);
            }
        });
        menu.add(mntmber);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (logframe != null)
                    logframe.align();
            }

            @Override
            public void componentMoved(ComponentEvent arg0) {
                if (logframe != null)
                    logframe.align();
            }
        });

        getContentPane().setLayout(new MigLayout("", "[35px][33px,grow]", "[116.00px,grow][14px]"));
        getContentPane().add(datumLB, "cell 0 0,alignx left,aligny top");
        getContentPane().add(uhrzeitLB, "cell 1 0,grow");
        getContentPane().add(laufzeitLB, "cell 0 1,alignx left,aligny bottom");
        getContentPane().add(pingLB, "cell 1 1,alignx right,aligny bottom");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                onQuit();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                Einstellungen e = NeoUhrzeit.getEinstellungen();
                if (!e.getBoolean(Einstellungen.ENABLE_FULLSCREEN_QUICK_SHOW)) {
                    return;
                }

                if (arg0.getClickCount() == 2) {
                    fullscreenMode();
                }
            }
        });

        logframe = new ExtendedPingLogFrame(this);
        NeoUhrzeit.getPingCalculator().setExtendedLog(logframe);
        vollbildFrame = new VollbildUhrzeitFrame();

        frameTimer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetVorgang();
            }
        });
        frameTimer.setRepeats(true);
        frameTimer.start();

        try {
            readFromSettings();
        } catch (Exception e) {
            e.printStackTrace();
            NeoUhrzeit.showErrormessage(
                    "Nicht alle Einstellungen konnten korrekt geladen werden.\nEs werden nun Standart-Einstellungen genutzt.",
                    e);
            NeoUhrzeit.setEinstellungen(Einstellungen.getDefaultEinstellungen());
            readFromSettings();
        }
        setAlwaysOnTop(NeoUhrzeit.getEinstellungen().getBoolean(Einstellungen.ONTOP));
        initStyleButtons();

        setVisible(true);
    }

    protected void fullscreenMode() {
        vollbildFrame.zeigen();
    }

    public void updateDisplayedData(DisplayData d) {
        Einstellungen e = NeoUhrzeit.getEinstellungen();
        String uhr = d.getUhrzeit();
        if (e.getBoolean(Einstellungen.PRAEFIX_UHRZEIT))
            uhr = "Uhrzeit: " + uhr;
        uhrzeitLB.setText(uhr);

        String dat = d.getDatum();
        if (e.getBoolean(Einstellungen.PRAEFIX_DATUM))
            dat = "Datum: " + dat;
        datumLB.setText(dat);

        laufzeitLB.setText(d.getLaufzeit());
        if (NeoUhrzeit.getPingCalculator().isServiceRunning()) {
            pingLB.setText(d.getPing());
        } else {
            pingLB.setText("Pingservice deaktiviert.");
        }

        if (d.isNeueMinute()) {
            int threshold = NeoUhrzeit.getEinstellungen().getInteger(Einstellungen.PING_THRESHOLD);
            PingCalculator c = NeoUhrzeit.getPingCalculator();

            int ping = c.getDurchschnittsping();
            System.out.println(c.getLogTimeFormat() + " - Durchschnittsping diese Minute: " + ping);
            if (ping >= threshold || ping < 0) {
                c.logExternal();
            }
            c.resetDurchschnittsping();
        }
    }

    private void resetVorgang() {
        if (resetDimension != null) {
            int w = getWidth();
            int h = getHeight();

            int wi = resettingMod(w, (int) resetDimension.getWidth());
            int hi = resettingMod(h, (int) resetDimension.getHeight());

            setSize(w + wi, h + hi);
            if (wi == 0 && hi == 0) {
                resetDimension = null;
            }
        }

        if (resetPoint != null) {
            int x = (int) getX();
            int y = (int) getY();

            int xi = resettingMod(x, (int) resetPoint.getX());
            int yi = resettingMod(y, (int) resetPoint.getY());

            setLocation(x + xi, y + yi);
            if (xi == 0 && yi == 0) {
                resetPoint = null;
            }
        }

        if (resetDimension == null && resetPoint == null) {
            frameTimer.stop();
        }
    }

    private void readFromSettings() {
        // TODO read from settings here
        Einstellungen e = NeoUhrzeit.getEinstellungen();
        Einstellungen defaultEinstellungen = Einstellungen.getDefaultEinstellungen();
        PingCalculator c = NeoUhrzeit.getPingCalculator();

        int x = e.getInteger(Einstellungen.POS_X);
        int y = e.getInteger(Einstellungen.POS_Y);
        setLocation(x, y);

        int w = e.getInteger(Einstellungen.WIDTH);
        int h = e.getInteger(Einstellungen.HEIGHT);
        if (h > 0 && w > 0) {
            setSize(w, h);
        } else {
            System.out.println("Invalid size. Resetting. w:" + w + " h:" + h);
            pack();
        }
        startupSize = getSize();
        setFontsize(e.getInteger(Einstellungen.FONTSIZE));

        resizableCB.setSelected(e.getBoolean(Einstellungen.RESIZEABLE));
        setResizable(e.getBoolean(Einstellungen.RESIZEABLE));
        ontopCB.setSelected(e.getBoolean(Einstellungen.ONTOP));
        e.getBoolean(Einstellungen.ONTOP);

        setPingAdress(e.getString(Einstellungen.PING_TARGET));

        DisplayData data = NeoUhrzeit.getDisplayData();
        data.setDatumStyle(e.getInteger(Einstellungen.STYLE_DATUM));
        data.setUhrzeitStyle(e.getInteger(Einstellungen.STYLE_UHRZEIT));
        data.setUhrzeitFullscreenStyle(e.getInteger(Einstellungen.STYLE_UHRZEIT_FULLSCREN));

        quickHideBT.setSelected(e.getBoolean(Einstellungen.ENABLE_FULLSCREEN_QUICK_HIDE));
        quickShowBT.setSelected(e.getBoolean(Einstellungen.ENABLE_FULLSCREEN_QUICK_SHOW));

        Boolean b = e.getBoolean(Einstellungen.ENABLE_PINGTEST);
        c.setEnabled(b);
        enablePingtestBT.setSelected(b);

        updateThresholdBT();

        // Werte für später testen
        e.getInteger(Einstellungen.STYLE_DATUM);
        e.getInteger(Einstellungen.STYLE_UHRZEIT);
        e.getInteger(Einstellungen.STYLE_UHRZEIT_FULLSCREN);
        e.getBoolean(Einstellungen.PRAEFIX_DATUM);
        e.getBoolean(Einstellungen.PRAEFIX_UHRZEIT);

        System.out.println("Settings loaded and applied.");
    }

    public void onQuit() {
        System.out.println("Wandow closing...");
        Einstellungen e = NeoUhrzeit.getEinstellungen();

        e.set(Einstellungen.POS_X, String.valueOf(getX()));
        e.set(Einstellungen.POS_Y, String.valueOf(getY()));
        e.set(Einstellungen.WIDTH, String.valueOf(getWidth()));
        e.set(Einstellungen.HEIGHT, String.valueOf(getHeight()));

        File f = NeoUhrzeit.getFilemanager().getSettingsFile();
        try {
            e.save(f);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setFontsize(float size) {
        if (size <= 0) {
            NeoUhrzeit.showErrormessage("Die neue größe ist zu klein.\nBitte wählen Sie einen wert größer als 0.");
            return;
        }

        Font f = uhrzeitLB.getFont().deriveFont(size);

        datumLB.setFont(f);
        pingLB.setFont(f);
        laufzeitLB.setFont(f);
        fontSizeCB.setText("Schriftgröße ändern [" + (int) size + "]");

        NeoUhrzeit.getEinstellungen().set(Einstellungen.FONTSIZE, String.valueOf((int) size));
    }

    private void initStyleButtons() {
        Einstellungen e = NeoUhrzeit.getEinstellungen();
        e.getBoolean(Einstellungen.PRAEFIX_UHRZEIT);

        // Default Uhrzeit Menu
        uhrzeitStyleMN.add(generateStyleMenuItem(DateFormat.SHORT, true, false));
        uhrzeitStyleMN.add(generateStyleMenuItem(DateFormat.MEDIUM, true, false));
        uhrzeitStyleMN.add(generateStyleMenuItem(DateFormat.LONG, true, false));
        uhrzeitStyleMN.add(generateStyleMenuItem(DateFormat.FULL, true, false));

        // Fullscreen Uhrzeit Menu
        uhrzeitStyleFullscreenMN.add(generateStyleMenuItem(DateFormat.SHORT, true, true));
        uhrzeitStyleFullscreenMN.add(generateStyleMenuItem(DateFormat.MEDIUM, true, true));

        // Default Date Menu
        datumStyleMN.add(generateStyleMenuItem(DateFormat.SHORT, false, false));
        datumStyleMN.add(generateStyleMenuItem(DateFormat.MEDIUM, false, false));
        datumStyleMN.add(generateStyleMenuItem(DateFormat.LONG, false, false));
        datumStyleMN.add(generateStyleMenuItem(DateFormat.FULL, false, false));

        uhrzeitStyleMN.addSeparator();
        uhrzeitPrafixBT = new JCheckBoxMenuItem("Zeige Präfix");
        uhrzeitStyleMN.add(uhrzeitPrafixBT);
        uhrzeitPrafixBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean b = uhrzeitPrafixBT.isSelected();
                NeoUhrzeit.getEinstellungen().set(Einstellungen.PRAEFIX_UHRZEIT, String.valueOf(b));
            }
        });
        uhrzeitPrafixBT.setSelected(e.getBoolean(Einstellungen.PRAEFIX_UHRZEIT));

        datumStyleMN.addSeparator();
        datumPrafixBT = new JCheckBoxMenuItem("Zeige Präfix");
        datumStyleMN.add(datumPrafixBT);
        datumPrafixBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean b = datumPrafixBT.isSelected();
                NeoUhrzeit.getEinstellungen().set(Einstellungen.PRAEFIX_DATUM, String.valueOf(b));
            }
        });
        datumPrafixBT.setSelected(e.getBoolean(Einstellungen.PRAEFIX_DATUM));
    }

    // @SuppressWarnings("unused")
    private JMenuItem generateStyleMenuItem(int style, boolean uhrzeit, boolean fullscreen) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem();
        String s = "";

        switch (style) {
            case DateFormat.DEFAULT:
                s = "Normal";
                break;
            case DateFormat.FULL:
                s = "Vollständig";
                break;
            case DateFormat.LONG:
                s = "Lang";
                break;
            case DateFormat.SHORT:
                s = "Kurz";
                break;
            default:
                return new JMenuItem("<Unbekannt>");
        }
        item.setText(s);
        styleMap.put(item, new Integer(style));
        Einstellungen e = NeoUhrzeit.getEinstellungen();

        if (uhrzeit) {
            if (fullscreen) {
                fullscreenStyleGroup.add(item);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        int i = styleMap.get(arg0.getSource());
                        DisplayData d = NeoUhrzeit.getDisplayData();
                        d.setUhrzeitFullscreenStyle(i);
                        d.changed();
                    }
                });
                if (style == e.getInteger(Einstellungen.STYLE_UHRZEIT_FULLSCREN)) {
                    item.setSelected(true);
                }
            } else {
                uhrzeitStyleGroup.add(item);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        int i = styleMap.get(arg0.getSource());
                        DisplayData d = NeoUhrzeit.getDisplayData();
                        d.setUhrzeitStyle(i);
                        d.changed();
                    }
                });
                if (style == e.getInteger(Einstellungen.STYLE_UHRZEIT)) {
                    item.setSelected(true);
                }
            }
        } else {
            datumStyleGroup.add(item);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int i = styleMap.get(arg0.getSource());
                    DisplayData d = NeoUhrzeit.getDisplayData();
                    d.setDatumStyle(i);
                    d.changed();
                }
            });
            if (style == e.getInteger(Einstellungen.STYLE_DATUM)) {
                item.setSelected(true);
            }
        }
        return item;
    }

    public void setPingAdress(String adress) {
        NeoUhrzeit.getEinstellungen().set(Einstellungen.PING_TARGET, adress);
        pingZielTF.setText("[" + adress + "]");
    }

    private void updateThresholdBT() {
        Einstellungen e = NeoUhrzeit.getEinstellungen();
        int i = e.getInteger(Einstellungen.PING_THRESHOLD);

        thresholdBT.setText("[" + i + "]");
    }

    private int resettingMod(int current, int soll) {
        if (current < soll) {
            return RESET_MODIFIKATOR;
        } else {
            if (current > soll) {
                return -RESET_MODIFIKATOR;
            }
        }
        return 0;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DisplayData) {
            DisplayData d = (DisplayData) o;
            updateDisplayedData(d);
        }
    }

    private Point getResettingLocation() {
        Point p = getLocation();
        setLocationRelativeTo(null);
        Point res = getLocation();
        setLocation(p);

        return res;
    }

    public VollbildUhrzeitFrame getVollbildFrame() {
        return vollbildFrame;
    }
}
