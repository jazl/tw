package com.azl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

public class ShowTw extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        //ToolWindowManager.getInstance(project).registerToolWindow("Issues",false, ToolWindowAnchor.BOTTOM);

        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("Issues");
        tw.activate(null);

        ToolWindow tw2 = ToolWindowManager.getInstance(project).getToolWindow("Analysis");
        tw2.activate(null);
    }
}
