package com.scitegic.proxy.examples;
import javax.swing.text.DefaultCaret;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import javax.swing.Box;
import java.awt.Graphics;
import javax.swing.border.Border;
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
	private JTextArea ProgressLabel;  // Declare ProgressLabel as an instance variable

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
			messageContent(rootFolder.getName());

			rootTreeNode = new DefaultMutableTreeNode(rootFolder.getName());  // the top treeNode. It will not show up in the JTree
			myTree = new JTree(rootTreeNode);  // create a JTree GUI to interact with user
			myTree.setBackground(Color.WHITE);  // Set background color of the JTree to white

			myTree.setBorder(null); // remove the border of the JTree


			JScrollPane treeScrollPane = new JScrollPane(myTree);
			treeScrollPane.getViewport().setBackground(Color.WHITE);
			treeScrollPane.setBorder(null); // remove the border of the JScrollPane

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
					messageContent("Deleting job " + protocol.getJobId());
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



	private String shiftTextToLeft(String text) {
		int halfLength = text.length() / 2;
		StringBuilder shiftedText = new StringBuilder();

		for (int i = 0; i < halfLength; i++) {
			shiftedText.append(" ");  // append space
		}
		shiftedText.append(text);

		return shiftedText.toString();
	}


	void listProtocolInfo(String protocolName) {

		panelRightTop.removeAll();  // refresh it for each protocol selected by user
		panelRightTop.revalidate();
		panelRightTop.repaint();

		panelRightMiddle.removeAll();  // refresh it for each protocol selected by user
		panelRightMiddle.revalidate();
		panelRightMiddle.repaint();

		int rightPadding = 10;  // Adjust this value as needed
		Border paddingBorder = BorderFactory.createEmptyBorder(0, 0, 0, rightPadding);

		try {
			selectedProtocolName = protocolName;
			selectedJob = pp.createJob(protocolName);
			selectedCmptInfo = selectedJob.getComponentInfo();




// For Protocol Name -----------------------------------------------------------
			JLabel nameL = new JLabel("Protocol Name:");
			Border paddingBorderWithSpace = BorderFactory.createEmptyBorder(0, 0, 0, 5); // 增加了5像素的右侧填充
			nameL.setBorder(paddingBorderWithSpace);
			JTextArea nameLabel = new JTextArea(0, 30);
			nameLabel.setText(protocolName);
			nameLabel.setLineWrap(true);
			nameLabel.setWrapStyleWord(true);
			nameLabel.setEditable(false);
			nameLabel.setBackground(Color.WHITE);

// For Description -----------------------------------------------------------
			JLabel descriptionL = new JLabel("Description:");
			Border paddingBorder_des = BorderFactory.createEmptyBorder(0, 0, 3, 5); //

			descriptionL.setBorder(paddingBorder_des);
			JTextArea descriptionLabel = new JTextArea(0, 30);
			descriptionLabel.setText(selectedCmptInfo.getDescription());
			descriptionLabel.setLineWrap(true);
			descriptionLabel.setWrapStyleWord(true);
			descriptionLabel.setEditable(false);


			Border rounded = new RoundedBorder(5);
			Border empty = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			Border compound = BorderFactory.createCompoundBorder(rounded, empty);

			nameLabel.setBorder(compound);
			descriptionLabel.setBorder(compound);

			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
			namePanel.add(nameL);
			namePanel.add(nameLabel);

			JPanel descriptionPanel = new JPanel();
			descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.X_AXIS));
			descriptionPanel.add(descriptionL);
			descriptionPanel.add(descriptionLabel);

			panelRightTop.add(namePanel);
			panelRightTop.add(Box.createVerticalStrut(10)); // add a spacer
			panelRightTop.add(descriptionPanel);

			panelRightTop.revalidate();
			panelRightTop.repaint();

			ParameterInfo[] params = selectedCmptInfo.getParameters();
			for (int i = 0; i < params.length; ++i) {
				ParameterInfo para = params[i];
				if (!InSkipSet(para.getName())) {
					addParameters(para);
				}
			}

		} catch (Exception e) {
			selectedProtocolName = null;
			selectedXmldbItem = null;
			selectedJob = null;
			selectedCmptInfo = null;
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
		frame.setSize(900, 600);
		frame.setVisible(true); // make the frame visible
		frame.setResizable(true); // make the frame resizable
		frame.setMinimumSize(new Dimension(600, 400)); // set the minimum size of the frame
		frame.setLocationRelativeTo(null); // center the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close the frame when the user clicks the close button
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
		frame.setTitle("Blossomhill Therapeutics PLP launcher");
		frame.pack();
		frame.setVisible(true);
	}



	private void createLeftPanel() {
		// Load the background image
		ImageIcon imageIcon = new ImageIcon("data/BHT_img.jpg");
		Image backgroundImage = imageIcon.getImage();

		// Create a custom JPanel for the top left panel with the background image
		panelLeftTop = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Determine the target width and height as 80% of the panel's size
				int targetWidth = (int) (this.getWidth() * 0.8);
				int targetHeight = (int) (this.getHeight() * 0.8);

				// Determine the x and y coordinates to center the image
				int x = (this.getWidth() - targetWidth) / 2;
				int y = (this.getHeight() - targetHeight) / 2;

				// Draw the background image centered and scaled to 80% of the panel's size
				g.drawImage(backgroundImage, x, y, targetWidth, targetHeight, this);
			}
		};
		panelLeftTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftTop.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align to the left
		// Set JButton as before
		buttonLeftTop = new JButton("Refresh JTree");
		buttonLeftTop.addActionListener(myListner);
		panelLeftTop.add(buttonLeftTop); // Add the button to the top-left corner
		// Set the panel's background color to white
		panelLeftTop.setBackground(Color.WHITE);
		// Set the background color to #3AAAF3
		buttonLeftTop.setBackground(new Color(58, 170, 243));
		// set font color to #574382
		buttonLeftTop.setForeground(Color.BLACK);
		buttonLeftTop.setFont(new Font("Arial", Font.ITALIC, 14));
		buttonLeftTop.setOpaque(true); // Set the button to be opaque

		panelLeftMiddle = new JPanel();
		panelLeftMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftMiddle.setLayout(new BorderLayout());

		panelLeftBottom = new JPanel();
		panelLeftBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftBottom.setLayout(new BoxLayout(panelLeftBottom, BoxLayout.PAGE_AXIS));

		// For Progress
		JLabel ProgressL = new JLabel("Progress:");
		ProgressL.setFont(new Font("Arial", Font.PLAIN, 18));
		panelLeftBottom.add(ProgressL);
		ProgressLabel = new JTextArea(10, 1); // Initialize ProgressLabel with dimensions
		ProgressLabel.setLineWrap(true);
		ProgressLabel.setWrapStyleWord(true);
		ProgressLabel.setEditable(false);
		ProgressLabel.setBorder(null);
		ProgressLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		ProgressLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300)); // Set preferred size
		ProgressLabel.setBackground(new Color(238, 238, 238));
		panelLeftBottom.add(ProgressLabel);





		// Add panels
		panelLeft.add(panelLeftTop, BorderLayout.PAGE_START);
		panelLeft.add(new JScrollPane(panelLeftMiddle), BorderLayout.CENTER);
		panelLeft.add(panelLeftBottom, BorderLayout.PAGE_END);

		addPLPTree();
	}

//	public void appendProgressMessage(String message) {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String dateTime = formatter.format(new Date());
//		ProgressLabel.append("[" + dateTime + "] " + message + "\n");
//	}

	public void messageContent(String content) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = formatter.format(new Date());
		String message = "returned Message is: \n" + content + "\n";
		ProgressLabel.append("[" + dateTime + "] " + message);
	}





	private void createRightPanel() {
		panelRightTop = new JPanel();
		panelRightTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightTop.setLayout(new BoxLayout(panelRightTop, BoxLayout.PAGE_AXIS));

		panelRightMiddle = new JPanel();
		panelRightMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightMiddle.setLayout(new BoxLayout(panelRightMiddle, BoxLayout.PAGE_AXIS));

		panelRightBottom = new JPanel();
		panelRightBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRightBottom.setLayout(new BoxLayout(panelRightBottom, BoxLayout.PAGE_AXIS));
//		panelRightBottom.setBackground(Color.WHITE);

		panelRight.add(panelRightTop, BorderLayout.PAGE_START);
		panelRight.add(panelRightMiddle, BorderLayout.CENTER);
		panelRight.add(panelRightBottom, BorderLayout.PAGE_END);

		buttonRightTop = new JButton("Button rightTop (PAGE_START)");
		buttonRightTop.addActionListener(myListner);
		panelRightTop.add(buttonRightTop, BorderLayout.PAGE_START);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // 设置边距为0
		buttonRightBottom = new JButton("Submit Job");
		buttonRightBottom.addActionListener(myListner);
		buttonPanel.add(buttonRightBottom); // 将按钮添加到新面板中
		panelRightBottom.add(buttonPanel); // 将新面板添加到 panelRightBottom

		//add space inbetween
		panelRightBottom.add(Box.createRigidArea(new Dimension(0, 10))); // 10像素的垂直空间
		panelRightBottom.add(Box.createVerticalGlue()); // 垂直方向上的可伸缩空间


		// For Result
		JLabel ResultL = new JLabel("Result:");
		Font labelFont = ResultL.getFont();
		ResultL.setFont(new Font(labelFont.getName(), labelFont.getStyle(), 18)); // 设置字体大小为20
		panelRightBottom.add(ResultL);

		panelRightBottom.add(ResultL);
		ResultLabel = new JTextArea(10, 1);  // Initialize ResultLabel
		ResultLabel.setLineWrap(true);
		ResultLabel.setWrapStyleWord(true);
		ResultLabel.setEditable(false);
		ResultLabel.setBorder(null);
		ResultLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // 设置最大高度和宽度
		ResultLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 300));
		ResultLabel.setBackground(new Color(238, 238, 238));

		// center the textarea
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
							messageContent(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
						}
						else {
							selectedJob.setInputValue(para.getName(), ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
							System.out.println(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
							messageContent(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
						}
					}
					else if ( panelRightMiddle.getComponent(jth) instanceof javax.swing.JComboBox) {
						selectedJob.setInputValue(para.getName(), ((JComboBox) panelRightMiddle.getComponent(jth) ).getSelectedItem().toString());
						System.out.println(para.getName() + " = "  + ((JComboBox) panelRightMiddle.getComponent(jth) ).getSelectedItem().toString());
						messageContent(para.getName() + " = "  + ((JComboBox) panelRightMiddle.getComponent(jth) ).getSelectedItem().toString());
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
//		System.out.println("I am inside submitJob(). ");
		messageContent("I am inside submitJob(). ");


		// prepare job parameters
		// selectedJob.setInputFileOnClient("Source", localFile);              // file on the client-side
		// selectedJob.setInputValue("Source", "data/Tables/imports-85.txt");  // file on the server-side
		//
		// selectedJob.setInputValue("X Property", "Highwaympg");              // set parameter value pair, string
		// selectedJob.setInputValue("NumOfRun:, 10);                          // ineger type
		// selectedJob.setInputValue("selectedChoices", String[] values);
		//

		System.out.println("Submitting a job " + selectedProtocolName);
		messageContent("Submitting a job " + selectedProtocolName);

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
				ResultLabel.append("returned Message is: " + "\n" + message + "\n");

				// get results as files.
				String[] results = prr.getResultFiles();

				if ((results != null) && (results.length > 0)) {
					// print all result files
					System.out.println("Getting job results:");
					messageContent("Getting job results:");
					for (int i = 0; i < results.length; i++) {
						System.out.println(results[i]);                          // remote file URL
						messageContent(results[i]);

						String localFileName = localOutputFolder + extractFileName(results[i]);

						File localResultFile = new File(localFileName);
						pp.getRemoteFileManager().downloadFile(results[0], localResultFile);

						System.out.print("Writing result file to ");
						messageContent("Writing result file to ");
						System.out.println(localFileName);
						messageContent(localFileName);

						//System.out.println(localResultFile.getAbsolutePath());  // local file full path
						messageContent(localResultFile.getAbsolutePath());
					}
					System.out.println();
					messageContent("");
				} else {
//					System.out.println("No job results");

//					ResultLabel.append("No job results" + "\n");

					messageContent("No job results");


				}


			} else {
//				System.out.println("Getting job errors results");
				messageContent("Getting job errors results");

				String[] error = protocol.getErrorMessages();

				for (int i = 0; i < error.length; i++) {
					System.out.println(error[i]);
					messageContent(error[i]);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Remove the job from the server
			try {
				System.out.println("Deleting job " + selectedJob.getJobId());

				messageContent("Deleting job " + selectedJob.getJobId());


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
		messageContent("This is the testGUI.");

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
				messageContent("buttonLeftTop was clicked");

				buttonLeftTop.setText("refresh Server info.");
				refreshPLPTree();
				return;
			}

			if ( e.getSource().equals(buttonRightBottom) ) {
				submitJob();
				return;
			}

			if ( e.getSource() instanceof JTextField ) {
				System.out.println("A file JTextField was clicked");
				messageContent("A file JTextField was clicked");

				JFileChooser fileChooser = new JFileChooser();
				int response = fileChooser.showOpenDialog(null) ;
				if (response == JFileChooser.APPROVE_OPTION) {
					messageContent("File chosen = " + fileChooser.getSelectedFile().getAbsolutePath());
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
