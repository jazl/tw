package com.fortify.fod.remediation;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jazl on 9/30/2017.
 */
public class RemediationProjectComponent implements ProjectComponent {

    public RemediationProjectComponent(Project project) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component constructor, project "+project.getName()+" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void projectOpened() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component projectOpened <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void projectClosed() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component projectClosed <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void initComponent() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component initComponent <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void disposeComponent() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component disposeComponent <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RemediationProjectComponent";
    }
}