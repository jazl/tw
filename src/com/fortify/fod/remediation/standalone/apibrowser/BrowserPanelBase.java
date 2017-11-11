package com.fortify.fod.remediation.standalone.apibrowser;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by jazl on 11/11/17.
 */
public class BrowserPanelBase extends JPanel {
    protected String _panelTitle;

    public BrowserPanelBase() {
        setPreferredSize(new Dimension(350,200));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    }

    protected void setTitle(String title) {
        _panelTitle = title;
        setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), _panelTitle));
    }
}
