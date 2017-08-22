package com.azl;

import com.intellij.codeInspection.ui.OptionAccessor;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class IssuesToolWindowFactory implements ToolWindowFactory {

    MessageBus messageBus = null;
    ChangeActionNotifier publisher = null;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content formContent = contentFactory.createContent(getFormContent(),"Form",false);
        Content treeContent = contentFactory.createContent(getTreePanel(),"Tree",false);

        toolWindow.getContentManager().addContent(treeContent);
        toolWindow.getContentManager().addContent(formContent);
    }

    private JPanel getTreePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Vehicles");
        createNodes(root);
        Tree tree = new Tree(root);
        tree.setShowsRootHandles(true);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                publisher.afterAction(selectedNode.toString());

            }
        });

        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);
        return panel;
    }

    private DefaultMutableTreeNode createCategoryNodeAndChildren(String catNodeName, HashMap<String, ArrayList> vulnerabilities) {
        final DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(catNodeName);

        vulnerabilities.forEach(new BiConsumer<String, ArrayList>() {
            @Override
            public void accept(String vulnName, ArrayList traces) {
                DefaultMutableTreeNode vulnNodes = new DefaultMutableTreeNode(vulnName);
                for(int i=0; i<traces.size(); i++) {
                    vulnNodes.add(new DefaultMutableTreeNode(traces.get(i)));
                }
                catNode.add(vulnNodes);
            }
        });

        return catNode;
    }

    private void createNodes(DefaultMutableTreeNode root) {
        HashMap<String, ArrayList> vulnerabilities;
        ArrayList<String> traceNodes;

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        vulnerabilities = new HashMap<>();
        vulnerabilities.put("Exec.java:103", traceNodes);
        root.add(createCategoryNodeAndChildren("Command Injection (3)", vulnerabilities));

        traceNodes = new ArrayList<>();
        traceNodes.add("ParameterParser.java:593 - getParameterValues(return)");
        traceNodes.add("ParameterParser.java:593 - Return");
        traceNodes.add("WSDLScanning.java:201 - getParameterValues(return)");
        vulnerabilities = new HashMap<>();
        vulnerabilities.put("Exec.java:103", traceNodes);
        root.add(createCategoryNodeAndChildren("Command Injection", vulnerabilities));

    }

    private JPanel getFormContent() {
        JPanel panel = new JPanel();
        String txt = "<html><h2>Tool Window!</h2><hr/><b>Hello</b> from <a href='#'>toolwindow</a></html>";
        panel.add(new JLabel(txt));

        ComboBox combo = new ComboBox();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Sharon H");
        model.addElement("Susan Huffstutter");
        model.addElement("Heidi Clever");
        model.addElement("Sarah B");
        combo.setModel(model);
        panel.add(combo);

        JButton button = new JButton("Send Message");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sending message...");
            }
        });
        panel.add(button);
        return panel;
    }

    @Override
    public void init(ToolWindow window) {
        Application application = ApplicationManager.getApplication();
        messageBus = application.getMessageBus();
        publisher = messageBus.syncPublisher(ChangeActionNotifier.CHANGE_ACTION_TOPIC);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }
}
