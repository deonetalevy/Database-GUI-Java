/*Name: Deone'Ta Levy
  Course: CNT 4714 Summer 2014
  Two-Tier Client-Server Application Development With MySQL
  Due Date: June 24, 2014
*/

import java.sql.*;

import javax.swing.JFrame;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;


//Create Database GUI class
public class DatabaseGui {

	private static JFrame databaseFrame; //JFrame variable
	private JTextField userName; //Username field
	private JPasswordField passwordField; //Password field
	private static JTable commandResultTable; //Table containing DB command results
	
	Object[][] data;
	String[] columnNames;	
	Connection connection;
	Statement statement;
	
	private String driver; //Name of driver
	private String databaseAddress; //URL of the database
	private String uName; //User Name
	private String pword; //Password
	private String input; //User Input
	
	
	//Start the program
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")//annoying warning
					DatabaseGui window;
					window = new DatabaseGui();
					DatabaseGui.databaseFrame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
						}
				}
			});
	}

	//Create GUI
	public DatabaseGui()
	{
		initialize();
	}


	//Create values and objects in the  JFrame
	private void initialize()
	{
		databaseFrame = new JFrame("SQL Client GUI - (DTL)");
		databaseFrame.setBounds(100, 100, 825, 478);
		databaseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		databaseFrame.getContentPane().setLayout(null);
		
		JLabel databaseInformationLabel = new JLabel("Enter Database Information");
		databaseInformationLabel.setBounds(10, 11, 156, 14);
		databaseFrame.getContentPane().add(databaseInformationLabel);
		
		JLabel driverLabel = new JLabel("JDBC Driver");
		driverLabel.setBounds(10, 36, 75, 14);
		databaseFrame.getContentPane().add(driverLabel);
		
		JLabel addressLabel = new JLabel("Database URL");
		addressLabel.setBounds(10, 61, 80, 14);
		databaseFrame.getContentPane().add(addressLabel);
		
		JLabel userNameLabel = new JLabel("User Name");
		userNameLabel.setBounds(10, 86, 80, 14);
		databaseFrame.getContentPane().add(userNameLabel);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(10, 111, 75, 14);
		databaseFrame.getContentPane().add(passwordLabel);
		
		//Create dropdown boxes and fields
		final JComboBox<String> driverSelection = new JComboBox<String>();
		driverSelection.addItem("com.mysql.jdbc.Driver");
		driverSelection.addItem("com.idbm.db2.jdbc.net.DB2Driver");
		driverSelection.addItem("oracle.jdbc.driver.OracleDriver");
		driverSelection.addItem("com.jdbc.odbc.jdbcOdbcDriver");
		driverSelection.setBounds(95, 33, 344, 20);
		databaseFrame.getContentPane().add(driverSelection);
		
		final JComboBox<String> URLSelection = new JComboBox<String>();
		URLSelection.addItem("jdbc:mysql://localhost:3310/project3");
		URLSelection.addItem("jdbc:mysql://localhost:3310/test");
		URLSelection.addItem("jdbc:mysql://localhost:3310/unreal");
		URLSelection.setBounds(95, 58, 344, 20);
		databaseFrame.getContentPane().add(URLSelection);
		
		
		//Create User Name and password fields
		userName = new JTextField();
		userName.setBounds(95, 83, 344, 20);
		databaseFrame.getContentPane().add(userName);
		userName.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(95, 108, 344, 20);
		databaseFrame.getContentPane().add(passwordField);
		
		
		//Create area where SQL commands will be entered
		final JEditorPane commandPane = new JEditorPane();
		commandPane.setBounds(449, 30, 350, 125);
		databaseFrame.getContentPane().add(commandPane);
		
		JLabel lblEnterAnSql = new JLabel("Enter an SQL Command");
		lblEnterAnSql.setBounds(449, 11, 143, 14);
		databaseFrame.getContentPane().add(lblEnterAnSql);
		
		
		//Create execute, connect, and clear buttons
		JButton executeButton = new JButton("Execute SQL Command");
		executeButton.setBounds(629, 166, 170, 23);
		executeButton.setBackground(Color.GREEN);
		databaseFrame.getContentPane().add(executeButton);
		
		JButton connectButton = new JButton("Connect to Database");
		connectButton.setBounds(269, 166, 170, 23);
		connectButton.setBackground(Color.BLUE);
		connectButton.setForeground(Color.white);
		databaseFrame.getContentPane().add(connectButton);
		
		JButton clearCommandButton = new JButton("Clear Command");
		clearCommandButton.setBounds(449, 166, 170, 23);
		databaseFrame.getContentPane().add(clearCommandButton);
		
		
		//Label for execution result
		JLabel executionResultLabel = new JLabel("SQL Execution Result");
		executionResultLabel.setBounds(10, 210, 170, 14);
		databaseFrame.getContentPane().add(executionResultLabel);
		
		
		//Button to clear results
		final JButton clearResultButton = new JButton("Clear Result Window");
		clearResultButton.setEnabled(false);
		clearResultButton.setBounds(10, 402, 170, 23);
		clearResultButton.setBackground(Color.YELLOW);
		databaseFrame.getContentPane().add(clearResultButton);
		
		//Connection Status label
		final JLabel connectionStatus = new JLabel("No Connection Now");
		connectionStatus.setForeground(Color.RED);
		connectionStatus.setBounds(10, 190, 605, 14);
		databaseFrame.getContentPane().add(connectionStatus);

		
		
		//Create Listeners
		clearCommandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commandPane.setText(null); //Clears SQL command pane
				}
			});
		
		clearResultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commandResultTable.setModel(new DefaultTableModel()); //Clears table result
				clearResultButton.setEnabled(false);
				}
			});
		
		executeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					input = commandPane.getText();
					ResultSet queryReturn = statement.executeQuery(input);
					ResultSetMetaData metaData = queryReturn.getMetaData();
					int numColumns = metaData.getColumnCount();
					data = new Object[100][numColumns];
					columnNames	= new String[numColumns];
					int j = 0;
					
					//Set column names
					for (int i = 1; i <= numColumns; i++)
					{
						columnNames[i-1] = metaData.getColumnName(i);
					}
					
					//fill the array
					while (queryReturn.next())
					{
						for (int i = 1; i <= numColumns; i++)
						
						{data[j][i-1] = queryReturn.getObject(i);}
						
						j++;
					}
					
					//Display results
					commandResultTable = new JTable(data, columnNames);
					commandResultTable.setFillsViewportHeight(true);
					commandResultTable.setBorder(new EmptyBorder(6, 0, 0, 0));
				
					final JScrollPane scrollPane = new JScrollPane(commandResultTable);
					JPanel panel = new JPanel();
					scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
					scrollPane.setBorder(new EmptyBorder(6,0,0,0));
					scrollPane.setBounds(10,205,789,196);
					panel.add(scrollPane);
					databaseFrame.getContentPane().add(scrollPane);
					
					clearResultButton.setEnabled(true);
				}
				
				//error handling
				catch (SQLException e) {
					if (e.getMessage().contains("Query"))
					{
						try {
							statement.executeUpdate(input);
							clearResultButton.setEnabled(true);
						}
						
						//Display error message
						catch (SQLException a) { showErrorMessage(a.getMessage()); }
					}else {
						showErrorMessage(e.getMessage());
					}
				}
				catch (NullPointerException e)
				{
					showErrorMessage("Invalid Query \nTry again");
				}
			}
		});
		
		connectButton.addActionListener(new ActionListener()
		{
			@SuppressWarnings("deprecation")//annoying warning
			public void actionPerformed(ActionEvent arg0)
			{
				driver	= (String)driverSelection.getSelectedItem();
				databaseAddress	= (String)URLSelection.getSelectedItem();
				uName	= userName.getText();
				pword	= passwordField.getText();
				try
				{
					Class.forName(driver);
					connection	= DriverManager.getConnection(databaseAddress, uName, pword);
					statement	= connection.createStatement();
					connectionStatus.setText("Connected to " + databaseAddress + " as " + uName);
					
				}
				catch (ClassNotFoundException e){showErrorMessage("ERROR CLASS NOT FOUND EXCEPTION");}
				catch (SQLException e){showErrorMessage("Error! Check Username and Password and try again.");}
			}
		});
		
		
	}
	
	//show error message
	private static void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}
}
