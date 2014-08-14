import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;
import packets.Packet3ClientDisconnect;
import packets.Packet4Chat;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javax.swing.JTextPane;
     
     
    public class Main implements ActionListener {
     
            private static final JFrame frame = new JFrame("Chat Client");
            private static final JTextArea textArea = new JTextArea();
            private static final JTextField textField = new JTextField(25);
            private static final JButton sendButton = new JButton("Send");
            private final Client client;
            private String userName;
            private JTextField textField_1;
           
            public Main() {
            	
            	client = new Client();
            	client.start();
            	
            	try {
					client.connect(5000, "10.24.29.13", 54555, 54777);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Cannot connect to server");
					return;
				}
            	
            	client.getKryo().register(Packet.class);
            	client.getKryo().register(Packet1Connect.class);
            	client.getKryo().register(Packet2Line.class);
        		client.getKryo().register(Packet3ClientDisconnect.class);
        		client.getKryo().register(Packet4Chat.class);
        		
        		client.addListener(new Listener(){
        			public void received(Connection connection, Object object) {
        				if (object instanceof Packet){
        					if (object instanceof Packet2Line) {
        						Packet2Line p2 = (Packet2Line) object;
        						textArea.append(p2.line + " connected. \n");
        					} else if (object instanceof Packet3ClientDisconnect) {
        						Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
        						textArea.append(p3.clientName + " disconnected. \n");
        					} else if (object instanceof Packet4Chat) {
        						Packet4Chat p4 = (Packet4Chat) object;
        						textArea.append(p4.username + ": " + p4.message + "\n");
        					}
        				}
        			}
        		});
            	
            	userName = JOptionPane.showInputDialog("Please give us a user name :");

            	Packet1Connect p1 = new Packet1Connect();
            	p1.username = userName;
            	client.sendTCP(p1);
            	            	
                frame.setSize(512, 385);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle(p1.username + " Connected !!");
               
                Panel p = new Panel();
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                sendButton.setBounds(398, 329, 65, 23);
                sendButton.addActionListener(this);
                p.setLayout(null);
                textArea.setEditable(false);
                JScrollPane areaScrollPane = new JScrollPane(textArea);
                areaScrollPane.setBounds(33, 73, 430, 246);
                areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                areaScrollPane.setPreferredSize(new Dimension(430, 275));
               
                p.add(areaScrollPane);
                textField.setBounds(33, 330, 355, 20);
                p.add(textField);
                p.add(sendButton);
               
                frame.getContentPane().add(p);
                
                textField_1 = new JTextField();
                textField_1.setBounds(33, 11, 127, 20);
                p.add(textField_1);
                textField_1.setColumns(10);
                frame.setVisible(true);
            }
           
            public static void main(String[] args) {
                    new Main();
            }
     
            
            public void actionPerformed(ActionEvent arg0) {
                String message = textField.getText();
                if (!(message.trim().length() == 0)){
                	Packet4Chat p4 = new Packet4Chat();
                	p4.username = userName;
                	p4.message = message;
                	client.sendTCP(p4);
                }
                textField.setText("");
            }
    }

