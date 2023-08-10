package com.scitegic.proxy.examples;

import com.scitegic.proxy.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TestGUI {

	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/BHT/PLP_for_DataWarrior/";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "for_dev_test/01_Hello_World";
	static final String WHITESPACE = "                                                     ";

	private PipelinePilotServer pp = null;
	private PipelinePilotServerConfig conf = null;
	private Job protocol = null;
	private ComponentDatabase compdb = null;
	private XmldbItem rootFolder = null;

	private JFrame frame;
	private JPanel panelLeft, panelLeftTop, panelLeftMiddle, panelLeftBottom;
	private JPanel panelRight, panelRightTop, panelRightMiddle, panelRightBottom;
	private JButton buttonLeftTop, buttonLeftBottom, buttonRightTop, buttonRightBottom;
	private JLabel labelLeft;
	private JLabel labelRight = new JLabel("Show which tree node is clicked by user.");
	private int width = 50;
	private int height = 200;
	private int shift = 5;
	private int countLeft, countRight;
	private JTree myTree;
	DefaultMutableTreeNode rootTreeNode;
	private myActionListener myListner = new myActionListener();

	// Additional attributes and methods you've provided...

	public TestGUI() {
		createTopGUI();
	}

	private void createTopGUI() {
		frame = new JFrame();
		panelLeft = new JPanel();
		panelLeft.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelLeft.setLayout(new GridLayout(0, 1));
		panelRight = new JPanel();
		panelRight.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelRight.setLayout(new GridLayout(0, 1));
		panelRight.setAutoscrolls(true);

		createLeftPanel();
		createRightPanel();
		addPLPTree();

		frame.add(panelLeft, BorderLayout.WEST);
		frame.add(panelRight, BorderLayout.EAST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("My Login");
		frame.pack();
		frame.setVisible(true);
	}

	private void createLeftPanel() {
		panelLeftTop = new JPanel();
		panelLeftTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelLeftTop.setLayout(new BoxLayout(panelLeftTop, BoxLayout.PAGE_AXIS));
		panelLeftMiddle = new JPanel();
		panelLeftMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelLeftMiddle.setLayout(new BorderLayout(0, 1));
		panelLeftBottom = new JPanel();
		panelLeftBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
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
	}

	private void createRightPanel() {
		panelRightTop = new JPanel();
		panelRightTop.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelRightTop.setLayout(new BoxLayout(panelRightTop, BoxLayout.PAGE_AXIS));
		panelRightMiddle = new JPanel();
		panelRightMiddle.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
		panelRightMiddle.setLayout(new BoxLayout(panelRightMiddle, BoxLayout.PAGE_AXIS) );
		panelRightBottom = new JPanel();
		panelRightBottom.setBorder(BorderFactory.createEmptyBorder(shift, shift, height, width));
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

	private void addPLPTree() {
		try {
			pp = new PipelinePilotServer(Login.Global.serverAddress, Login.Global.username, Login.Global.password);
			conf = new PipelinePilotServerConfig(pp);
			compdb = conf.getComponentDatabase();
			rootFolder = compdb.getFolder(WEB_PORT_EXAMPLE_PROTOCOLS);

			rootTreeNode = new DefaultMutableTreeNode(rootFolder.getName());
			listPLPFolder(rootTreeNode, rootFolder);

			myTree = new JTree(rootTreeNode);
			myTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();

					if (node == null) return;
					if (node.isLeaf()) {
						XmldbItem item = (XmldbItem) node.getUserObject();
						if (item.isProtocol()) {
							listProtocolInfo(item.getFullName());
						}
					}
				}
			});

			panelLeftMiddle.add(new JScrollPane(myTree));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listProtocolInfo(String protocolName) {
		try {
			protocol = conf.getJob(protocolName);
			ParameterInfo[] paras = protocol.getParameterInfo();

			for (ParameterInfo para : paras) {
				addParameters(para);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addParameters(ParameterInfo para) {
		String[] legals = para.getLegalValues();
		if (legals.length > 1) {
			JComboBox<String> jcb = new JComboBox<String>(legals);
			jcb.setSelectedIndex(1);
			panelRightMiddle.add(jcb);
		} else {
			JTextField jtf = new JTextField(para.getDefaultValue());
			panelRightMiddle.add(jtf);
		}
	}

	private void submitJob() {
		System.out.println("I am inside submitJob().");
	}

	public static void main(String[] args) {
		Login loginForm = new Login();
		loginForm.setVisible(true);
		if (loginForm.isUserValid) {
			new TestGUI();
		} else {
			System.exit(0); // Exit the application if login fails
		}
	}

	private class myActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(buttonLeftTop)) {
				refreshPLPTree();
			}
			if (e.getSource().equals(buttonRightBottom)) {
				submitJob();
			}
		}
	}
}
