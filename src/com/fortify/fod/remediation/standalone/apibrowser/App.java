package com.fortify.fod.remediation.standalone.apibrowser;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jazl on 11/11/17.
 */
public class App extends JFrame {

    public App() {
        setTitle("API Browser");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setSize(800,600);
        setLayout(new BorderLayout());
        add(createBrowserPanels(), BorderLayout.CENTER);
    }

    private JPanel createBrowserPanels() {
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.add(new AnalysisResultsBrowserPanel());
        mainPanel.add(new AnalysisTraceBrowserPanel());
        mainPanel.add(new AuditSummaryBrowserPanel());
        mainPanel.add(new AnalysisSummaryBrowserPanel());
        return mainPanel;
    }

    public static void main(String[] args) {
        App app = new App();
        app.setVisible(true);
    }
}
