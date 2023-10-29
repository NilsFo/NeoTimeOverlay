package uhrzeit.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UhrzeitSyncronizer implements Runnable {

    private UhrzeitTimer timer;

    public UhrzeitSyncronizer(UhrzeitTimer timer) {
        this.timer = timer;
    }

    private int toSecond(Date d) {
        SimpleDateFormat f = new SimpleDateFormat("ss");
        String s = f.format(d);
        // System.out.println(s);
        return Integer.parseInt(s);
    }

    @Override
    public void run() {
        boolean found = false;
        int oldSecond = toSecond(new Date());
        while (!found) {
            int newSecond = toSecond(new Date());
            if (newSecond != oldSecond) {
                // System.out.println("DIFFERENT");
                found = true;
            }
        }

        timer.startTiming();
    }

}
