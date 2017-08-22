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

        Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();

        LogicalPosition logicalPosition = caretModel.getLogicalPosition();
        VisualPosition visualPosition = caretModel.getVisualPosition();
        int offset = caretModel.getOffset();
        Messages.showInfoMessage(logicalPosition.toString() + "\n" +
                visualPosition.toString() + "\n" +
                "Offset: " + offset, "Caret Parameters Inside The Editor");

        //editor.visualPositionToXY(new VisualPosition(15,0));
        editor.visualPositionToXY(editor.getCaretModel().getVisualPosition());

        ToolWindow tw = ToolWindowManager.getInstance(project).getToolWindow("Issues");
        tw.activate(null);

        ToolWindow tw2 = ToolWindowManager.getInstance(project).getToolWindow("Analysis Results");
        tw2.activate(null);

        ToolWindow tw3 = ToolWindowManager.getInstance(project).getToolWindow("Analysis Trace");
        tw3.activate(null);
    }
}
