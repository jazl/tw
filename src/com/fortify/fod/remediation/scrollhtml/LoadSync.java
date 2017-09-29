package com.fortify.fod.remediation.scrollhtml;


/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski
ISBN: 1-893115-78-X
Publisher: APress
*/

import oracle.jrockit.jfr.JFR;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class LoadSync {

    static String mockAuditDetails = "<html><body><p><span font=\"header\"><h3>Abstract</h3></span></p><p>Hardcoded passwords may compromise system security in a way that cannot be easily remedied.</p><p><span font=\"header\"><h3>Explanation</h3></span></p><p>It is never a good idea to hardcode a password. Not only does hardcoding a password allow all of the project's developers to view the password, it also makes fixing the problem extremely difficult. Once the code is in production, the password cannot be changed without patching the software. If the account protected by the password is compromised, the owners of the system will be forced to choose between security and availability.</p><p>" +
            "In this case a hardcoded password was found in the call to <b>getConnection()</b> in <b>CreateDB.java</b> at line <b>33</b>." +
            "</p><p><b>Example 1:</b> The following code uses a hardcoded password to connect to a database:</p><p><span font=\"code\"></span> <span font=\"code\">...</span> <span font=\"code\">DriverManager.getConnection(url, \"scott\", \"tiger\");</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>This code will run successfully, but anyone who has access to it will have access to the password. Once the program has shipped, there is likely no way to change the database user \"scott\" with a password of \"tiger\" unless the program is patched. An employee with access to this information could use it to break into the system. Even worse, if attackers have access to the bytecode for the application they can use the <b>javap -c</b> command to access the disassembled code, which will contain the values of the passwords used. The result of this operation might look something like the following for the example above:</p><p><span font=\"code\"></span> <span font=\"code\">javap -c ConnMngr.class</span> <span font=\"code\">22: ldc  #36; //String jdbc:mysql://ixne.com/rxsql</span> <span font=\"code\">24: ldc  #38; //String scott</span> <span font=\"code\">26: ldc  #17; //String tiger</span> <span font=\"code\"></span></p><p>In the mobile world, password management is even trickier, considering a much higher chance of device loss." +
            "<b>Example 2:</b> The code below uses hardcoded username and password to setup authentication for viewing protected pages with Android's WebView." +
            "</p><p><span font=\"code\"></span> <span font=\"code\">...</span> <span font=\"code\">webview.setWebViewClient(new WebViewClient() {</span> <span font=\"code\">public void onReceivedHttpAuthRequest(WebView view,</span> <span font=\"code\">HttpAuthHandler handler, String host, String realm) {</span> <span font=\"code\">handler.proceed(\"guest\", \"allow\");</span> <span font=\"code\">}</span> <span font=\"code\">});</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>Similar to Example 1, this code will run successfully, but anyone who has access to it will have access to the password.</p><p><p></p><b>Instance ID:</b> 629FAA1A3656CCB183EEDF74AA2A6378</p><p><b>Primary Rule ID:</b> CFD5D7C5-08EB-4D20-AE29-28C05CDA4B92</p><p><b>Issue Scan Type:</b> Static</p><p><b>Priority Metadata Values</b></p><p><b>Impact:</b> 4.0</p><p><b>Probability:</b> 4.0</p><p><b>Legacy Priority Metadata Values</b></p><p><b>Severity:</b> 4.0</p><p><b>Confidence:</b> 5.0</p></body></html>";

    static String mockRecommendations = "<html><body><p><span font=\"header\">Recommendation</span></p><p>Passwords should never be hardcoded and should generally be obfuscated and managed in an external source. Storing passwords in plaintext anywhere on the system allows anyone with sufficient permissions to read and potentially misuse the password. At the very least, passwords should be hashed before being stored.</p><p>Some third-party products claim the ability to manage passwords in a more secure way. For example, WebSphere Application Server 4.x uses a simple XOR encryption algorithm for obfuscating values, but be skeptical about such facilities. WebSphere and other application servers offer outdated and relatively weak encryption mechanisms that are insufficient for security-sensitive environments. For a secure generic solution, the best option today appears to be a proprietary mechanism that you create.</p><p>For Android, as well as any other platform that uses SQLite database, a good option is SQLCipher -- an extension to SQLite database that provides transparent 256-bit AES encryption of database files. Thus, credentials can be stored in an encrypted database.</p><p><b>Example 3:</b> The code below demonstrates how to integrate SQLCipher into an Android application after downloading the necessary binaries, and store credentials into the database file.\n" +
            "</p><p><span font=\"code\"></span> <span font=\"code\">import net.sqlcipher.database.SQLiteDatabase;</span> <span font=\"code\">...</span> <span font=\"code\">SQLiteDatabase.loadLibs(this);</span> <span font=\"code\">File dbFile = getDatabasePath(\"credentials.db\");</span> <span font=\"code\">dbFile.mkdirs();</span> <span font=\"code\">dbFile.delete();</span> <span font=\"code\">SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, \"credentials\", null);</span> <span font=\"code\">db.execSQL(\"create table credentials(u, p)\");</span> <span font=\"code\">db.execSQL(\"insert into credentials(u, p) values(?, ?)\", new Object[]{username, password});</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>Note that references to <b>android.database.sqlite.SQLiteDatabase</b> are substituted with those of <b>net.sqlcipher.database.SQLiteDatabase</b>.</p><p>To enable encryption on the WebView store, WebKit has to be re-compiled with the <b>sqlcipher.so</b> library.</p><p><span font=\"header\">Tips</span></p><li>The Fortify Java Annotations FortifyPassword and FortifyNotPassword can be used to indicate which fields and variables represent passwords.</li><li>When identifying null, empty, or hardcoded passwords, default rules only consider fields and variables that contain the word <b>password</b>. However, the Custom Rules Editor provides the Password Management wizard that makes it easy to create rules for detecting password management issues on custom-named fields and variables.</li><p><span font=\"header\">References</span></p><li>A6 Sensitive Data Exposure, Standards Mapping - OWASP Top 10 2013 - (OWASP 2013)</li><li>A7 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2010 - (OWASP 2010)</li><li>A8 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2007 - (OWASP 2007)</li><li>A8 Insecure Storage, Standards Mapping - OWASP Top 10 2004 - (OWASP 2004)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3 - (STIG 3)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.4 - (STIG 3.4)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.5 - (STIG 3.5)</li><li>CWE ID 259, CWE ID 798, Standards Mapping - Common Weakness Enumeration - (CWE)</li><li>IA, Standards Mapping - FIPS200 - (FISMA)</li><li>Porous Defenses - CWE ID 259, Standards Mapping - SANS Top 25 2009 - (SANS 2009)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2010 - (SANS 2010)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2011 - (SANS Top 25 2011)</li><li>Requirement 3.4, Requirement 6.3.1.3, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.2 - (PCI 1.2)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.2.1, Standards Mapping - Payment Card Industry Data Security Standard Version 3.0 - (PCI 3.0)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 2.0 - (PCI 2.0)</li><li>Requirement 3.4, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.1 - (PCI 1.1)</li><li>SC-28 Protection of Information at Rest, Standards Mapping - NIST Special Publication 800-53 Revision 4 - (NIST SP 800-53 Rev.4)</li><li>SQLCipher., <a>http://sqlcipher.net/</a></li></body></html>";

    public static void main(String args[]) {
        final String filename = "Test.html";
        JFrame frame = new JFrame("Loading/Saving Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container content = frame.getContentPane();

        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        content.add(scrollPane, BorderLayout.CENTER);

        editorPane.setEditorKit(new HTMLEditorKit());

        JPanel panel = new JPanel();

        // Setup actions
        Action loadAction = new AbstractAction() {
            {
                putValue(Action.NAME, "Load");
            }

            public void actionPerformed(ActionEvent e) {
                doLoadCommand(editorPane, filename);
            }
        };
        JButton loadButton = new JButton(loadAction);
        panel.add(loadButton);

        content.add(panel, BorderLayout.SOUTH);

        frame.setSize(250, 150);
        frame.setVisible(true);
    }

    public static void doLoadCommand(JTextComponent textComponent, String filename) {
        FileReader reader = null;
        try {
            System.out.println("Loading");
            //reader = new FileReader(filename);

            // Create empty HTMLDocument to read into
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
            // Create parser (javax.swing.text.html.parser.ParserDelegator)
            HTMLEditorKit.Parser parser = new ParserDelegator();
            // Get parser callback from document
            HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
            // Load it (true means to ignore character set)
            //parser.parse(reader, callback, true);

            // Replace document
            textComponent.setDocument(htmlDoc);
            Element htmlElement = htmlDoc.getRootElements()[0];

            htmlDoc.setInnerHTML(htmlElement, mockAuditDetails);
            System.out.println("Loaded");

        } catch (IOException exception) {
            System.out.println("Load oops");
            exception.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignoredException) {
                }
            }
        }
    }
}