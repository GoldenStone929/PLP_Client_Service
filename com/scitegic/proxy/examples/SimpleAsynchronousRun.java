package com.scitegic.proxy.examples;

import com.scitegic.proxy.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class SimpleAsynchronousRun {

	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "/Generic/XY Scatter Plot Utility";

	public SimpleAsynchronousRun() {
	}

	private static DefaultMutableTreeNode buildFolderTreeRecursive(XmldbItem folder, int indent) {
		if (folder.getName().toLowerCase().equals("utilities")) {
			return null;
		}
		DefaultMutableTreeNode currentFolder = new DefaultMutableTreeNode(folder.getName());
		indent += 2;
		XmldbItem[] children = folder.getChildren();
		for (int i = 0, m = children.length; i < m; i++) {
			XmldbItem child = children[i];
			if (child.isFolder()) {
				DefaultMutableTreeNode childFolder = buildFolderTreeRecursive(child, indent);
				if (childFolder != null) {
					currentFolder.add(childFolder);
				}
			} else {
				currentFolder.add(new DefaultMutableTreeNode(child.getName()));
			}
		}
		return currentFolder;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			return;
		}

		String server = args[0];
		String user = args[1];
		String password = args[2];

		PipelinePilotServer pp = null;
		Job protocol = null;

		try {
			pp = new PipelinePilotServer(server, user, password);
			PipelinePilotServerConfig conf = pp.getServerConfig();
			ComponentDatabase compdb = pp.getComponentDatabase();

			XmldbItem rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);
			DefaultMutableTreeNode treeRoot = buildFolderTreeRecursive(rootFolder, 0);

			// Create and show the JTree
			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame("File Structure");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JTree tree = new JTree(treeRoot);
				JScrollPane scrollPane = new JScrollPane(tree);
				frame.add(scrollPane);

				frame.setSize(500, 500);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			});

			// Other job-related code ...
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (protocol != null) {
				try {
					protocol.releaseJob();
				} catch (Exception ex) {
				}
			}
		}
	}
}
