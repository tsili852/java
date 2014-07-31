import java.io.IOException;
import java.util.HashMap;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;
import packets.Packet3ClientDisconnect;
import packets.Packet4Chat;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class main {

	private static HashMap<String, Connection> clients = new HashMap<String, Connection>();
	
	public static void main(String[] args) {
		final Server server = new Server();
		server.start();
		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.getKryo().register(Packet.class);
		server.getKryo().register(Packet1Connect.class);
		server.getKryo().register(Packet2Line.class);
		server.getKryo().register(Packet3ClientDisconnect.class);
		server.getKryo().register(Packet4Chat.class);
		
	    server.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
        		if (object instanceof Packet) {
        			if (object instanceof Packet1Connect) {
            			Packet1Connect p1 = (Packet1Connect) object;
            			clients.put(p1.username, connection);
            			Packet2Line p2 = new Packet2Line();
            			p2.line = p1.username;
            			server.sendToAllExceptTCP(connection.getID(), p2);
            		} else if (object instanceof Packet3ClientDisconnect){
            			Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
            			clients.remove(p3.clientName);
            			server.sendToAllExceptTCP(clients.get(p3.clientName).getID(), p3);
            		} else if (object instanceof Packet4Chat){
            			Packet4Chat p4 = (Packet4Chat) object;
            			server.sendToAllTCP(p4);
            		}
        		}
	        }
	     });
	}
}
