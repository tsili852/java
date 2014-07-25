import java.io.IOException;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class main {

	public static void main(String[] args) {
		Server server = new Server();
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.getKryo().register(Packet.class);
		server.getKryo().register(Packet1Connect.class);
		server.getKryo().register(Packet2Line.class);
		
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
        		if (object instanceof Packet) {
        			if (object instanceof Packet1Connect) {
            			Packet1Connect p1 = (Packet1Connect) object;
            			System.out.println(p1.name + " connected");
            		} else if (object instanceof Packet2Line){
            			Packet2Line p2 = (Packet2Line) object;
            			System.out.println("Client said : " + p2.line);
            			
            			Packet2Line response = new Packet2Line();
            			response.line = "You said " + p2.line;
            			connection.sendTCP(response);
            		}
        		}
	        }
	     });
	}
}
