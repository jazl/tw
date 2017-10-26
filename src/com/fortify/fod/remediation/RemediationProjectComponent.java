package com.fortify.fod.remediation;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jazl on 9/30/2017.
 */
public class RemediationProjectComponent implements ProjectComponent {

    private Object someObject = null;

    public RemediationProjectComponent(Project project) {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component constructor, project "+project.getName()+" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void projectOpened() {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component projectOpened <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void projectClosed() {
        someObject = null;
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component projectClosed <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void initComponent() {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component initComponent <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @Override
    public void disposeComponent() {
        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> project component disposeComponent <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RemediationProjectComponent";
    }

    public boolean isLoggedIn() {
        return someObject != null;
    }

    public void setLogin(boolean isLoggedIn) {
        someObject = isLoggedIn ? new Object() : null;
    }

}