package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.models.VulnFolder;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class VulnTabbedPane extends JTabbedPane {

    HashMap<String, VulnFolder> vulnMap = new HashMap<>();

    public void addTabPanel(String title, AnalysisResultsTabPanel panel, VulnFolder folder) {
        Icon icon = null;
        try {
            icon = IconLoader.getIcon("/icons/"+title.toLowerCase()+".png");
        }
        catch(Exception e) {
            System.out.println("Cannot find icon for "+title);
        }
        super.addTab(title, icon, panel);
        vulnMap.put(title, folder);
    }

    public void setGroupByComponent(JComboBox<String> comp) {
        AnalysisResultsTabPanel tabPanel = (AnalysisResultsTabPanel)getComponent(0);
        tabPanel.setGroupByComponent(comp);
    }
}