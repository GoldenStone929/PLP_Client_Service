package com.scitegic.proxy.examples;
import javax.swing.text.DefaultCaret;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.*;
import com.scitegic.proxy.ComponentDatabase;
import com.scitegic.proxy.ComponentInfo;
import com.scitegic.proxy.ParameterInfo;
import com.scitegic.proxy.Job;
import com.scitegic.proxy.JobResult;
import com.scitegic.proxy.JobStatus;
import com.scitegic.proxy.PipelinePilotServer;
import com.scitegic.proxy.PipelinePilotServerConfig;
import com.scitegic.proxy.XmldbItem;

public class TestGUI {
	
	//static final String EXAMPLE_PROTOCOLS = "Protocols/Examples";
	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/BHT/PLP_for_DataWarrior/";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "for_dev_test/01_Hello_World";
	static final String WHITESPACE = "                                                     ";
	
	// System.getProperty("user.name") ==> "zhengwei.peng", not "zpeng". Need to call UTOT to resolve this. Making a logic link?
	//static final String localOutFolder = "C:/Users/" + System.getProperty("user.name") + "/OneDrive - BlossomHill Therapeutics, Inc/Documents/DW_PLP_Temp_Folder/";
	static final String localInputFolder  = "C:/Users/zpeng/OneDrive - BlossomHill Therapeutics, Inc/Documents/DW_PLP_Temp_Folder/Input";  // DW file(s) to be uploaded to the PLP server
	static final String localOutputFolder = "C:/Users/zpeng/OneDrive - BlossomHill Therapeutics, Inc/Documents/DW_PLP_Temp_Folder/Output"; // protocol output files to be down loaded into this folder
	
	private PipelinePilotServer pp = null;
	private PipelinePilotServerConfig conf = null;
	private Job protocol = null;
	private ComponentDatabase compdb = null;
	private XmldbItem rootFolder = null;

	private JTextArea ResultLabel;  // Declare ResultLabel as an instance variable


	// selected protocol info
	private String selectedProtocolName    = null;
	private XmldbItem selectedXmldbItem    = null;
	private Job               selectedJob  = null;
	private ComponentInfo selectedCmptInfo = null;
	

	
	private JFrame frame;                           // the top GUI container
	private JPanel panelLeft, panelLeftTop, panelLeftMiddle, panelLeftBottom;
	private JPanel panelRight, panelRightTop, panelRightMiddle, panelRightBottom;           // the 2nd-level GUI container

	private JButton buttonLeftTop, buttonLeftBottom, buttonRightTop, buttonRightBottom;        // button is a type of low-level GUI element
	private JLabel labelLeft;                      // label is another type of low-level GUI element

	
	private JLabel labelRight = new JLabel("Show which tree node is clicked bu user.");
	//private JLabel labelRightTop;
	//private JLabel labelRightMiddle;
	//private JLabel labelRightBottom;
	
	private int width  = 50;
	private int height = 200;
	private int shift  = 5;
	

	private int countLeft, countRight;
	
	private JTree myTree;
	DefaultMutableTreeNode rootTreeNode;
	
	private myActionListener myListner = new myActionListener();
	
	private String fileFullPath = null;     // use to pass file selection to parameter JTextField box(es).
	

	
	private void addFolderTreeRecursive(XmldbItem node, DefaultMutableTreeNode treeNode) {
		
		if (node.isFolder()) {
			XmldbItem[] children = node.getChildren();
			
			for (int i = 0; i < children.length; i++) {
				
				XmldbItem childNode = children[i];
				if ( childNode.isFolder() == false ) {
					
				}
				DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode.getName());
				
				treeNode.add(childTreeNode);
				
				addFolderTreeRecursive(childNode, childTreeNode);  // recursive call
				
			}
		} 
	}

	public void addPLPTree() {
		try {
//			pp = new PipelinePilotServer(Login.Global.serverAddress, Login.Global.username, Login.Global.password);
			pp = new PipelinePilotServer("http://10.30.50.184:9944/", "ShawnCui", Login.Global.password);

			conf = pp.getServerConfig();
			compdb = pp.getComponentDatabase();
			rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);

			System.out.println(rootFolder.getName());

			rootTreeNode = new DefaultMutableTreeNode(rootFolder.getName());  // the top treeNode. It will not show up in the JTree
			myTree = new JTree(rootTreeNode);  // create a JTree GUI to interact with user
			myTree.setBackground(Color.WHITE);  // Set background color of the JTree to white

			JScrollPane treeScrollPane = new JScrollPane(myTree);
			treeScrollPane.getViewport().setBackground(Color.WHITE);

			// Set scroll bars' background color to white
			treeScrollPane.getHorizontalScrollBar().setBackground(Color.WHITE);
			treeScrollPane.getVerticalScrollBar().setBackground(Color.WHITE);

			panelLeftMiddle.add(treeScrollPane, BorderLayout.CENTER);

			// Set the panel's background color to white
			panelLeftMiddle.setBackground(Color.WHITE);

			myTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();

					// If a node is selected, clear the ResultLabel
					if (selectedNode != null) {
						ResultLabel.setText("");
					}

					Object nodeInfo = selectedNode.getUserObject();
					listProtocolInfo(nodeInfo.toString());  // report the protocol info on a selected tree
					return;
				}
			});


			addFolderTreeRecursive(rootFolder, rootTreeNode);

			labelRight.setText("Show which tree node is clicked by user.");

			// Refresh the panel to reflect the changes
			panelLeftMiddle.revalidate();
			panelLeftMiddle.repaint();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (protocol != null) {
				try {
					protocol.releaseJob();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}




	private void refreshPLPTree() {    // not working yet
		
		rootTreeNode.removeAllChildren();    // clear the treeNode model
		rootFolder = null;
		
		myTree.removeAll();                  // clear the JTreemyTree.validate();
		myTree = null;
		panelLeftMiddle.removeAll();
		panelLeftMiddle.validate();
		panelLeftMiddle.repaint();
		
		
		try {
		compdb     = pp.getComponentDatabase();      // get the updated PLP component database
		rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);  // get the updated XmldbItem tree model

		
		addFolderTreeRecursive(rootFolder, rootTreeNode);  // re-populate the treeNode model

		// 
		myTree = new JTree(rootTreeNode);
		myTree.addTreeSelectionListener(new TreeSelectionListener() {     // respond to user selection on a tree node

			public void valueChanged(TreeSelectionEvent e) {
		        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();

		    /* if nothing is selected */
		        if (selectedNode == null) return;

		    /* retrieve the node that was selected */
		        Object nodeInfo = selectedNode.getUserObject();
		        listProtocolInfo(nodeInfo.toString());                   // report the protocol info on a selected tree
		        return;
		    }
		});

		panelLeftMiddle.add(myTree);
		
		myTree.validate();
		myTree.repaint();
		
		panelLeftMiddle.revalidate();     // update the panel hosting the JTree
		panelLeftMiddle.repaint();        // 
		
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Remove the job from the server
			if (protocol != null) {
				try {
					//System.out.println("Deleting job " + protocol.getJobId());
					protocol.releaseJob();
				} catch (Exception ex) {
				}
			}
		}
		
	}
	
	private boolean InSkipSet(String name) {
		if (name.equals("DeclareGlobal")) {
			return true;
		} 
		else if (name.equals("__PoolID")) {
			return true;
		}
		else if (name.equals("Http Cookie Handling")) {
			return true;
		}
		else if (name.equals("__NotificationProtocol")) {
			return true;
		}
		else if (name.equals("Reporting Stylesheet")) {
				return true;
		}
		else {
			return false;
		}
			
	}


	public void listProtocolInfo(String protocolName) {

		panelRightTop.removeAll();  // refresh it for each protocol selected by user
		panelRightTop.revalidate();
		panelRightTop.repaint();

		panelRightMiddle.removeAll();  // refresh it for each protocol selected by user
		panelRightMiddle.revalidate();
		panelRightMiddle.repaint();

		try {
			selectedProtocolName = protocolName;
			selectedJob          = pp.createJob(protocolName);
			selectedCmptInfo     = selectedJob.getComponentInfo();

			// For Protocol Name -----------------------------------------------------------
			JLabel nameL = new JLabel("Protocol Name:");
			JTextArea nameLabel = new JTextArea(0, 1);
			nameLabel.setText(protocolName); // Set the text of the label to the protocol name
			nameLabel.setLineWrap(true); // Ensuring words don't get cut off
			nameLabel.setWrapStyleWord(true); // Ensuring words don't get cut off
			nameLabel.setEditable(false); // Ensuring the user can't edit the text
//			nameLabel.setBackground(Color.BLUE); // Ensuring the background is white
			// make the length maximum to 1 line
			nameLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, nameLabel.getPreferredSize().height));
			nameLabel.setBorder(BorderFactory.createLineBorder(Color.cyan));

			// For Description -----------------------------------------------------------
			JLabel descriptionL = new JLabel("Description:");
			JTextArea descriptionLabel = new JTextArea(0, 1);
			descriptionLabel.setText(selectedCmptInfo.getDescription());
			descriptionLabel.setLineWrap(true);
			descriptionLabel.setWrapStyleWord(true); // Ensuring words don't get cut off
			descriptionLabel.setEditable(false); // Ensuring the user can't edit the text
			nameLabel.setWrapStyleWord(true); // Ensuring words don't get cut off
			// init a new color with rgb: R97, G208, B236
//			Color lightBlue = new Color(97, 208, 236);
			descriptionLabel.setBorder(BorderFactory.createLineBorder(Color.cyan));
			descriptionLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));





			// Add the components to a panel with BoxLayout for vertical alignment
			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.PAGE_AXIS));
			namePanel.add(nameL);
			namePanel.add(nameLabel);

			JPanel descriptionPanel = new JPanel();
			descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.PAGE_AXIS));
			descriptionPanel.add(descriptionL);
			descriptionPanel.add(descriptionLabel);

			panelRightTop.add(namePanel);
			panelRightTop.add(descriptionPanel);

			panelRightTop.revalidate();
			panelRightTop.repaint();

			ParameterInfo[] params = selectedCmptInfo.getParameters();

			for (int i=0; i<params.length; ++i) {
				ParameterInfo para = params[i];

				if (InSkipSet(para.getName()) == false) {    // ignore a few irrelevant PLP parameters.
					addParameters(para);
				}
			}

			return;

		} catch (Exception e) {
			selectedProtocolName = null;
			selectedXmldbItem    = null;    // clicked on a non-protocol treeNode. clear selectedProtocol
			selectedJob          = null;
			selectedCmptInfo     = null;

			return;
		}
	}





	public void addParameters(ParameterInfo para) {

		String [] legals = para.getLegalValues();
		String [] values = para.getValue();
		
		if (para.getType().equals("URLType")) {
			// add a file chooser instead
			JLabel    name     = new JLabel(para.getName());
			JTextField fileInput = new JTextField("");
			fileInput.addActionListener(myListner);
			
			panelRightMiddle.add(name);
			panelRightMiddle.add(fileInput);

			
		} else {
			if ( legals == null || legals.length == 0) {   // basic parameters with no legal values predefined. 
				JLabel     name     = new JLabel(para.getName());
				JTextField txtInput = new JTextField("");
				if (values != null && values.length > 0) {
					txtInput.setText(values[0]);                 // assuming one and only value
				}

				panelRightMiddle.add(name);
				panelRightMiddle.add(txtInput);
			
			} else {
				JLabel    name     = new JLabel(para.getName());
				JComboBox combobox = new JComboBox(legals);
				combobox.setSelectedItem(values[0]);           // select the one specified by the server-side
			
				panelRightMiddle.add(name);
				panelRightMiddle.add(combobox);
			}
		}
		
		
		panelRightMiddle.revalidate();
		panelRightMiddle.repaint();
		return;
	}

	private void createTopGUI() {
		//http://10.30.50.184:9944/
		frame = new JFrame();
//		frame.getContentPane().setBackground(Color.WHITE);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(900, 600);
		frame.setVisible(true); // make the frame visible
		frame.setResizable(true); // make the frame resizable
		frame.setMinimumSize(new Dimension(600, 400));
		frame.setTitle("BHT PLP Launcher");
		frame.setLocationRelativeTo(null); // center the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close the frame when the user clicks the close button
		//default color change from light grey to all page white
		frame.getContentPane().setBackground(Color.WHITE);

		frame.setFont(new Font("Courier New", Font.PLAIN, 12));

		// Declare the imageIcon variable
		ImageIcon BHTIcon;
		BHTIcon = new ImageIcon(".\\data\\logo-bht.png");
		java.awt.Image image = BHTIcon.getImage();
		frame.setIconImage(image);



		panelLeft = new JPanel();
		panelLeft.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeft.setLayout(new GridLayout(0, 1));

		panelRight = new JPanel();
		panelRight.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRight.setLayout(new GridLayout(0, 1));
		panelRight.setAutoscrolls(true);


		// Set equal size for the left and right panels
		Dimension panelSize = new Dimension(frame.getWidth() / 2, frame.getHeight());
		panelLeft.setPreferredSize(panelSize);
		panelRight.setPreferredSize(panelSize);

		// Add a border to the left panel for separation
		panelLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));


		createLeftPanel();
		createRightPanel();

		frame.add(panelLeft, BorderLayout.WEST);
		frame.add(panelRight, BorderLayout.EAST);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("My TestGUI");
		frame.pack();
		frame.setVisible(true);
	}




	private void createLeftPanel() {

		panelLeftTop = new JPanel();
		panelLeftTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftTop.setLayout(new BoxLayout(panelLeftTop, BoxLayout.PAGE_AXIS));
		
		panelLeftMiddle = new JPanel();
		panelLeftMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftMiddle.setLayout(new BorderLayout());



		panelLeftBottom = new JPanel();
		panelLeftBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftBottom.setLayout(new BoxLayout(panelLeftBottom, BoxLayout.PAGE_AXIS));
		
		buttonLeftTop = new JButton("Refresh JTree");
		buttonLeftTop.addActionListener(myListner);
		panelLeftTop.add(buttonLeftTop, BorderLayout.PAGE_START);
		
		buttonLeftBottom = new JButton("Show protocol error message (to be implemented)");
		buttonLeftBottom.addActionListener(myListner);
		panelLeftBottom.add(buttonLeftBottom, BorderLayout.PAGE_END);
		
		panelLeft.add(panelLeftTop, BorderLayout.PAGE_START);
		panelLeft.add(new JScrollPane(panelLeftMiddle), BorderLayout.CENTER);
		panelLeft.add(panelLeftBottom, BorderLayout.PAGE_END);
		
		addPLPTree();
		
	}

	private void createRightPanel() {
		panelRightTop = new JPanel();
		panelRightTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightTop.setLayout(new BoxLayout(panelRightTop, BoxLayout.PAGE_AXIS));
//		panelRightTop.setBackground(Color.WHITE);
//		panelRightTop.setOpaque(true); // this is needed to make the background color visible
//		panelRightTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

		panelRightMiddle = new JPanel();
		panelRightMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightMiddle.setLayout(new BoxLayout(panelRightMiddle, BoxLayout.PAGE_AXIS) );

		panelRightBottom = new JPanel();
		panelRightBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightBottom.setLayout(new BoxLayout(panelRightBottom, BoxLayout.PAGE_AXIS));

		panelRight.add(panelRightTop, BorderLayout.PAGE_START);
		panelRight.add(panelRightMiddle, BorderLayout.CENTER);
		panelRight.add(panelRightBottom, BorderLayout.PAGE_END);


		buttonRightTop = new JButton("Button rightTop (PAGE_START)");
		buttonRightTop.addActionListener(myListner);
		panelRightTop.add(buttonRightTop, BorderLayout.PAGE_START);

		buttonRightBottom = new JButton("Submit Job");
		buttonRightBottom.addActionListener(myListner);
		panelRightBottom.add(buttonRightBottom, BorderLayout.PAGE_END);

		// For Result
		JLabel ResultL = new JLabel("Result:");
		panelRightBottom.add(ResultL);
		ResultLabel = new JTextArea(0, 1);  // Initialize ResultLabel
		ResultLabel.setLineWrap(true);
		ResultLabel.setWrapStyleWord(true);
		ResultLabel.setEditable(false);
		ResultLabel.setEditable(false);
		ResultLabel.setBorder(BorderFactory.createLineBorder(Color.cyan));
		ResultLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));

		//center the textarea
		DefaultCaret caret = (DefaultCaret)ResultLabel.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// make sure it is in the middle based on Jframe resize





		panelRightBottom.add(ResultLabel);







	}

public void setProtocolParametersfromGUI() {
		
		ParameterInfo[] params = selectedCmptInfo.getParameters();
		
		int ith = 0;               // index for the parameters
		int jth = 0;               // index for the JTextArea in panelRightMiddle
		
		try {
			
		for (int i=0; i<params.length; ++i) {
			ParameterInfo para = params[i];

			if (InSkipSet(para.getName()) == false) {    // ignore a few irrelevant PLP parameters.
				jth = 2*ith + 1;
				
				//String pType = para.getType();
				
				if ( panelRightMiddle.getComponent(jth) instanceof javax.swing.JTextField) {
					if ( para.getType().equals("URLType") ) {
						File localFile = new File(((JTextField) panelRightMiddle.getComponent(jth) ).getText());
						selectedJob.setInputFileOnClient(para.getName(), localFile);
						System.out.println(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
					}
					else {
						selectedJob.setInputValue(para.getName(), ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
						System.out.println(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
					}
				}
				else if ( panelRightMiddle.getComponent(jth) instanceof javax.swing.JComboBox) {
					selectedJob.setInputValue(para.getName(), ((JComboBox) panelRightMiddle.getComponent(jth) ).getSelectedItem().toString());
					System.out.println(para.getName() + " = "  + ((JComboBox) panelRightMiddle.getComponent(jth) ).getSelectedItem().toString());
				}
				else {
					; // do nothing
				}
				
				ith++;  // next valid parameter
			}
		} // for loop
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
		
	}
	
	
	private void submitJob() {
		System.out.println("I am inside submitJob(). ");
		

			// prepare job parameters
			// selectedJob.setInputFileOnClient("Source", localFile);              // file on the client-side
			// selectedJob.setInputValue("Source", "data/Tables/imports-85.txt");  // file on the server-side
			//
			// selectedJob.setInputValue("X Property", "Highwaympg");              // set parameter value pair, string
			// selectedJob.setInputValue("NumOfRun:, 10);                          // ineger type
			// selectedJob.setInputValue("selectedChoices", String[] values);
			//
			
			System.out.println("Submitting a job " + selectedProtocolName);
			
			//selectedJob.setInputValue("Project", "BH-00");
			setProtocolParametersfromGUI();
			
			try {
				
			selectedJob.validate();

			JobResult prr = selectedJob.runAndPoll();
			JobStatus status = selectedJob.getStatus();
			
			if (JobStatus.Finished.equals(status)) {
				// get returned global variables as webport result.
				String message = prr.getWebExportResult("Message");
//				System.out.println("returned Message is " + message);
				ResultLabel.append("returned Message is " + message + "\n");
				
				// get results as files.
				String[] results = prr.getResultFiles();

				if ((results != null) && (results.length > 0)) {
					// print all result files
					System.out.println("Getting job results:");
					for (int i = 0; i < results.length; i++) {
						System.out.println(results[i]);                          // remote file URL
						
						String localFileName = localOutputFolder + extractFileName(results[i]);
						
						File localResultFile = new File(localFileName);
						pp.getRemoteFileManager().downloadFile(results[0], localResultFile);
						
						System.out.print("Writing result file to ");
						System.out.println(localFileName);
						
						//System.out.println(localResultFile.getAbsolutePath());  // local file full path
					}
					System.out.println();
				} else {
//					System.out.println("No job results");
					//print out in Jframe
					ResultLabel.append("No job results" + "\n");
				}

			} else {
				System.out.println("Getting job errors results");

				String[] error = protocol.getErrorMessages();

				for (int i = 0; i < error.length; i++) {
					System.out.println(error[i]);
				}
			}
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// Remove the job from the server
				try {
					System.out.println("Deleting job " + selectedJob.getJobId());
					selectedJob.releaseJob();
				} catch (Exception ex) {
					ex.printStackTrace();
				} // end job cleanup

			} // end try
			

	}
	
	private String extractFileName(String url) {
		String [] vec = url.split("/");
		return vec[vec.length-1];
	}
	
	
	public TestGUI() {           
		createTopGUI();
		//System.out.println("This is the testGUI.");
	}

	public static void main(String[] args) {
		Login loginForm = new Login();
		loginForm.waitForLogin();
		if (Login.isUserValid) {
			new TestGUI();
			loginForm.closeLogin();
		} else {
			System.exit(0);
		}
	}



	private class myActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			if (e.getSource().equals(buttonRightBottom)) {
				ResultLabel.setText("");  // Clear the content
				submitJob();
				return;
			}

			if (e.getSource().equals(buttonRightBottom)) {
				// Check if ResultLabel is already added
				if (ResultLabel == null) {
					// For Result
					JLabel ResultL = new JLabel("Result:");
					panelRightBottom.add(ResultL);
					ResultLabel = new JTextArea(0, 1);  // Initialize ResultLabel
					ResultLabel.setLineWrap(true);
					panelRightBottom.add(ResultLabel);

					// Refresh the panel
					panelRightBottom.revalidate();
					panelRightBottom.repaint();
				}

				submitJob();
				return;
			}
		
			if ( e.getSource().equals(buttonLeftTop) ) {     // which object triggers the event?
				System.out.println("buttonLeftTop was clicked");
				buttonLeftTop.setText("click to refresh the PLP server info.");
				refreshPLPTree();
				return;
			}
			
			if ( e.getSource().equals(buttonRightBottom) ) {
				submitJob();
				return;
			}
			
			if ( e.getSource() instanceof JTextField ) {
				System.out.println("A file JTextField was clicked");
				
				JFileChooser fileChooser = new JFileChooser();
				int response = fileChooser.showOpenDialog(null) ; 
				if (response == JFileChooser.APPROVE_OPTION) {
					System.out.println("File chosen = " + fileChooser.getSelectedFile().getAbsolutePath());
					((JTextField) e.getSource()).setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
				return;
			}
			
			// add more action handling code if needed. 
			return;    // do nothing.
		}
	}
}
