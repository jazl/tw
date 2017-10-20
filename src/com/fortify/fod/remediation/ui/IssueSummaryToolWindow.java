package com.fortify.fod.remediation.ui;

import com.fortify.fod.remediation.custom.HtmlEditorPane;
import com.fortify.fod.remediation.messages.IssueChangeInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class IssueSummaryToolWindow extends RemediationToolWindowBase {

    private int _width;
    private final String mockAuditDetails = "<html><body><p><span font=\"header\"><h3>Abstract</h3></span></p><p>Hardcoded passwords may compromise system security in a way that cannot be easily remedied.</p><p><span font=\"header\"><h3>Explanation</h3></span></p><p>It is never a good idea to hardcode a password. Not only does hardcoding a password allow all of the project's developers to view the password, it also makes fixing the problem extremely difficult. Once the code is in production, the password cannot be changed without patching the software. If the account protected by the password is compromised, the owners of the system will be forced to choose between security and availability.</p><p>" +
        "In this case a hardcoded password was found in the call to <b>getConnection()</b> in <b>CreateDB.java</b> at line <b>33</b>." +
        "</p><p><b>Example 1:</b> The following code uses a hardcoded password to connect to a database:</p><p><span font=\"code\"></span> <span font=\"code\">...</span> <span font=\"code\">DriverManager.getConnection(url, \"scott\", \"tiger\");</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>This code will run successfully, but anyone who has access to it will have access to the password. Once the program has shipped, there is likely no way to change the database user \"scott\" with a password of \"tiger\" unless the program is patched. An employee with access to this information could use it to break into the system. Even worse, if attackers have access to the bytecode for the application they can use the <b>javap -c</b> command to access the disassembled code, which will contain the values of the passwords used. The result of this operation might look something like the following for the example above:</p><p><span font=\"code\"></span> <span font=\"code\">javap -c ConnMngr.class</span> <span font=\"code\">22: ldc  #36; //String jdbc:mysql://ixne.com/rxsql</span> <span font=\"code\">24: ldc  #38; //String scott</span> <span font=\"code\">26: ldc  #17; //String tiger</span> <span font=\"code\"></span></p><p>In the mobile world, password management is even trickier, considering a much higher chance of device loss." +
        "<b>Example 2:</b> The code below uses hardcoded username and password to setup authentication for viewing protected pages with Android's WebView." +
        "</p><p><span font=\"code\"></span> <span font=\"code\">...</span> <span font=\"code\">webview.setWebViewClient(new WebViewClient() {</span> <span font=\"code\">public void onReceivedHttpAuthRequest(WebView view,</span> <span font=\"code\">HttpAuthHandler handler, String host, String realm) {</span> <span font=\"code\">handler.proceed(\"guest\", \"allow\");</span> <span font=\"code\">}</span> <span font=\"code\">});</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>Similar to Example 1, this code will run successfully, but anyone who has access to it will have access to the password.</p><p><p></p><b>Instance ID:</b> 629FAA1A3656CCB183EEDF74AA2A6378</p><p><b>Primary Rule ID:</b> CFD5D7C5-08EB-4D20-AE29-28C05CDA4B92</p><p><b>Issue Scan Type:</b> Static</p><p><b>Priority Metadata Values</b></p><p><b>Impact:</b> 4.0</p><p><b>Probability:</b> 4.0</p><p><b>Legacy Priority Metadata Values</b></p><p><b>Severity:</b> 4.0</p><p><b>Confidence:</b> 5.0</p></body></html>";

    private final String mockRecommendations = "<html><body><p><span font=\"header\">Recommendation</span></p><p>Passwords should never be hardcoded and should generally be obfuscated and managed in an external source. Storing passwords in plaintext anywhere on the system allows anyone with sufficient permissions to read and potentially misuse the password. At the very least, passwords should be hashed before being stored.</p><p>Some third-party products claim the ability to manage passwords in a more secure way. For example, WebSphere Application Server 4.x uses a simple XOR encryption algorithm for obfuscating values, but be skeptical about such facilities. WebSphere and other application servers offer outdated and relatively weak encryption mechanisms that are insufficient for security-sensitive environments. For a secure generic solution, the best option today appears to be a proprietary mechanism that you create.</p><p>For Android, as well as any other platform that uses SQLite database, a good option is SQLCipher -- an extension to SQLite database that provides transparent 256-bit AES encryption of database files. Thus, credentials can be stored in an encrypted database.</p><p><b>Example 3:</b> The code below demonstrates how to integrate SQLCipher into an Android application after downloading the necessary binaries, and store credentials into the database file.\n" +
        "</p><p><span font=\"code\"></span> <span font=\"code\">import net.sqlcipher.database.SQLiteDatabase;</span> <span font=\"code\">...</span> <span font=\"code\">SQLiteDatabase.loadLibs(this);</span> <span font=\"code\">File dbFile = getDatabasePath(\"credentials.db\");</span> <span font=\"code\">dbFile.mkdirs();</span> <span font=\"code\">dbFile.delete();</span> <span font=\"code\">SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, \"credentials\", null);</span> <span font=\"code\">db.execSQL(\"create table credentials(u, p)\");</span> <span font=\"code\">db.execSQL(\"insert into credentials(u, p) values(?, ?)\", new Object[]{username, password});</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>Note that references to <b>android.database.sqlite.SQLiteDatabase</b> are substituted with those of <b>net.sqlcipher.database.SQLiteDatabase</b>.</p><p>To enable encryption on the WebView store, WebKit has to be re-compiled with the <b>sqlcipher.so</b> library.</p><p><span font=\"header\">Tips</span></p><li>The Fortify Java Annotations FortifyPassword and FortifyNotPassword can be used to indicate which fields and variables represent passwords.</li><li>When identifying null, empty, or hardcoded passwords, default rules only consider fields and variables that contain the word <b>password</b>. However, the Custom Rules Editor provides the Password Management wizard that makes it easy to create rules for detecting password management issues on custom-named fields and variables.</li><p><span font=\"header\">References</span></p><li>A6 Sensitive Data Exposure, Standards Mapping - OWASP Top 10 2013 - (OWASP 2013)</li><li>A7 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2010 - (OWASP 2010)</li><li>A8 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2007 - (OWASP 2007)</li><li>A8 Insecure Storage, Standards Mapping - OWASP Top 10 2004 - (OWASP 2004)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3 - (STIG 3)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.4 - (STIG 3.4)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.5 - (STIG 3.5)</li><li>CWE ID 259, CWE ID 798, Standards Mapping - Common Weakness Enumeration - (CWE)</li><li>IA, Standards Mapping - FIPS200 - (FISMA)</li><li>Porous Defenses - CWE ID 259, Standards Mapping - SANS Top 25 2009 - (SANS 2009)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2010 - (SANS 2010)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2011 - (SANS Top 25 2011)</li><li>Requirement 3.4, Requirement 6.3.1.3, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.2 - (PCI 1.2)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.2.1, Standards Mapping - Payment Card Industry Data Security Standard Version 3.0 - (PCI 3.0)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 2.0 - (PCI 2.0)</li><li>Requirement 3.4, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.1 - (PCI 1.1)</li><li>SC-28 Protection of Information at Rest, Standards Mapping - NIST Special Publication 800-53 Revision 4 - (NIST SP 800-53 Rev.4)</li><li>SQLCipher., <a>http://sqlcipher.net/</a></li></body></html>";

    private HtmlEditorPane detailsLabel = null;
    private JEditorPane detailsPane = null;
    private HtmlEditorPane recommendationsLabel = null;
    private JBScrollPane scrollPane = null;

    private JPanel createDetailsContent(){
        JPanel panel = new JPanel(new BorderLayout());

        JBScrollPane jsp = new JBScrollPane(detailsLabel) {
            @Override
            public Dimension getPreferredSize() {
                setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                Dimension dim = new Dimension(super.getPreferredSize().width + getVerticalScrollBar().getSize().width, 500);
                setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                return dim;
            }
        };

        panel.add(new JBScrollPane(detailsLabel), BorderLayout.CENTER);
        return panel;
    }
    private JPanel createRecommendationsContent(){

        JPanel panel = new JPanel(new BorderLayout());
        scrollPane = new JBScrollPane(recommendationsLabel);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                int width = arg0.getComponent().getWidth();
                System.out.println("width = "+width);
                _width = width;
                //scrollPane.setSize(arg0.getComponent().getWidth(), 0);
                recommendationsLabel.setSize(_width, recommendationsLabel.getHeight());
                scrollPane.revalidate();
            }
        });


        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = getDefaultToolWindowContentPanel();

        panel.add(headerLabel, BorderLayout.NORTH);

        System.out.println(".............. createToolWindowContent after add, headerLabel = "+headerLabel.getText());

        JBTabbedPane tab = new JBTabbedPane();
        tab.addTab("Details", createDetailsContent());
        tab.addTab("Recommendations", createRecommendationsContent());
        panel.add(tab, BorderLayout.CENTER);

        addContent(toolWindow, panel);

        toolWindow.setTitle(project.getName()+" - "+this.hashCode());
    }

    @Override
    protected void onIssueChange(IssueChangeInfo changeInfo) {
        headerLabel.setText(changeInfo.getIssueName());
        detailsLabel.setHtmlContent(mockAuditDetails);
        recommendationsLabel.setHtmlContent(mockRecommendations);

        System.out.println(".............. onIssueChange after add, headerLabel = "+headerLabel.getText());

        detailsLabel.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType()== HyperlinkEvent.EventType.ACTIVATED) {
                    System.out.println("Link activated: "+e.getURL());
                }
            }
        });
    }

    @Override
    protected void onFoDProjectChange(String msg) {
        toggleContent();
    }

    @Override
    public void init(ToolWindow window) {
        super.init(window);
        setToolWindowId("Issue Summary");

        detailsLabel = new HtmlEditorPane();
        recommendationsLabel = new HtmlEditorPane();
    }
}
