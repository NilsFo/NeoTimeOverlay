package uhrzeit.data.persistency;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static final int FILES_TO_CREATE = 1;
    private File settingsFile, pingLogFile, fileDir;

    public FileManager() throws IOException {
        initFiles();
    }

    public void initFiles() throws IOException {
        String dir = getSystemEddyFolder();
        File d = new File(dir + "/Uhrzeitoverlay Neo");
        File s = new File(d + "/settings.xml");
        File l = new File(d + "/ping.log");

        int createCounter = 0;
        if (!d.exists()) {
            d.mkdirs();
        }
        if (!s.exists()) {
            s.createNewFile();
            createCounter++;
        }
        if (!l.exists()) {
            l.createNewFile();
            createCounter++;
        }

        if (createCounter == FILES_TO_CREATE) {
            System.out.println("Every file created successfully.");
        }

        settingsFile = s;
        pingLogFile = l;
        fileDir = d;
    }

    public boolean delete() throws IOException {
        settingsFile.delete();
        fileDir.delete();

        System.out.println("Löschen ausgeführt. DirFile exists? " + fileDir.exists() + " " + fileDir.getAbsolutePath());

        return !fileDir.exists();
    }

    public File getPingLogFile() {
        return pingLogFile;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public String getSystemEddyFolder() {
        String dir = System.getProperty("user.home");
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            dir = System.getenv("APPDATA");
        }

        return dir;
    }
}
