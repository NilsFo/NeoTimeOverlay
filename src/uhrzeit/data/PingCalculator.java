package uhrzeit.data;

import uhrzeit.NeoUhrzeit;
import uhrzeit.data.concurrent.PingLogRunner;
import uhrzeit.data.persistency.Einstellungen;
import uhrzeit.window.frame.ExtendedPingLogFrame;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingCalculator {

    public static final String MITTELWERT_RX = "mittelwert = (\\d+)ms";
    public static final long PING_DELAY = 3000;

    private DisplayData data;
    private ExecutorService service;
    private ArrayList<Integer> duchschnittPingList;

    private ExecutorService logExecutor;

    private ExtendedPingLogFrame extendedLog;

    public PingCalculator(DisplayData data) {
        this.data = data;
        duchschnittPingList = new ArrayList<>();

        logExecutor = Executors.newSingleThreadExecutor();
    }

    public void ping() {
        PingResults r = null;
        r = checkPing();
        if (r.isConnected()) {
            duchschnittPingList.add(new Integer(r.getPing()));
        } else {
            duchschnittPingList.add(new Integer(Integer.MIN_VALUE));
        }

        data.setPing(r);
    }

    public PingResults checkPing() {
        Process process = null;
        try {
            Einstellungen e = NeoUhrzeit.getEinstellungen();
            process = new ProcessBuilder().command("ping", e.getString(Einstellungen.PING_TARGET))
                    .redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Scanner scanner = new Scanner(process.getInputStream(), "Cp850");
        int ping = -1;
        int verlust = 0;
        log(ExtendedPingLogFrame.DELETE_MSG);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine().trim();
            log(s);

            s = s.toLowerCase();
            if (s.equals("")) {
                continue;
            }

            if (s.toLowerCase().contains("versuchen Sie es erneut".toLowerCase())) {
                scanner.close();
                return new PingResults(-1, false, 100);
            }

            if (s.toLowerCase().contains("verlust")) {
                String st = s.substring(s.indexOf('(') + 1, s.indexOf('%'));
                verlust = Integer.parseInt(st);
            }
            if (s.toLowerCase().contains("mittelwert")) {
                String st = s.substring(s.lastIndexOf(' ') + 1, s.lastIndexOf('m'));
                ping = Integer.parseInt(st);
            }
        }
        scanner.close();

        boolean connected = !(verlust == 100);
        return new PingResults(ping, connected, verlust);
    }

    private void startupService() {
        if (service != null) {
            service.shutdown();
        }

        NeoUhrzeit.getDisplayData().resetConnectionTime();
        service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                ping();
                service.submit(this);
            }
        });
    }

    public synchronized void setEnabled(boolean enabled) {
        if (enabled) {
            startupService();
        } else {
            if (service != null)
                service.shutdown();
        }
    }

    public void logExternal() {
        if (duchschnittPingList.isEmpty()) {
            System.out.println("Wollte Durchschnittsping speichern. FEHLER: Pingliste ist leer.");
            return;
        }

        int ping = getDurchschnittsping();
        logExecutor.submit(new PingLogRunner(ping, new Date()));
    }

    public synchronized String getLogTimeFormat(Date d) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        String s = df.format(d);

        df = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        s += " " + df.format(d);
        return s;
    }

    public void resetDurchschnittsping() {
        duchschnittPingList = new ArrayList<>();
    }

    private void log(String s) {
        if (extendedLog != null) {
            extendedLog.log(s);
        }
    }

    public int getDurchschnittsping() {
        if (duchschnittPingList.isEmpty()) {
            return -1;
        }

        int ping = 0;
        for (int i = 0; i < duchschnittPingList.size(); i++) {
            int p = duchschnittPingList.get(i);
            if (p == Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }

            ping += p;
        }
        ping = ping / duchschnittPingList.size();

        return ping;
    }

    public ExtendedPingLogFrame getExtendedLog() {
        return extendedLog;
    }

    public void setExtendedLog(ExtendedPingLogFrame extendedLog) {
        this.extendedLog = extendedLog;
    }

    public synchronized String getLogTimeFormat() {
        return getLogTimeFormat(new Date());
    }

    public boolean isServiceRunning() {
        if (service == null)
            return false;

        return !service.isShutdown();
    }
}