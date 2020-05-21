package finalproject.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import finalproject.entities.Person;

/**
 * Server UI , uses SQLite Driver for JDBC and JAVA FX Swing AWT
 * 
 * @author Milind Singh
 *
 */
public class Server extends JFrame implements Runnable {

//	Static Variables

//	TODO Make UI to change ports
	// Port for Establishing Connection
	public static final int DEFAULT_PORT = 8001;

	// Set Main Window Sizes
	private static final int FRAME_WIDTH = 510;
	private static final int FRAME_HEIGHT = 700;

	// Set Console Size
	final int AREA_ROWS = 10;
	final int AREA_COLUMNS = 40;

	// Store Active Port and no of Clients
	private int activePort, clientNo;

	// Connection Containers
	private Connection conn;
	private ServerSocket serverSocket;

	// UI Containers
	private JTextArea console = new JTextArea(AREA_ROWS, AREA_COLUMNS);
	private JMenuBar menuBar;
	private JPanel dbPanel, consolePanel;
	private JLabel lbl, dbName, dbStatus, port;

	// String Literals for UI
	private String lspacer = " = = = = = = = = = = = = = = = = = = = = ";
	private String spacer = " = = = = = = = ";
	private String url = "jdbc:sqlite:server.db";
	private String head = lspacer + "Server Console" + lspacer;
	private String queryHead = spacer + "Name" + spacer + "Age" + spacer + "City = = = = = Sent" + spacer + "ID = =";

	/**
	 * Default Constructor for Server UI
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	public Server() throws IOException, SQLException {
		this(DEFAULT_PORT, "server.db");
	}

	/**
	 * Open server on dbFile ,Constructor.
	 * 
	 * @param dbFile
	 * @throws IOException
	 * @throws SQLException
	 */
	public Server(String dbFile) throws IOException, SQLException {
		this(DEFAULT_PORT, dbFile);
	}

	/**
	 * Open Server on Port and dbFile , Constructor
	 * 
	 * @param port
	 * @param dbFile
	 * @throws IOException
	 * @throws SQLException
	 */
	public Server(int port, String dbFile) throws IOException, SQLException {

//		Log console
		console.append("\nNew Server Thread Started");
//		Set Members
		this.activePort = port;
		this.clientNo = 0;
		this.setSize(Server.FRAME_WIDTH, Server.FRAME_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		Create UI
		createUI();

//		Establish Connection to Default DB , server.db
		connectToDB();

//		Make and start Main Server Thread
		Thread t = new Thread(this);
		t.start();

	}

	/**
	 * Create Server UI using JavaFX Swing
	 */
	private void createUI() {

		this.setTitle("JDBC Server Multi-Thread");
		// Set Close button operation
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Container for Box Layout
		Container c = this.getContentPane();
		this.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		// Set Sizes
		this.setBounds(400, 200, FRAME_WIDTH, FRAME_HEIGHT);

		// Make Input Panel
		JPanel input = new JPanel();
		// Create and Attach MenuBar
		createMenu();
		this.add(input);

		// Create DB Control and Console Window Panels
		createDBPanel();
		createConsolePanel();

		// Set Visible
		this.setVisible(true);
	}

	/**
	 * Draw Console Panel Container
	 */
	private void createConsolePanel() {

		consolePanel = new JPanel();

		consolePanel.setLayout(new BorderLayout());

		console = new JTextArea(AREA_ROWS, AREA_COLUMNS);
		console.setEditable(false);
		console.setSize(400, 400);
		console.setText(head);
		consolePanel.add(console);
		// Scroll Pane to scroll through TextArea
		consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);

		this.add(consolePanel);
	}

	/**
	 * Create menu bar for GUI
	 */
	private void createMenu() {
		menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		this.setJMenuBar(menuBar);
	}

	/**
	 * Create File Menu in MenuBar for Server Interface
	 * 
	 * @return JMenu File Menu
	 */
	private JMenu createFileMenu() {

		JMenu menu = new JMenu("File");

		menu.add(createFileExitItem());

		return menu;
	}

	/**
	 * Create exit button in Menu Bar
	 * 
	 * @return JMenuItem exit button
	 */
	private JMenuItem createFileExitItem() {
		JMenuItem exit = new JMenuItem("Exit");

		exit.addActionListener(e -> System.exit(0));

		return exit;

	}

	/**
	 * Connect to Client Database
	 * 
	 * @param url
	 */
	private void connectToDB() {
//		Console Message
		log("INSTR: Connect to DB : server.db");
//		Open Connection
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			log("ERROR: SERVER DB DISCONNECTED");
			dbStatus.setText("< Not Connected >");
		}
//		Update UI Elements
		dbStatus.setText("< Connected >");
		dbName.setText("server.db");
		log("OUT: SUCCESS\nConnected to DB = server.db");
	}

	/**
	 * Logs String to Console , functions like println()
	 * 
	 * @param text
	 */
	private void log(String text) {
		console.append("\n\n" + text);
		console.setCaretPosition((int) (console.getText().length()));
	}

	/**
	 * Draw DB Control Panel
	 */
	private void createDBPanel() {

		dbPanel = new JPanel();

//		DB Information Panel
		dbPanel.setBounds(0, 0, 400, 400);
		GridLayout lay = new GridLayout(3, 2);
		lay.setHgap(10);
		lay.setVgap(10);
		dbPanel.setLayout(lay);

		dbPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		lbl = new JLabel("DB Name = ");
		dbPanel.add(lbl);

		dbName = new JLabel("< Empty >");
		dbPanel.add(dbName);

		lbl = new JLabel("DB Status = ");
		dbPanel.add(lbl);

		dbStatus = new JLabel("< Not Connected >");
		dbPanel.add(dbStatus);

		lbl = new JLabel("Active Port = ");
		dbPanel.add(lbl);

		port = new JLabel(activePort + "");
		dbPanel.add(port);

//		New Panel to separate buttons from  Labels layout
		JPanel butPanel = new JPanel();

		GridLayout layou = new GridLayout(1, 4);
		layou.setHgap(20);
		layou.setVgap(5);
		butPanel.setLayout(layou);

		JButton query = new JButton("Query DB");
		query.addActionListener(new QueryButtonListener());
		butPanel.add(query);

		JButton clear = new JButton("Clear");
		clear.addActionListener(e -> console.setText(head));
		butPanel.add(clear);

		JButton reset = new JButton("Purge DB");
		reset.addActionListener(e -> clearDB());
		butPanel.add(reset);

		JButton exit = new JButton("Exit");
		exit.addActionListener(e -> System.exit(0));
		butPanel.add(exit);

		this.add(dbPanel);
		this.add(butPanel);
	}

	/**
	 * Queries all Entries from Connected Database
	 * 
	 * @author Milind Singh
	 *
	 */
	class QueryButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			log("INSTR: QUERY ALL");
			try {
//				String Literals
				String rowString = "";
				String QueryString = "";

//				Generate Prepared Statement	
				PreparedStatement all = conn.prepareStatement("SELECT * FROM People");

//				Get Results
				ResultSet rset = all.executeQuery();

//				Get MetaData to calculate no of entries
				ResultSetMetaData rsmd = rset.getMetaData();
				int numColumns = rsmd.getColumnCount();

				while (rset.next()) {
					for (int i = 1; i <= numColumns; i++) {
						Object o = rset.getObject(i);
						rowString += o.toString() + "\t";
					}
					rowString += "\n";
//					Make Larger String from Individual Row Strings,append
					QueryString += rowString;
//					Clear RowString
					rowString = "";
				}
//				If Query returned data
				if (QueryString != "") {
					log("OUT: SUCCESS");
					log(queryHead);
					log(QueryString);
				}
//				If Query is Empty
				else {
					log("OUT: FAILED DB EMPTY");
				}
			} catch (SQLException e) {
				log("OUT: FAILED TO READ DB");
			}
		}
	}

	/**
	 * Process Person Object and parse to DB
	 * 
	 * @param person
	 */
	private void addtoDB(Person person) throws SQLException {

		log("INSTR : ADD TO DB\n" + person);

		String s = "INSERT INTO people(first,last,age,city,sent,id) VALUES (?,?,?,?,?,?)";
//		Generate SQL Statement
		PreparedStatement stmt;
//		try {

		stmt = conn.prepareStatement(s);

//			Parse Person Object into SQL Statement
		stmt.setString(1, person.getFirstName());
		stmt.setString(2, person.getLastName());
		stmt.setInt(3, person.getAge());
		stmt.setString(4, person.getCity());
		stmt.setBoolean(5, person.isSent());
		stmt.setInt(6, person.getId());

//			Execute update statement
		stmt.executeUpdate();
//		log("OUT: SUCCESS");

//		} catch (SQLException e) {
//			log("OUT : FAILED Check Duplicates/Wrong Entry");

//		}
	}

	/**
	 * Purge All DB Entries on Server
	 */
	void clearDB() {
		try {
			log("INSTR : PURGE DB");
			// Execute SQL Statement
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM People");
			stmt.execute();
			log("OUT : SUCCESS");
		} catch (SQLException e) {
			log("OUT : FAILED PURGE");
		}

	}

	/**
	 * Thread Runnable Section , Start Socket and Keep Listening for Requests
	 */
	@Override
	public void run() {
		log("INSTR: START LISTEN");

//		Open Socket and pass to Inner Class HandleClient
		try {
			serverSocket = new ServerSocket(activePort);

			log("OUT: Listening on Port = " + serverSocket.getLocalPort());

			while (true) {
				// Listen for a new connection request
				Socket socket = serverSocket.accept();

				// Increment clientNo
				clientNo++;
				log("INSTR: NEW CLIENT " + clientNo + "\nTIME =  " + new Date());

				// Find the client's host name, and IP address
				InetAddress inetAddress = socket.getInetAddress();
				log("OUT:Sucess\nCLIENT " + clientNo + "\nHost = " + inetAddress.getHostName() + "\nIP Address = "
						+ inetAddress.getHostAddress());

				// Create and start a new thread for the connection
				new Thread(new HandleAClient(socket, clientNo)).start();
			}
		} catch (IOException ex) {
			System.err.println(ex);
			log("OUT: FAILED Connection already in use");
			JOptionPane.showMessageDialog(this, "Port Already In Use!\n        Exiting", "Error",
					JOptionPane.WARNING_MESSAGE, null);
//			Exit Server if Port is Busy
			System.exit(0);
		}
	}

	/**
	 * Define the thread class for handling new connection
	 * 
	 * @author Milind Singh
	 *
	 */
	class HandleAClient implements Runnable {

//		Connection Container
		private Socket socket;

//		Client ID		
		private int clientNum;

		/** Construct a thread */
		public HandleAClient(Socket socket, int clientNum) {
			this.socket = socket;
			this.clientNum = clientNum;
		}

		/** Run a thread */
		public void run() {
//			Serve Client continously
			ObjectInputStream inputFromClient = null;
			DataOutputStream out = null;
			try {
				while (true) {
//				Person Object to store sent data
					Person person = null;
//				Create Input Stream
					inputFromClient = new ObjectInputStream(socket.getInputStream());
//					Read Object from Input Stream
					person = (Person) inputFromClient.readObject();
//					If object found , write to DB
					if (person != null) {
						try {
							addtoDB(person);
						}
//						SQLException will occur when SQL Transfer Fails , sent FAIL Signal
						catch (SQLException e) {
							out = new DataOutputStream(socket.getOutputStream());
							out.write(0);
							out.flush();
							log("FAILED TRANSFER : Check Duplicates");
							out.close();
//							e.printStackTrace();
						}
//						Will Reach here when Transfer is Successful
						out = new DataOutputStream(socket.getOutputStream());
						out.write(1);
						out.flush();
						log("TRANSFER SUCCESS");
						out.close();
//						Clear temporary Person Object
						person = null;
					}
				}
			} catch (ClassNotFoundException e) {
//					e.printStackTrace();
			} catch (IOException e) {
				try {
					log("OUT: DISCONNECTED CLIENT " + this.clientNum + "\nTime = " + new Date());
//					Close Stream
					inputFromClient.close();
				} catch (IOException | NullPointerException e1) {
				} finally {
//					Close Thread
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public static void main(String[] args) {

		Server sv;
		try {
			sv = new Server("server.db");
			sv.setVisible(true);
		} catch (IOException | SQLException e) {

		}
	}
}