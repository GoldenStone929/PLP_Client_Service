package com.scitegic.proxy.examples;


import com.scitegic.proxy.ComponentDatabase;
import com.scitegic.proxy.PipelinePilotServer;
import com.scitegic.proxy.XmldbItem;
import com.scitegic.proxy.PipelinePilotServerConfig;
import com.scitegic.proxy.Job;


import com.scitegic.proxy.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Tree_Infor {

    static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";
    static final String PROTOCOL = WEB_PORT_EXAMPLE_PROTOCOLS + "/Generic/XY Scatter Plot Utility";

    public Tree_Infor() {}

    private static DefaultMutableTreeNode buildTreeRecursive(XmldbItem folder) {
        DefaultMutableTreeNode currentFolder = new DefaultMutableTreeNode(folder.getName());
        XmldbItem[] children = folder.getChildren();
        for (XmldbItem child : children) {
            if (child.isFolder()) {
                currentFolder.add(buildTreeRecursive(child));
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
            DefaultMutableTreeNode treeRoot = buildTreeRecursive(rootFolder);

            JTree tree = new JTree(treeRoot);
            tree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
                    if (tp != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
                        if (node.isLeaf()) {
                            new JobPane(node.getUserObject().toString());
                        }
                    }
                }
            });

            JFrame frame = new JFrame("File Structure");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(tree));
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

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
