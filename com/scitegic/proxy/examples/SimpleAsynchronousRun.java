package com.scitegic.proxy.examples;

import com.scitegic.proxy.*;

import java.io.File;
import java.util.ArrayList;

public class SimpleAsynchronousRun {

//	static final String EXAMPLE_PROTOCOLS = "Protocols/Examples";
	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "/Generic/XY Scatter Plot Utility";
	static final String WHITESPACE = "                                                     ";
	static ArrayList<ArrayList<String>> folderTree = new ArrayList<ArrayList<String>>();

	public SimpleAsynchronousRun() {
	}

	private static void printFolderTreeRecursive(XmldbItem folder, int indent) {
		if (folder.getName().toLowerCase().equals("utilities")) {
			return;
		}
		String folderName = WHITESPACE.substring(0, indent) + folder.getName();
		ArrayList<String> currentFolder = new ArrayList<String>();
		currentFolder.add(folderName);
		folderTree.add(currentFolder);
		indent += 2;
		XmldbItem[] children = folder.getChildren();
		for (int i = 0, m = children.length; i < m; i++) {
			XmldbItem child = children[i];
			if (child.isFolder()) {
				printFolderTreeRecursive(child, indent);
			} else {
				String fileName = WHITESPACE.substring(0, indent) + child.getName();
				currentFolder.add(fileName);
			}
		}
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
			printFolderTreeRecursive(rootFolder, 0);

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

		// Print the folderTree ArrayList
		for (ArrayList<String> folder : folderTree) {
			for (String item : folder) {
				System.out.println("N * N :" + item);
			}
			System.out.println(); // Print a new line between folders
		}
	}
}
