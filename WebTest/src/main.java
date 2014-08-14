import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;
import packets.Packet3ClientDisconnect;
import packets.Packet4Chat;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class main {

	private static HashMap<String, Connection> clients = new HashMap<String, Connection>();
	private static InetAddress ip; 
	
	public static void main(String[] args) {
		final Server server = new Server();
		try {
			ip = Inet4Address.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		SystemTray tray = SystemTray.getSystemTray();
		ImageIcon icon = new ImageIcon("libs/chat-icon.png");
		Image image = icon.getImage();
		TrayIcon trayIcon = new TrayIcon(image, "Chat Server");
		trayIcon.setImageAutoSize(true);
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		trayIcon.displayMessage("Server IP address", ip.getHostAddress(), MessageType.INFO);
		
		System.out.println("Ip : " + ip.getHostAddress());
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
            			p2.numberOfUsers = clients.size();
            			server.sendToAllTCP(p2);
            		} else if (object instanceof Packet3ClientDisconnect){
            			Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
            			//clients.remove(p3.clientName);
            			server.sendToAllExceptTCP(clients.get(p3.clientName).getID(), p3);
            		} else if (object instanceof Packet4Chat){
            			Packet4Chat p4 = (Packet4Chat) object;
            			server.sendToAllTCP(p4);
            		}
        		}
	        }
	        
	        @SuppressWarnings("unchecked")
			public void disconnected (Connection connection) {
	        	Packet3ClientDisconnect p3 = new Packet3ClientDisconnect();
	        	Iterator<Entry<String, Connection>> it = clients.entrySet().iterator();
	        	String userName = " ";
	        	while (it.hasNext()) {
	        		//Map.Entry<String, Connection> pairs = (Map.Entry<String, Connection>)it.next();
	        		Map.Entry<String, Connection> pairs = it.next();
	        		if (pairs.getValue().equals(connection)) {
	        			userName = (String) pairs.getKey();
	        		}
	        	}
	        	if (!userName.equalsIgnoreCase("")) {
	        		p3.clientName = userName;
        			clients.remove(p3.clientName);
	        		server.sendToAllExceptTCP(connection.getID(), p3);
	        	}
	        }
	     });
	}
}
