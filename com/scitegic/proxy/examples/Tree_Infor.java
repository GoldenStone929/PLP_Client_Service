package com.scitegic.proxy.examples;

import com.scitegic.proxy.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Tree_Infor {

    static final String WEB_PORT_EXAMPLE_PROTOCOLS = "Protocols/Web Services/Web Port Examples";

    public Tree_Infor() {
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

        try {
            pp = new PipelinePilotServer(server, user, password);

            XmldbItem rootFolder = pp.getComponentDatabase().getXmldbContentsRecursive(WEB_PORT_EXAMPLE_PROTOCOLS);
            DefaultMutableTreeNode treeRoot = buildTreeRecursive(rootFolder, 0);

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("File Structure");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());

                JTree tree = new JTree(treeRoot);
                JScrollPane scrollPane = new JScrollPane(tree);
                frame.add(scrollPane, BorderLayout.CENTER);

                tree.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
                        if (tp != null && tp.getLastPathComponent() != null) {
                            String filename = tp.getLastPathComponent().toString();
                            // When a file is clicked in the tree, show the corresponding job pane
                            JobPane.showJobPane(filename);
                        }
                    }
                });

                frame.setSize(500, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
