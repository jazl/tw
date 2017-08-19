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

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IssuesToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content formContent = contentFactory.createContent(getFormContent(),"Content1",false);
        Content treeContent = contentFactory.createContent(getTreePanel(),"Content2",false);

        toolWindow.getContentManager().addContent(treeContent);
        toolWindow.getContentManager().addContent(formContent);
    }

    private JPanel getTreePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

//        DefaultTreeModel model = new DefaultTreeModel();

        Tree tree = new Tree();
        tree.setShowsRootHandles(true);
//        tree.setModel(model);

        panel.add(new JBScrollPane(tree), BorderLayout.CENTER);
        return panel;
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
                Application application = ApplicationManager.getApplication();
                MessageBus bus = application.getMessageBus();
                ChangeActionNotifier publisher = bus.syncPublisher(ChangeActionNotifier.CHANGE_ACTION_TOPIC);
                publisher.beforeAction("oh hai!");
                publisher.afterAction("Sharon!");
            }
        });
        panel.add(button);
        return panel;
    }

    @Override
    public void init(ToolWindow window) {

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
