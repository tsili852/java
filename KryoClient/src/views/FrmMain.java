package views;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;

import com.esotericsoftware.kryonet.Client;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class FrmMain extends JFrame{

	private static final long serialVersionUID = 1L;
	static FrmMain form;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JButton btnConnect;
	private JTextField txtServersAddress;
	private JButton btnSend;
    private static Client client;
    private String userName;
    private String serverAddress;

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
					
	            	client = new Client();
	            	client.start();
					
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
		setBounds(100, 100, 475, 431);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane roomScrollPane = new JScrollPane();
		roomScrollPane.setBounds(10, 42, 447, 293);
		contentPane.add(roomScrollPane);
		
		JTextArea roomTextArea = new JTextArea();
		roomScrollPane.setViewportView(roomTextArea);
		
		JLabel lblServersAddress = new JLabel("Server's Address");
		lblServersAddress.setBounds(10, 11, 81, 20);
		contentPane.add(lblServersAddress);
		
		txtServersAddress = new JTextField();
		txtServersAddress.setBounds(101, 11, 129, 20);
		contentPane.add(txtServersAddress);
		txtServersAddress.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(240, 10, 91, 23);
		contentPane.add(btnConnect);
		
		txtMessage = new JTextField();
		txtMessage.setBounds(10, 341, 249, 20);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(274, 340, 91, 23);
		contentPane.add(btnSend);

	}
	
	private void createEvents() {
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (!(txtServersAddress.getText().trim().length() == 0)) {
					serverAddress = txtServersAddress.getText().trim();
					
	            	try {
						client.connect(5000, serverAddress, 54555, 54777);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "Cannot connect to server");
						return;
					}
				} else {
					JOptionPane.showMessageDialog(null, "Fill in the server's address", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
