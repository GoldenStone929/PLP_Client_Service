/*
 * This program and all files, classes and data used by it are protected by Copyright and hence may
 * not be used, copied, modified, transmitted, inspected, or executed by any means including the
 * use of electronic data processing equipment, xerography, or any other methods without the
 * express written permission of the copyright holder.
 *
 * Copyright (C) 2003 Accelrys Software Inc., All Rights Reserved
 *                    10188 Telesis Ct.
 *                    Suite 100
 *                    San Diego, CA 92121
 *                    (858) 799-5000
 */
package com.scitegic.proxy.examples;

import java.io.File;

import com.scitegic.proxy.ComponentDatabase;
import com.scitegic.proxy.Job;
import com.scitegic.proxy.JobStatus;
import com.scitegic.proxy.PipelinePilotServer;
import com.scitegic.proxy.PipelinePilotServerConfig;
import com.scitegic.proxy.XmldbItem;

/**
 * This sample Java client illustrates how to use the 'Java Client SDK for Server' to launch Protocol Jobs, Asynchronously, on a server
 */
public class SimpleAsynchronousRun {

	static final String EXAMPLE_PROTOCOLS = "Protocols/Examples";
	static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";
	static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "/Generic/XY Scatter Plot Utility";
	static final String WHITESPACE = "                                                     ";
	
	public SimpleAsynchronousRun() {
	}


	private static void printFolderTreeRecursive(XmldbItem folder, int indent) {
		if (folder.getName().toLowerCase().equals("utilities")) {
			return;
		}
		System.out.println(WHITESPACE.substring(0, indent) + folder.getName());
		indent += 2;
		XmldbItem[] children = folder.getChildren();
		for (int i = 0, m = children.length; i < m; i++) {
			XmldbItem child = children[i];
			if (child.isFolder()) {
				printFolderTreeRecursive(child, indent);
			} else {
				System.out.println(WHITESPACE.substring(0, indent) + child.getName());
			}
		}
	}


	public static void main(String[] args) {
		// Pick up the server URL, username, and password from the command line
		if (args.length < 3) {
			System.out.println("Usage: java com.scitegic.proxy.examples.SimpleAsynchronousRun "
					+ "<server_url> <username> <password>");
			return;
		}

		String server = args[0];
		String user = args[1];
		String password = args[2];

		PipelinePilotServer pp = null;
		Job protocol = null;

		try {
			pp = new PipelinePilotServer(server, user, password);

			// Print server configuration settings.
			PipelinePilotServerConfig conf = pp.getServerConfig();
			System.out.println("Printing server configuration settings");
			System.out.println("  Local root = " + conf.getLocalRoot());
			System.out.println("  Local temp root = " + conf.getLocalTempRoot());
			System.out.println("  File Web Service Endpoint = " + conf.getSciTegicFileEndPoint());
			System.out.println("  Server version = " + conf.getServerVersion());
			System.out.println("  Authentication on = " + conf.isAuthenticationOn());
			System.out.println();
			System.out.println("Printing SciTegic Root of server");
			String root = pp.getRemoteFileManager().getSciTegicRoot();
			System.out.println("  SciTegic Root = " + root);
			System.out.println();

			ComponentDatabase compdb = pp.getComponentDatabase();

			// Print contents of 'Protocols/Examples'
			System.out.println("Printing folder names under " + EXAMPLE_PROTOCOLS);
			String[] list = compdb.getFolderNamesInFolder(EXAMPLE_PROTOCOLS);
			for (int i = 0; i < list.length; i++) {
				System.out.println(list[i]);
			}
			System.out.println();

			// Print out folder hierarchy under 'Protocols/Web Services/Web Port Examples'.
			System.out.println("Printing folder tree under " + WEB_PORT_EXAMPLE_PROTOCOLS
					+ " (omitting Utilities directories)");
			XmldbItem rootFolder = compdb.getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);
			printFolderTreeRecursive(rootFolder, 0);
			System.out.println();

			// Create job and add parameters for protocol run
			protocol = pp.createJob(PROTOCOL);

			boolean uploadFromClient = true;
			if (uploadFromClient) {
				// Use setInputFileOnClient() to upload a local file to the server.
				File localFile = new File("./data/imports-85.txt");
				protocol.setInputFileOnClient("Source", localFile);
			} else {
				// Alternatively, read a file on the server.
				protocol.setInputValue("Source", "data/Tables/imports-85.txt");
			}

			protocol.setInputValue("X Property", "Highwaympg");
			protocol.setInputValue("Y Property", "Horsepower");
			protocol.setInputValue("Tooltip",
					"'Make = ' . (make) . ', $(X Property) = ' . ($(X Property)) . ', "
							+ "$(Y Property) = ' . ($(Y Property))");
			protocol.setInputValue("File Type", "PDF");
			protocol.validate();

			// Run job and check results
			System.out.println("Starting Protocol Job Synchronously");
			System.out.println();

			protocol.run();

			System.out.println("Polling Job to see if it is finished...");
			JobStatus status = protocol.getStatus();
			System.out.println("Protocol Status is: " + status.toString());

			while (!status.isExitStatus()) {
				Thread.sleep(2000);
				status = protocol.getStatus();
				System.out.println("Protocol Status is: " + status.toString());
			}
			System.out.println();

			if (JobStatus.Finished.equals(status)) {
				String[] results = protocol.getJobResults().getResultFiles();

				if (results.length > 0) {
					// print all result files
					System.out.println("Getting job results:");
					for (int i = 0; i < results.length; i++) {
						System.out.println(results[i]);
					}
					System.out.println();

					// download, locally, the first result file
					File localResultFile = new File("chart.pdf");
					System.out.print("Writing result file to ");
					System.out.println(localResultFile.getAbsolutePath());
					System.out.println();

					pp.getRemoteFileManager().downloadFile(results[0], localResultFile);
				}

				System.out.println("Job files will be deleted after you press <Enter>");
				System.out.print(">>>>");
				System.in.read();
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
			if (protocol != null) {
				try {
					System.out.println("Deleting job " + protocol.getJobId());
					protocol.releaseJob();
				} catch (Exception ex) {
				}
			}
		}

	}
}
