import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

/**
 * Created by jazl on 9/22/2017.
 */
public class FileFolderValidation {
    private static JDialog dialog;
    public static void main(String[] args) {
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setTitle("File and Folder Validation");
        dialog.setLocationRelativeTo(null);

        dialog.add(getMainPanel());
        dialog.setSize(400,100);
        dialog.setVisible(true);
    }

    private static JPanel getMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel userMsg = new JLabel("Enter a file name");
        mainPanel.add(userMsg);

        JTextField textField = new JTextField();
        mainPanel.add(textField);

        JButton button = new JButton("Test Button");
        mainPanel.add(button);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                isValidPath(textField.getText());
            }
        });

        return mainPanel;
    }

    private static boolean isValidPath(String path) {
        boolean isValid = true;
        File file = new File(path);
        if(file.isDirectory()) {
            System.out.println(path + " is a directory");
        } else if(file.isFile()) {
            System.out.println(path + " is a file");
        }
        else {
            System.out.println("Can't tell WTF "+path+" is");
            isValid = false;
        }
        return isValid;
    }
}