import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class Main {
	
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
	    Client client = new Client();
	    client.start();
	    try {
			client.connect(5000, "127.0.0.1", 54555, 54777);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The server is not started. Can not connect. ", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	    
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
        		if (object instanceof Packet){
        			if (object instanceof Packet2Line){
        				Packet2Line p2 = (Packet2Line) object;
        				System.out.println(p2.line);
        			}
        		}
	        }
	     });
	    
	    client.getKryo().register(Packet.class);
	    client.getKryo().register(Packet1Connect.class);
	    client.getKryo().register(Packet2Line.class);
	    
	    Packet1Connect p1 = new Packet1Connect();
	    p1.name = "tsili852";
	    client.sendTCP(p1);
	    
	    
	    while(client.isConnected()) {
	    	String line = scanner.nextLine();
		    
		    Packet2Line p2 = new Packet2Line();
		    p2.line = line;
		    
		    if (line.equals("Exit"))
		    	break;
		  
		    client.sendTCP(p2);
	    }
	}

}
