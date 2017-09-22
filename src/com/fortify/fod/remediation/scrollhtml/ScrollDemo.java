package com.fortify.fod.remediation.scrollhtml;

import javax.swing.*;
import java.awt.*;

/**
 * Created by jazl on 9/22/2017.
 */
public class ScrollDemo {
    private static JDialog dialog;

    public static void main(String[] args) {
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setTitle("Scroll Demo");
        dialog.setLocationRelativeTo(null);

        dialog.add(getMainPanel());
        dialog.setSize(400,400);
        dialog.setVisible(true);
    }

    private static JPanel getMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Details", null, getTabPanel(), null);
        tabbedPane.addTab("Recommendations", null, getTabPanel(), null);

        mainPanel.add(tabbedPane);

        return mainPanel;
    }

    private static JPanel getTabPanel() {
        JPanel tabPanel = new JPanel(new BorderLayout());
        JLabel lblNewLabel = new JLabel("<html><body><p><span font=\"header\">Recommendation</span></p><p>Passwords should never be hardcoded and should generally be obfuscated and managed in an external source. Storing passwords in plaintext anywhere on the system allows anyone with sufficient permissions to read and potentially misuse the password. At the very least, passwords should be hashed before being stored.</p><p>Some third-party products claim the ability to manage passwords in a more secure way. For example, WebSphere Application Server 4.x uses a simple XOR encryption algorithm for obfuscating values, but be skeptical about such facilities. WebSphere and other application servers offer outdated and relatively weak encryption mechanisms that are insufficient for security-sensitive environments. For a secure generic solution, the best option today appears to be a proprietary mechanism that you create.</p><p>For Android, as well as any other platform that uses SQLite database, a good option is SQLCipher -- an extension to SQLite database that provides transparent 256-bit AES encryption of database files. Thus, credentials can be stored in an encrypted database.</p><p><b>Example 3:</b> The code below demonstrates how to integrate SQLCipher into an Android application after downloading the necessary binaries, and store credentials into the database file.\r\n\" +\r\n        \"</p><p><span font=\"code\"></span> <span font=\"code\">import net.sqlcipher.database.SQLiteDatabase;</span> <span font=\"code\">...</span> <span font=\"code\">SQLiteDatabase.loadLibs(this);</span> <span font=\"code\">File dbFile = getDatabasePath(\"credentials.db\");</span> <span font=\"code\">dbFile.mkdirs();</span> <span font=\"code\">dbFile.delete();</span> <span font=\"code\">SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, \"credentials\", null);</span> <span font=\"code\">db.execSQL(\"create table credentials(u, p)\");</span> <span font=\"code\">db.execSQL(\"insert into credentials(u, p) values(?, ?)\", new Object[]{username, password});</span> <span font=\"code\">...</span> <span font=\"code\"></span></p><p>Note that references to <b>android.database.sqlite.SQLiteDatabase</b> are substituted with those of <b>net.sqlcipher.database.SQLiteDatabase</b>.</p><p>To enable encryption on the WebView store, WebKit has to be re-compiled with the <b>sqlcipher.so</b> library.</p><p><span font=\"header\">Tips</span></p><li>The Fortify Java Annotations FortifyPassword and FortifyNotPassword can be used to indicate which fields and variables represent passwords.</li><li>When identifying null, empty, or hardcoded passwords, default rules only consider fields and variables that contain the word <b>password</b>. However, the Custom Rules Editor provides the Password Management wizard that makes it easy to create rules for detecting password management issues on custom-named fields and variables.</li><p><span font=\"header\">References</span></p><li>A6 Sensitive Data Exposure, Standards Mapping - OWASP Top 10 2013 - (OWASP 2013)</li><li>A7 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2010 - (OWASP 2010)</li><li>A8 Insecure Cryptographic Storage, Standards Mapping - OWASP Top 10 2007 - (OWASP 2007)</li><li>A8 Insecure Storage, Standards Mapping - OWASP Top 10 2004 - (OWASP 2004)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3 - (STIG 3)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.4 - (STIG 3.4)</li><li>APP3210.1 CAT II, APP3340 CAT I, APP3350 CAT I, Standards Mapping - Security Technical Implementation Guide Version 3.5 - (STIG 3.5)</li><li>CWE ID 259, CWE ID 798, Standards Mapping - Common Weakness Enumeration - (CWE)</li><li>IA, Standards Mapping - FIPS200 - (FISMA)</li><li>Porous Defenses - CWE ID 259, Standards Mapping - SANS Top 25 2009 - (SANS 2009)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2010 - (SANS 2010)</li><li>Porous Defenses - CWE ID 798, Standards Mapping - SANS Top 25 2011 - (SANS Top 25 2011)</li><li>Requirement 3.4, Requirement 6.3.1.3, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.2 - (PCI 1.2)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.2.1, Standards Mapping - Payment Card Industry Data Security Standard Version 3.0 - (PCI 3.0)</li><li>Requirement 3.4, Requirement 6.5.3, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 2.0 - (PCI 2.0)</li><li>Requirement 3.4, Requirement 6.5.8, Requirement 8.4, Standards Mapping - Payment Card Industry Data Security Standard Version 1.1 - (PCI 1.1)</li><li>SC-28 Protection of Information at Rest, Standards Mapping - NIST Special Publication 800-53 Revision 4 - (NIST SP 800-53 Rev.4)</li><li>SQLCipher., <a>http://sqlcipher.net/</a></li></body></html>");

        tabPanel.add(new JScrollPane(lblNewLabel), BorderLayout.CENTER);

        return tabPanel;
    }
}