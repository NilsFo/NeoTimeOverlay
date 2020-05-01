package uhrzeit.data;

public class PingResults {
	
	private int ping;
	private boolean connected;
	private int verlust;
	
	public PingResults(int ping, boolean connected, int verlust) {
		super();
		this.ping = ping;
		this.connected = connected;
		this.verlust = verlust;
	}
	
	public int getPing() {
		return ping;
	}
	
	public int getVerlust() {
		return verlust;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
}
