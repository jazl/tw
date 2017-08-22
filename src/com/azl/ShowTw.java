package com.azl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

public class ShowTw extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        //ToolWindowManager.getInstance(project).registerToolWindow("Issues",false, ToolWindowAnchor.BOTTOM);

        ToolWindow results = ToolWindowManager.getInstance(project).getToolWindow("Analysis Results");
        results.activate(null);

        ToolWindow summary = ToolWindowManager.getInstance(project).getToolWindow("Issue Summary");
        summary.activate(null);

        ToolWindow trace = ToolWindowManager.getInstance(project).getToolWindow("Analysis Trace");
        trace.activate(null);

        ToolWindow auditSummary = ToolWindowManager.getInstance(project).getToolWindow("Audit Summary");
        auditSummary.activate(null);
    }
}
