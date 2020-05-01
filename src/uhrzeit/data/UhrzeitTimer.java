package uhrzeit.data;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UhrzeitTimer {
	
	private DisplayData data;
	private Timer timer;
	
	public UhrzeitTimer(DisplayData data) {
		this.data = data;
		
		UhrzeitSyncronizer syncronizer = new UhrzeitSyncronizer(this);
		ExecutorService s = Executors.newSingleThreadExecutor();
		s.submit(syncronizer);
	}
	
	public synchronized void startTiming() {
		// System.out.println("starting");
		
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.setRepeats(true);
		timer.start();
		tick();
	}
	
	private void tick() {
		data.tickLaufzeit();
		data.setDate(new Date());
	}
	
}
