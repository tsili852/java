package packets;

public class Packet1Connect extends Packet {
	public String username;
	
	public Packet1Connect() {
		
	}
	
	public Packet1Connect(String uName) {
		username = uName;
	}
}
