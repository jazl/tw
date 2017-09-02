package com.fortify.fod.remediation.models;

import java.awt.*;

public class VulnFolder {
    private String id;
    private String title;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VulnFolder(String id, String title){
        this.id = id;
        this.title = title;
    }
}
