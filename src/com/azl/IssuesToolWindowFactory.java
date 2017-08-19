package com.azl;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.jetbrains.annotations.NotNull;
import org.sonatype.nexus.index.treeview.DefaultTreeNode;

import javax.enterprise.inject.Default;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private void createNodes(DefaultMutableTreeNode root) {
        DefaultMutableTreeNode vehicleTypes = null;
        DefaultMutableTreeNode vehicle = null;

        vehicleTypes = new DefaultMutableTreeNode("SUVs");
        root.add(vehicleTypes);

        vehicle = new DefaultMutableTreeNode("Jeep Grand Cherokee");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Ford Explorer");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Toyota 4Runner");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Chevy Tahoe");
        vehicleTypes.add(vehicle);

        vehicleTypes = new DefaultMutableTreeNode("Sedans");
        root.add(vehicleTypes);

        vehicle = new DefaultMutableTreeNode("BMW 540i");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Nissan Altima");
        vehicleTypes.add(vehicle);

        vehicleTypes = new DefaultMutableTreeNode("Trucks");
        root.add(vehicleTypes);

        vehicle = new DefaultMutableTreeNode("Toyota Tacoma");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Nissan Frontier");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Ford F-150");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Dodge RAM");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Chevy Silverado");
        vehicleTypes.add(vehicle);

        vehicleTypes = new DefaultMutableTreeNode("Performance");
        root.add(vehicleTypes);

        vehicle = new DefaultMutableTreeNode("Chevrolet SS");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Ford Mustang GT");
        vehicleTypes.add(vehicle);
        vehicle = new DefaultMutableTreeNode("Mazda Miata MX-5");
        vehicleTypes.add(vehicle);

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
