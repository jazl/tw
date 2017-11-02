package com.fortify.fod.remediation.custom;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class GroupByComponent extends JComboBox<String> {

    private static JComboBox<String> groupByCombo;

    public static JComboBox<String> getInstance() {
        if(groupByCombo == null) {
            groupByCombo = new ComboBox<>();
            setGroupByData();
        }
        return groupByCombo;
    }

    public static void setGroupByData() {
        DefaultComboBoxModel<String> groupByModel = new DefaultComboBoxModel<>();
        groupByModel.addElement("analysisType");
        groupByModel.addElement("assignedUser");
        groupByModel.addElement("auditPendingAuditorStatus");
        groupByModel.addElement("auditorStatus");
        groupByModel.addElement("category");
        groupByCombo.setModel(groupByModel);
    }
}
