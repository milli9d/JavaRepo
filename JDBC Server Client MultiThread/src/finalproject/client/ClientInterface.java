package finalproject.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import finalproject.entities.Person;

public class ClientInterface extends JFrame {

	private static final long serialVersionUID = 1L;

//	Static Variables , control UI and Port
	public static final int DEFAULT_PORT = 8001;

	final int AREA_ROWS = 10;
	final int AREA_COLUMNS = 40;

//	Swing Containers
	private JPanel dbPanel, controlPanel, consolePanel;
	private JLabel dbName, dbStatus, serverStatus, lbl, active_port;
	private JTextField first_tf, last_tf, age_tf, city_tf, id_tf;
	private JTextArea console;
	private JMenuBar menuBar;
	private JButton send, close, query, reset, clear, exit, dbEntry, purge;
	private JComboBox peopleSelect;
	private JFileChooser jFileChooser;

//	Connection containers
	private Socket socket;
	private Connection conn;

//	Local Person Data Array
	private List<Person> persons = new ArrayList<Person>();

//	String Literals
	private String DB_NAME = "";
	private String host = "localhost";
	private String url = "";
	private String url_text = "jdbc:sqlite:";
	private String lspacer = " = = = = = = = = = = = = = = = = = = = = ";
	private String spacer = " = = = = = = = ";
	private String head = lspacer + "Client Console" + lspacer;
	private String queryHead = spacer + "Name" + spacer + "Age" + spacer + "City = = = = = Sent" + spacer + "ID = =";

//	Class Variables
	private int port;

	/**
	 * Default Constructor
	 */
	public ClientInterface() {
		this(DEFAULT_PORT);
	}

	/**
	 * Create Client UI with port in parameter
	 * 
	 * @param port
	 */
	public ClientInterface(int port) {
		this.port = port;
		this.createUI();
	}

	/**
	 * Create Client UI
	 */
	private void createUI() {
//		Frame Properties
		this.setTitle("Client UI");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setBounds(1000, 200, 510, 700);
		Container c = this.getContentPane();
		this.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
//		Create and add Panels
		createMenu();
		createDBPanel();
		createControlPanel();
		createConsole();
//		Set visibility
		this.setVisible(true);
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
	 * Create DB Diagnostic Panel
	 */
	private void createDBPanel() {

		dbPanel = new JPanel();

//		Panel Properties
		dbPanel.setBounds(0, 0, 400, 400);
		GridLayout lay = new GridLayout(4, 2);
		lay.setHgap(10);
		lay.setVgap(10);
		dbPanel.setLayout(lay);
		dbPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

//		Component Containers
		lbl = new JLabel("DB Name = ");
		dbPanel.add(lbl);

		dbName = new JLabel("< Empty >");
		dbPanel.add(dbName);

		lbl = new JLabel("DB Status = ");
		dbPanel.add(lbl);

		dbStatus = new JLabel("< Not Connected >");
		dbPanel.add(dbStatus);

		lbl = new JLabel("Server Status = ");
		dbPanel.add(lbl);

		serverStatus = new JLabel("< Not Connected >");
		dbPanel.add(serverStatus);

		lbl = new JLabel("Active Port = ");
		dbPanel.add(lbl);

		active_port = new JLabel(Integer.toString(this.port));
		dbPanel.add(active_port);

//		Add to parent
		this.add(dbPanel);
	}

	/**
	 * Create JPanel to control the interface for Client GUI
	 * 
	 * @return
	 */
	public void createControlPanel() {

//		Use separate Panel for comboBox for Better Layout
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new GridLayout());

//		Add Combo Box and set it disabled before DB is connected, also fill Empty item
		peopleSelect = new JComboBox();
		clearComboBox();
		comboPanel.add(peopleSelect);

		this.add(comboPanel);

//		Control Buttons Panel
		controlPanel = new JPanel();

//		Panel Properties
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		GridLayout lay = new GridLayout(3, 2);
		lay.setHgap(20);
		lay.setVgap(5);
		controlPanel.setLayout(lay);

//		Container Components
		JButton open = new JButton("Open Connection");
		open.addActionListener(e -> openConn());
		controlPanel.add(open);

		close = new JButton("Close Connection");
		close.addActionListener(e -> closeConn());
		controlPanel.add(close);
		close.setEnabled(false);

		send = new JButton("Send Data");
		send.addActionListener(new SendButtonListener());
		controlPanel.add(send);
		send.setEnabled(false);

		query = new JButton("Query DB Data");
		query.addActionListener(new QueryButtonListener());
		controlPanel.add(query);
		query.setEnabled(false);

		reset = new JButton("Reset Sent");
		reset.addActionListener(e -> resetSent());
		controlPanel.add(reset);
		reset.setEnabled(false);

		dbEntry = new JButton("DB Entry");
		dbEntry.addActionListener(e -> entryFrame());
		controlPanel.add(dbEntry);
		dbEntry.setEnabled(false);

		clear = new JButton("Clear Console");
		clear.addActionListener(e -> console.setText(head));
		controlPanel.add(clear);

		purge = new JButton("PURGE DB");
		purge.setEnabled(false);
		purge.addActionListener(e -> clearDB());
		controlPanel.add(purge);

		exit = new JButton("Exit");
		exit.addActionListener(e -> System.exit(0));
		controlPanel.add(exit);

		controlPanel.setVisible(true);

//		Add to parent container
		this.add(controlPanel);
	}

	/**
	 * Create New Entry Frame
	 */
	private void entryFrame() {

		JFrame entry = new JFrame();
		entry.setBounds(600, 200, 300, 200);
		Container c = entry.getContentPane();
		entry.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		JPanel entryPanel = new JPanel();
		entryPanel.setBounds(0, 0, 200, 200);

		entryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		entryPanel.setLayout(new GridLayout(5, 2));

		JLabel lbl;

		lbl = new JLabel("First Name");
		entryPanel.add(lbl);

		first_tf = new JTextField();
		entryPanel.add(first_tf);

		lbl = new JLabel("Last Name");
		entryPanel.add(lbl);

		last_tf = new JTextField();
		entryPanel.add(last_tf);

		lbl = new JLabel("Age");
		entryPanel.add(lbl);

		age_tf = new JTextField();
		entryPanel.add(age_tf);

		lbl = new JLabel("City");
		entryPanel.add(lbl);

		city_tf = new JTextField();
		entryPanel.add(city_tf);

		lbl = new JLabel("ID");
		entryPanel.add(lbl);

		id_tf = new JTextField();
		entryPanel.add(id_tf);

		entry.add(entryPanel);

		JPanel buttonsPane = new JPanel();
		buttonsPane.setLayout(new GridLayout(1, 4));

		JButton enter = new JButton("Insert");
		enter.addActionListener(e -> addtoDB());
		buttonsPane.add(enter);

		JButton exit = new JButton("Exit");
		exit.addActionListener(e -> entry.setVisible(false));
		buttonsPane.add(exit);

		entry.add(buttonsPane);
		entry.setVisible(true);
	}

	/**
	 * Process Person Object and parse to DB
	 * 
	 * @param person
	 */
	private void addtoDB() {
//		Check if entry text fields are incomplete
		if (last_tf.getText().trim().isEmpty() || first_tf.getText().trim().isEmpty()
				|| age_tf.getText().trim().isEmpty() || id_tf.getText().trim().isEmpty()
				|| city_tf.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please Enter All Values");
		} else {
//			Create new Person Object
			Person person = new Person();
//			Set person properties
			person.setFirstName(first_tf.getText());
			person.setLastName(last_tf.getText());
			person.setCity(city_tf.getText());
			person.setAge(Integer.parseInt(age_tf.getText()));
			person.setId(Integer.parseInt(id_tf.getText()));
			person.setSent(false);
//			Clear Text Fields
			last_tf.setText("");
			first_tf.setText("");
			age_tf.setText("");
			city_tf.setText("");
			id_tf.setText("");
//			Display Log Message
			log("INSTR : ADD TO DB\n" + person);
//			Add to Local Data
			persons.add(person);

			// Generate SQL Statement
			String s = "INSERT INTO people(first,last,age,city,sent,id) VALUES (?,?,?,?,?,?)";
			PreparedStatement stmt;
			try {

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
				refreshComboBox();
				log("OUT: SUCCESS");

			} catch (SQLException e) {
				log("OUT : FAILED Check Duplicates/Wrong Entry");
			}
		}
	}

	/**
	 * Resets the Combo Box
	 */
	private void clearComboBox() {
		peopleSelect.removeAllItems();
		peopleSelect.addItem("< Empty >");
		peopleSelect.setEnabled(false);
	}

	/**
	 * Close connection to server
	 */
	private void closeConn() {
		log("INSTR: Close Server Connection");
		try {
//			Release Socket and update UI
			socket.close();
			serverStatus.setText("< Not Connected >");
			send.setEnabled(false);
			close.setEnabled(false);
			log("OUT: Closed Successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log("OUT: Unsuccessful Connection Refused!");
//			e.printStackTrace();
		} catch (NullPointerException e) {
			log("OUT: No Open Connection");
		}
		try {
			refreshComboBox();
		} catch (SQLException e) {
			log("DB Connection Error " + e.getMessage());

//			e.printStackTrace();
		}
		serverStatus.setText("< Not Connected >");

	}

	/**
	 * Connect to Client Database
	 * 
	 * @param url
	 */
	private void connectToDB(String url) {
//		Console Message
		log("INSTR: Connect to DB :" + DB_NAME);
		try {
//			Open Connection
			conn = DriverManager.getConnection(url);
			dbStatus.setText("< Connected >");
			purge.setEnabled(true);
			query.setEnabled(true);
			reset.setEnabled(true);
			dbEntry.setEnabled(true);
			send.setEnabled(true);
			close.setEnabled(true);
			log("OUT: Success\nConnected to DB = " + DB_NAME);

		} catch (SQLException e) {
			log("OUT: Unsuccessful ,  DB Not Connected");
			dbStatus.setText("< Not Connected >");
			send.setEnabled(false);
			close.setEnabled(false);
		}

	}

	/**
	 * Create Log Console
	 */
	private void createConsole() {

		consolePanel = new JPanel();

		consolePanel.setLayout(new BorderLayout());

		console = new JTextArea(AREA_ROWS, AREA_COLUMNS);
		console.setEditable(false);
		console.setSize(400, 400);
		console.setText(head);
		consolePanel.add(console);
		consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);

		this.add(consolePanel);

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
	 * Create File Menu in MenuBar for Client Interface
	 * 
	 * @return JMenu File Menu
	 */
	private JMenu createFileMenu() {

		JMenu menu = new JMenu("File");

		menu.add(createFileOpenItem());
		menu.add(createFileExitItem());

		return menu;
	}

	/**
	 * File Open Item Creator , implements File Chooser
	 * 
	 * @return
	 */
	private JMenuItem createFileOpenItem() {
		JMenuItem item = new JMenuItem("Open DB");
		class OpenDBListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent event) {
				url = url_text;
				DB_NAME = "";
				jFileChooser = new JFileChooser(".");
				int returnVal = jFileChooser.showOpenDialog(getParent());

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					System.out.println(
							"You chose to open this file: " + jFileChooser.getSelectedFile().getAbsolutePath());

					String dbFileName = jFileChooser.getSelectedFile().getAbsolutePath();
					url += dbFileName;
					try {
						DB_NAME = dbFileName.substring(dbFileName.lastIndexOf("\\") + 1);
						dbName.setText(DB_NAME);
						connectToDB(url);
						peopleSelect.setEnabled(true);
						refreshComboBox();
					} catch (Exception e) {
						log("Error connection to DB: " + e.getMessage());
						dbStatus.setText("< Not Connected >");
						send.setEnabled(false);
						close.setEnabled(false);
						query.setEnabled(false);
						purge.setEnabled(false);
						dbEntry.setEnabled(false);
						reset.setEnabled(false);
//						e.printStackTrace();
					}

				}
			}
		}
		item.addActionListener(new OpenDBListener());
		return item;
	}

	/**
	 * Fill Local Data List and populate ComboBox
	 * 
	 * @return
	 * @throws SQLException
	 */
	private List<ComboBoxItem> getNames() throws SQLException {

//		Clear Local Data Array
		persons.clear();

		List<ComboBoxItem> arr = new ArrayList<ComboBoxItem>();

		conn = DriverManager.getConnection(url);

		String s = "SELECT * From People Where Sent = 0";
		PreparedStatement st = conn.prepareStatement(s);
//		Process ResultSet , fill into persons list
		ResultSet rset = st.executeQuery();
		while (rset.next()) {
			Person temp = new Person();

			temp.setFirstName(rset.getString(1));
			temp.setLastName(rset.getString(2));
			temp.setAge(rset.getInt(3));
			temp.setCity(rset.getString(4));
			temp.setSent(rset.getBoolean(5));
			temp.setId(rset.getInt(6));

			persons.add(temp);
		}
//		Fill Combo Box using Local Data
		for (Person i : persons) {
			arr.add(new ComboBoxItem(i.getId(), i.getFirstName() + " " + i.getLastName()));
		}
		return arr;
	}

	/**
	 * Logs String to Console , functions like println()
	 * 
	 * @param text
	 */
	private void log(String text) {
		console.append("\n\n" + text);
		console.setCaretPosition((console.getText().length()));
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
	 * Open Connection to Server
	 */
	private void openConn() {
		log("INSTR: Open Server Connection");

		try {
			socket = new Socket(host, this.port);
			serverStatus.setText("< Connected >");
			send.setEnabled(true);
			InetAddress inet = socket.getInetAddress();
			close.setEnabled(true);
			String text = "Connected to Host = " + inet.getHostAddress();
			log(text);
			refreshComboBox();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			log("OUT: Host Not Found!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log("OUT: Connection Refused!");
//			e.printStackTrace();
		} catch (SQLException e) {
			log("OUT: SERVER KICKED CLIENT ,DB NOT CONNECTED!");
			try {
				socket.close();
			} catch (IOException e1) {
			}
			close.setEnabled(false);
			send.setEnabled(false);
			reset.setEnabled(false);
			serverStatus.setText("< Not Connected >");
			JOptionPane.showMessageDialog(this, "Connect DB from File Menu");
//			e.printStackTrace();
		}
	}

	/**
	 * Refresh Entries in ComboBox
	 * 
	 * @throws SQLException
	 */
	private void refreshComboBox() throws SQLException {
		clearComboBox();
		List<ComboBoxItem> l = getNames();
		if (!l.isEmpty()) {
			peopleSelect.setModel(new DefaultComboBoxModel(l.toArray()));
			if (serverStatus.getText() == "< Connected >") {
				send.setEnabled(true);
				peopleSelect.setEnabled(true);
			}
		} else {
			send.setEnabled(false);
			peopleSelect.setEnabled(false);

		}
	}

	public static void main(String[] args) {
		ClientInterface ci = new ClientInterface();
		ci.setVisible(true);
	}

	/**
	 * ComboBox Item to Link Name and ID
	 * 
	 * @author Milind Singh
	 *
	 */
	class ComboBoxItem {
		private int id;
		private String name;

		public ComboBoxItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	/**
	 * Query Button Action Listener , SELECT * From People
	 * 
	 * @author Milind Singh
	 *
	 */
	class QueryButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			log("INSTR: Query All Data");
			try {

				PreparedStatement all = conn.prepareStatement("SELECT * FROM People");

				ResultSet rset;

				rset = all.executeQuery();

				ResultSetMetaData rsmd = rset.getMetaData();

				int numColumns = rsmd.getColumnCount();

				String rowString = "";
				String QueryString = "";
				while (rset.next()) {
					for (int i = 1; i <= numColumns; i++) {
						Object o = rset.getObject(i);
						rowString += o.toString() + "\t";
					}
					rowString += "\n";
					QueryString += rowString;
					rowString = "";
				}
				log("OUT: SUCCESS");
				log(queryHead);
				log(QueryString);
			} catch (SQLException e) {
				log("OUT: FAILED");
//				e.printStackTrace();
			}
		}
	}

	/**
	 * Reset sent for All Entries in DB and local DB
	 */
	void resetSent() {

		String s = "UPDATE People SET sent = 0";

		try {
			conn = DriverManager.getConnection(url);
			PreparedStatement stmt = conn.prepareStatement(s);
			stmt.execute();
			refreshComboBox();
		} catch (SQLException e) {
			log("RESET FAILED");
//			e.printStackTrace();
		}
	}

	class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (peopleSelect.getSelectedItem() != "< Empty >") {
				try {
//					Buffered Reader for Input
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//					ComboBoxItem to transfer selected item
					ComboBoxItem personEntry = (ComboBoxItem) peopleSelect.getSelectedItem();

					int result = 0;

//					Find person object from Local Data
					Person person = null;
					for (Person i : persons) {
						if (i.getId() == personEntry.getId()) {
							person = i;
						}
					}
//					When Person is found in DB
					if (person != null) {
//					Create Output Stream and write Person Object
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject(person);
						out.flush();
					}

//					Wait for Input from Server
					result = br.read();
//					Check Result
					if (result == 1) {
						log("TRANSFER SUCCESS");

						// Update sent column in Client.db
						String s = "UPDATE People SET sent = 1 where id = ?";
						conn = DriverManager.getConnection(url);
						PreparedStatement stmt = conn.prepareStatement(s);
						stmt.setInt(1, person.getId());
						stmt.execute();
						refreshComboBox();
					} else {
						log("TRANSFER FAIL");
					}
				} catch (IOException e1) {
					openConn();
					this.actionPerformed(e);
//					e1.printStackTrace();
				} catch (SQLException e2) {
					log("FAILED " + e2.getMessage());
//					e2.printStackTrace();
				}

			}
		}

	}
}
