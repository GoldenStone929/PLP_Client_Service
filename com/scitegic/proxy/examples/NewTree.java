package com.scitegic.proxy.examples;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class NewTree {

	public static void main(String[] args) {
		// Get the information from the server
		String[] folders = getFoldersFromServer();
		String[] nodes = getNodesFromServer();

		// Create a tree
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Protocols/Web Services/Web Port Examples");
		for (String folder : folders) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(folder);
			top.add(child);
			for (String node : nodes) {
				if (folder.equals("Utilities")) {
					continue;
				}
				child.add(new DefaultMutableTreeNode(node));
			}
		}

		// Display the tree
		JTree tree = new JTree(top);
		JFrame frame = new JFrame("Tree Example");
		frame.add(tree);
		frame.pack();
		frame.setVisible(true);
	}

	private static String[] getFoldersFromServer() {
		// TODO: Implement this method to get the folders from the server
		return new String[] {"Chemistry", "Generic", "Query Service"};
	}

	private static String[] getNodesFromServer() {
		// TODO: Implement this method to get the nodes from the server
		return new String[] {
				"01 Simple Search",
				"02 Property Profiling",
				"03 ADMET Profiling",
				"04 Clustering Molecules",
				"05 Query by Form",
				"06 SAR Table",
				"07 Activity Modeling",
				"08 Find Molecules by Name Connecting to DGWS",
				"09 Pipette Sketcher and DGWS Search",
		};
	}
}
