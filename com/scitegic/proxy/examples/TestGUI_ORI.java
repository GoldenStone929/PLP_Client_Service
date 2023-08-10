package com.scitegic.proxy.examples;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.io.File;


import com.scitegic.proxy.ComponentDatabase;
import com.scitegic.proxy.ComponentInfo;
import com.scitegic.proxy.ParameterInfo;
import com.scitegic.proxy.Job;
import com.scitegic.proxy.JobResult;
import com.scitegic.proxy.JobStatus;
import com.scitegic.proxy.PipelinePilotServer;
import com.scitegic.proxy.PipelinePilotServerConfig;
import com.scitegic.proxy.XmldbItem;



public class TestGUI_ORI {

	//static final String EXAMPLE_PROTOCOLS = "Protocols/Examples";
	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/BHT/PLP_for_DataWarrior/";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "for_dev_test/01_Hello_World";
	static final String WHITESPACE = "                                                     ";

	private PipelinePilotServer pp = null;
	private PipelinePilotServerConfig conf = null;
	private Job protocol = null;
	private ComponentDatabase compdb = null;
	private XmldbItem rootFolder = null;

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

	private void findXmldbItemRecursive(XmldbItem node, String nodeName) {  // search for a protocol node under the parent node

		if (node.isFolder()) {
			XmldbItem[] children = node.getChildren();

			for (int i = 0; i < children.length; i++) {

				XmldbItem childNode = children[i];

				if ( childNode.isFolder() == false ) {
					if (childNode.getName().equals(nodeName)) {
						selectedXmldbItem = childNode;
						return;
					} else {
						continue;
					}
				} else {
					findXmldbItemRecursive(childNode, nodeName);  // recursive call
				}
			}
		}

	}


	public void addPLPTree() {

		try {
			pp         = new PipelinePilotServer("10.30.50.184:9944", "zhengweipeng", "Slaysalon2~");
			conf       = pp.getServerConfig();
			compdb     = pp.getComponentDatabase();
			rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);

			System.out.println(rootFolder.getName());

			rootTreeNode = new DefaultMutableTreeNode(rootFolder.getName());  // the top treeNode. It will not showing up in the Jtree

			myTree = new JTree(rootTreeNode);                                 // create a JTree GUI to interact with user

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


			addFolderTreeRecursive(rootFolder, rootTreeNode);

			panelLeftMiddle.add(myTree);

			labelRight.setText("Show which tree node is clicked by user.");


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


	private void refreshPLPTree() {    // not working yet

		rootTreeNode.removeAllChildren();    // clear the treeNode model
		rootFolder = null;


		try {
		compdb     = pp.getComponentDatabase();      // get the updated PLP component database
		rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);  // get the updated XmldbItem tree model


		addFolderTreeRecursive(rootFolder, rootTreeNode);  // re-populate the treeNode model


		//
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

		System.out.println(selectedCmptInfo.getFullName());

		JLabel nameL = new JLabel("Protocol Name:");
		panelRightTop.add(nameL);

		JTextArea nameLabel = new JTextArea(0, 1);
		nameLabel.setText(protocolName);
		nameLabel.setLineWrap(true);
		panelRightTop.add(nameLabel);

		JLabel descriptionL = new JLabel("Description:");
		panelRightTop.add(descriptionL);

		JTextArea descriptionLabel = new JTextArea(0, 1);
		descriptionLabel.setText(selectedCmptInfo.getDescription());
		descriptionLabel.setLineWrap(true);
		panelRightTop.add(descriptionLabel);

		panelRightTop.revalidate();
		panelRightTop.repaint();

		ParameterInfo[] params = selectedCmptInfo.getParameters();


		for (int i=0; i<params.length; ++i) {
			ParameterInfo para = params[i];

			if (InSkipSet(para.getName()) == false) {    // ignore a few irrelevant PLP parameters.
				addParameters(para);
			}
		}

		//selectedProtocol = null;    // keep it for job submission
		return;

		} catch (Exception e) {
			//e.printStackTrace();
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


		panelRightMiddle.revalidate();
		panelRightMiddle.repaint();
		return;
	}

	private void createTopGUI() {
		frame = new JFrame();

		panelLeft = new JPanel();
		panelLeft.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeft.setLayout(new GridLayout(0, 1));

		panelRight = new JPanel();
		panelRight.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelRight.setLayout(new GridLayout(0, 1));
		panelRight.setAutoscrolls(true);

		//addTree();
		//addPLPTree();

		createLeftPanel();
		createRightPanel();

		frame.add(panelLeft, BorderLayout.WEST);
		frame.add(panelRight, BorderLayout.EAST);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("My Login");
		frame.pack();
		frame.setVisible(true);
	}

	private void createLeftPanel() {

		panelLeftTop = new JPanel();
		panelLeftTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftTop.setLayout(new BoxLayout(panelLeftTop, BoxLayout.PAGE_AXIS));

		panelLeftMiddle = new JPanel();
		panelLeftMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		//panelLeftMiddle.setLayout(new BoxLayout(panelLeftMiddle, BoxLayout.PAGE_AXIS));
		panelLeftMiddle.setLayout(new BorderLayout(0, 1));

		panelLeftBottom = new JPanel();
		panelLeftBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, this.height, this.width));
		panelLeftBottom.setLayout(new BoxLayout(panelLeftBottom, BoxLayout.PAGE_AXIS));

		buttonLeftTop = new JButton("Refresh the JTree (to be implemented)");
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

	}

public void setProtocolParametersfromGUI() {

		ParameterInfo[] params = selectedCmptInfo.getParameters();

		int ith = 0;               // index for the parameters
		int jth = 0;               // index for the JTextArea in panelRightMiddle

		for (int i=0; i<params.length; ++i) {
			ParameterInfo para = params[i];

			if (InSkipSet(para.getName()) == false) {    // ignore a few irrelevant PLP parameters.
				jth = 2*ith + 1;

				//String pType = para.getType();

				if ( panelRightMiddle.getComponent(jth) instanceof javax.swing.JTextField) {
					selectedJob.setInputValue(para.getName(), ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
					System.out.println(para.getName() + " = "  + ((JTextField) panelRightMiddle.getComponent(jth) ).getText());
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
				System.out.println("returned Message is " + message);


				// get results as files.
				String[] results = prr.getResultFiles();

				if ((results != null) && (results.length > 0)) {
					// print all result files
					System.out.println("Getting job results:");
					for (int i = 0; i < results.length; i++) {
						System.out.println(results[i]);
					}
					System.out.println();

					// download, locally, the first result file
					File localResultFile = new File("output.sdf");
					//File localResultFile = new File("chart.htm");
					System.out.print("Writing result file to ");
					System.out.println(localResultFile.getAbsolutePath());
					System.out.println();

					pp.getRemoteFileManager().downloadFile(results[0], localResultFile);
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

	public TestGUI_ORI() {
		createTopGUI();
		//System.out.println("This is the testGUI.");
	}

	public static void main(String[] args) {

		new TestGUI_ORI();
	}

	private class myActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if ( e.getSource().equals(buttonLeftTop) ) {     // which object triggers the event?
				System.out.println("buttonLeftTop was clicked");
				buttonLeftTop.setText("You click me to refresh the JTree.");
				refreshPLPTree();
				return;
			}

			if ( e.getSource().equals(buttonRightBottom) ) {
				submitJob();
				return;
			}
			return;    // do nothing.
		}
	}
}
