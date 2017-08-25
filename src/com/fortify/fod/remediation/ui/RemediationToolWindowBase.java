package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.messages.ChangeActionNotifier;
import com.fortify.fod.remediation.RemediationPluginService;
import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class RemediationToolWindowBase implements ToolWindowFactory {

    private Object selectedFoDProject = null;
    private Content content = null;
    private Content uninitializedContent = null;
    private String toolWindowId = "";
    // predefined layouts for use with all tool windows
    private Border defaultToolWindowBorder = new EmptyBorder(5,5,5,5);
    private BorderLayout defaultToolWindowBorderLayout = new BorderLayout(5,5);

    protected Project project = null;
    protected JLabel headerLabel = new JLabel();
    protected static RemediationPluginService remediationPluginService = ServiceManager.getService(RemediationPluginService.class);

    public RemediationToolWindowBase() {
        headerLabel.setText("<Select Vulnerability>");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // In subclass, call super and add view-specific code

        if(selectedFoDProject == null) {
            return;
        }
    }

    @Override
    public void init(ToolWindow window) {
        // If implemented in subclass, call super!!

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if(openProjects != null) {
            System.out.println("openProjects.length = "+openProjects.length);
            project = openProjects[0];
        }

        Application application = ApplicationManager.getApplication();
        MessageBus bus = application.getMessageBus();

        bus.connect().subscribe(ChangeActionNotifier.CHANGE_ACTION_TOPIC, new ChangeActionNotifier() {
            @Override
            public void onProjectChanged(String msg) {
                onFoDProjectChange(msg);
            }
            @Override
            public void onIssueChanged(IssueChangeInfo msg) {
                onIssueChange(msg);
            }
        });
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }

    protected boolean isProjectSelected() {
        return selectedFoDProject != null;
    }

    // Will be called in subclass when selected issue changes
    protected abstract void onIssueChange(IssueChangeInfo changeInfo);

    // Will be called in subclass when project selected
    protected abstract void onFoDProjectChange(String msg);

    // TODO: find a way to get this automatically!!
    protected void setToolWindowId(String toolWindowId) {
        this.toolWindowId = toolWindowId;
    }

    protected void addContent(ToolWindow toolWindow, JPanel panel) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        content = contentFactory.createContent(panel,"",false);
        uninitializedContent = contentFactory.createContent(getDefaultPanel(),"",false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(isProjectSelected() ? content : uninitializedContent);
    }

    protected void toggleContent() {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(toolWindowId);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);
        contentManager.addContent(content);
    }

    private JPanel getDefaultPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel openFoDLabel = new JLabel("<html><a href='#'>Open Fortify on Demand Analysis Results</a></html>");
        openFoDLabel.setHorizontalAlignment(SwingConstants.CENTER);
        openFoDLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openFoDLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                remediationPluginService.publishFoDProjectChange("Project has been selected");
            }
        });

        panel.add(openFoDLabel);
        return panel;
    }

    protected Border getDefaultToolWindowBorder(){
        return defaultToolWindowBorder;
    }

    protected BorderLayout getDefaultBorderLayout(){
        return defaultToolWindowBorderLayout;
    }

    protected JPanel getDefaultToolWindowContentPanel() {
        JPanel panel = new JPanel(getDefaultBorderLayout());
        panel.setBorder(getDefaultToolWindowBorder());
        return panel;
    }
}
