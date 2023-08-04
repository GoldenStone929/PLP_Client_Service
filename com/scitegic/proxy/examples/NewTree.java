package com.scitegic.proxy.examples;

import com.scitegic.proxy.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class NewTree {

	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "/Generic/XY Scatter Plot Utility";

	public NewTree() {
	}

	private static DefaultMutableTreeNode buildTreeRecursive(XmldbItem folder, int indent) {
		if (folder.getName().toLowerCase().equals("utilities")) {
			return null;
		}
		DefaultMutableTreeNode currentFolder = new DefaultMutableTreeNode(folder.getName());
		indent += 2;
		XmldbItem[] children = folder.getChildren();
		for (int i = 0, m = children.length; i < m; i++) {
			XmldbItem child = children[i];
			if (child.isFolder()) {
				DefaultMutableTreeNode childFolder = buildTreeRecursive(child, indent);
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
			DefaultMutableTreeNode treeRoot = buildTreeRecursive(rootFolder, 0);

			SwingUtilities.invokeLater(() -> {
				JFrame frame = new JFrame("File Structure");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JTree tree = new JTree(treeRoot);
				JScrollPane scrollPane = new JScrollPane(tree);
				frame.add(scrollPane);

				frame.setSize(1500, 1500);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			});

			protocol = pp.createJob(PROTOCOL);
			boolean uploadFromClient = true;
			if (uploadFromClient) {
				File localFile = new File("./data/imports-85.txt");
				protocol.setInputFileOnClient("Source", localFile);
			} else {
				protocol.setInputValue("Source", "data/Tables/imports-85.txt");
			}

			protocol.setInputValue("X Property", "Highwaympg");
			protocol.setInputValue("Y Property", "Horsepower");
			protocol.setInputValue("Tooltip",
					"'Make = ' . (make) . ', $(X Property) = ' . ($(X Property)) . ', "
							+ "$(Y Property) = ' . ($(Y Property))");
			protocol.setInputValue("File Type", "PDF");
			protocol.validate();

			protocol.run();

			JobStatus status = protocol.getStatus();
			while (!status.isExitStatus()) {
				Thread.sleep(2000);
				status = protocol.getStatus();
			}

			if (JobStatus.Finished.equals(status)) {
				String[] results = protocol.getJobResults().getResultFiles();

				if (results.length > 0) {
					File localResultFile = new File("chart.pdf");
					pp.getRemoteFileManager().downloadFile(results[0], localResultFile);
				}
			}
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
