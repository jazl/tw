package com.fortify.fod.remediation.custom;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;

/**
 * Created by jazl on 9/30/2017.
 */
public class HtmlEditorPane extends JEditorPane {
    private HTMLDocument htmlDoc;
    public HtmlEditorPane() {
        super();

        HTMLEditorKit htmlKit = new HTMLEditorKit();
        htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);

        setEditorKit(htmlKit);
        setDocument(htmlDoc);
        setEditable(false);
    }

    public void setHtmlContent(String htmlContent) {
        Element htmlElement = htmlDoc.getRootElements()[0];
        try {
            htmlDoc.setInnerHTML(htmlElement, htmlContent);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
