package views;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import packets.Packet;
import packets.Packet1Connect;
import packets.Packet2Line;
import packets.Packet3ClientDisconnect;
import packets.Packet4Chat;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.awt.Font;

public class FrmMain extends JFrame{

	private static final long serialVersionUID = 1L;
	static FrmMain form;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JButton btnConnect;
	private static JTextField txtServersAddress;
	private JButton btnSend;
    private static Client client;
    private String userName;
    private String serverAddress;
    private static JTextArea roomTextArea;
    private Packet1Connect p1;
    private JTextField txtUserName;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}
					
					form = new FrmMain();
					
					form.setResizable(false);
					form.setVisible(true);
					form.setTitle("Chat with me");
					
	            	client = new Client();
	            	client.start();
	        		
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
	        						roomTextArea.append(p2.line + " connected. \n");
	        					} else if (object instanceof Packet3ClientDisconnect) {
	        						Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
	        						roomTextArea.append(p3.clientName + " disconnected. \n");
	        					} else if (object instanceof Packet4Chat) {
	        						Packet4Chat p4 = (Packet4Chat) object;
	        						roomTextArea.append(p4.username + ": " + p4.message + "\n");
	        					}
	        				}
	        			}
	        		});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FrmMain() {
		initComponents();
		createEvents();

	}

	private void initComponents() {
		//frame = new JFrame();
		setBounds(100, 100, 475, 437);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane roomScrollPane = new JScrollPane();
		roomScrollPane.setBounds(10, 75, 447, 293);
		contentPane.add(roomScrollPane);
		
		roomTextArea = new JTextArea();
		roomTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
		roomTextArea.setWrapStyleWord(true);
		roomTextArea.setLineWrap(true);
		roomScrollPane.setViewportView(roomTextArea);
		
		JLabel lblServersAddress = new JLabel("Server's Address");
		lblServersAddress.setBounds(10, 11, 81, 20);
		contentPane.add(lblServersAddress);
		
		txtServersAddress = new JTextField();
		txtServersAddress.setBounds(101, 11, 129, 20);
		contentPane.add(txtServersAddress);
		txtServersAddress.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(240, 41, 91, 23);
		contentPane.add(btnConnect);
		
		txtMessage = new JTextField();
		txtMessage.setBounds(10, 380, 346, 20);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(366, 379, 91, 23);
		contentPane.add(btnSend);
		
		JLabel lblUserName = new JLabel("Your Name");
		lblUserName.setBounds(39, 42, 52, 20);
		contentPane.add(lblUserName);
		
		txtUserName = new JTextField();
		txtUserName.setColumns(10);
		txtUserName.setBounds(101, 42, 129, 20);
		contentPane.add(txtUserName);

	}
	
	private void createEvents() {
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (!(txtServersAddress.getText().trim().length() == 0) 
						&& !(txtUserName.getText().trim().length() == 0)) {
					
					serverAddress = txtServersAddress.getText().trim();
					
	            	try {
						client.connect(5000, serverAddress, 54555, 54777);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Cannot connect to server");
						return;
					}
	            	
	            	userName = txtUserName.getText().trim();
	            	
	            	p1 = new Packet1Connect(userName);
	            	client.sendTCP(p1);
	            	form.setTitle("Chat with me :) " + userName);
				} else {
					JOptionPane.showMessageDialog(null, "Fill in the server's address and your name", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
			
		
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		        String message = txtMessage.getText();
		        if (!(message.trim().length() == 0)){
		        	Packet4Chat p4 = new Packet4Chat();
		        	p4.username = userName;
		        	p4.message = message;
		        	client.sendTCP(p4);
		        }
		        txtMessage.setText("");
			}
		});
		
	}
}
