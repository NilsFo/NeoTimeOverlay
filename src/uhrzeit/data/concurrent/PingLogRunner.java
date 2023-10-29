package uhrzeit.data.concurrent;

import uhrzeit.NeoUhrzeit;
import uhrzeit.data.PingCalculator;

import java.io.*;
import java.util.Date;

public class PingLogRunner implements Runnable {

    private int ping;
    private Date date;

    public PingLogRunner(int ping, Date date) {
        this.ping = ping;
        this.date = date;
    }

    private String pingToString() {
        if (ping < 0) {
            return "Keine Verbindung!";
        }
        return String.valueOf(ping);
    }

    @Override
    public void run() {
        File f = NeoUhrzeit.getFilemanager().getPingLogFile();
        PingCalculator c = NeoUhrzeit.getPingCalculator();
        PrintWriter writer;

        String log = "";
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                log += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log = log.trim();

        try {
            writer = new PrintWriter(f.getAbsolutePath(), "UTF-8");
            log += "\n" + c.getLogTimeFormat(date) + " - Erhoehter durchschnittlicher Ping: " + pingToString();
            writer.println(log.trim());
            writer.close();
            System.out.println("Erhöhter Ping [" + pingToString() + "] erfolgreich in den Log geschrieben.");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }
}
