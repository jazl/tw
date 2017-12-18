package com.fortify.fod.remediation.standalone;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by jazl on 10/27/17.
 */

/**
 * something on master to rebase to my personal branch!!!!
 * something on master to rebase to my personal branch!!!!
 * something on master to rebase to my personal branch!!!!
 */
public class ListStuff extends JFrame {

    class Person {
        private String _firstName;
        private String _lastName;

        public Person() {

        }
        public Person(String firstName, String lastName) {
            this._firstName = firstName;
            this._lastName = lastName;
        }

        public String getFirstName() {
            return this._firstName;
        }

        public void setFirstName(String firstName) {
            this._firstName = firstName;
        }

        public String getLastName() {
            return this._lastName;
        }

        public void setLastName(String lastName) {
            this._lastName = lastName;
        }
        public String toString() {
            return this._lastName +", "+this._firstName;
        }
    }

    class PersonNoTS extends Person {
        public PersonNoTS(String firstName, String lastName) {
            this.setFirstName(firstName);
            this.setLastName(lastName);
        }
        public String toString() {
            return null;
        }
    }

    private JList<String> stringList = new JList<>();
    private JList<Person> personList = new JList<>();

    private DefaultMutableTreeNode root;
    private TreePath rootPath;

    public ListStuff() {
        setSize(400,400);

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new JBScrollPane(getList()), BorderLayout.CENTER);
        panel.add(getControlsPanel(), BorderLayout.SOUTH);

        add(panel);
    }

    private Component getList() {
        JPanel panel = new JPanel();

        stringList = new JList<>();

        List<String> list1 = new ArrayList<>();
        list1.add("Sharon H");
        list1.add("Sarah B");
        list1.add("Becca C");

        String[] strings = list1.toArray(new String[0]);
        DefaultListModel<String> stringListModel = new DefaultListModel<>();
        for(String s:list1){
            stringListModel.addElement(s);
        }
        stringList.setModel(stringListModel);
        panel.add(new JBScrollPane(stringList));

        personList = new JList<>();

        DefaultListModel<Person> personModel = new DefaultListModel<>();
        personModel.addElement(new Person("Sharon", "H"));
        personModel.addElement(new Person("Sarah", "B"));
        personModel.addElement(new Person("Heidi", "C"));
        personList.setModel(personModel);
        panel.add(new JBScrollPane(personList));

        panel.add(getDropDownList());

        return panel;
    }

    private Component getDropDownList() {
        JPanel ddPanel = new JPanel();

        JComboBox<String> cbString = new JComboBox<>();
        DefaultComboBoxModel<String> stringModel = new DefaultComboBoxModel<>();
        cbString.setModel(stringModel);
        stringModel.addElement("Sharon");
        stringModel.addElement("Susan Huffstutter");
        stringModel.addElement("Janice Mauer");
        stringModel.setSelectedItem("Susan Huffstutter");
        ddPanel.add(cbString);

        JComboBox<PersonNoTS> cbPerson = new JComboBox<>();
        DefaultComboBoxModel<PersonNoTS> personModel = new DefaultComboBoxModel<>();
        cbPerson.setModel(personModel);
        cbPerson.setRenderer(new PersonNoTSRenderer());

        PersonNoTS sharon = new PersonNoTS("Sharon","H");
        PersonNoTS janice = new PersonNoTS("Janice","Mauer");
        PersonNoTS susan = new PersonNoTS("Susan","Huffstutter");
        personModel.addElement(sharon);
        System.out.println("sharon.toString() = "+sharon);
        System.out.println("sharon.getLastName() = "+sharon.getLastName());
        personModel.addElement(janice);
        personModel.addElement(susan);
        personModel.setSelectedItem(janice);

        ddPanel.add(cbPerson);

        return ddPanel;
    }

    class PersonNoTSRenderer implements ListCellRenderer<PersonNoTS> {

        DefaultListCellRenderer r = new DefaultListCellRenderer();
        @Override
        public Component getListCellRendererComponent(JList<? extends PersonNoTS> list, PersonNoTS value, int index, boolean isSelected, boolean cellHasFocus) {
            String text = value.getFirstName() + " " + value.getLastName() + "!!!";
            r.setText(text);

            if (isSelected) {
                r.setBackground(list.getSelectionBackground());
                r.setForeground(list.getSelectionForeground());
            }
            else {
                r.setBackground(list.getBackground());
                r.setForeground(list.getForeground());
            }

            return r;
        }
    }

    private Component getControlsPanel() {
        JPanel panel = new JPanel();

        return panel;
    }

    public static void main(String[] args) {
        ListStuff ts = new ListStuff();
        ts.setVisible(true);
    }
}
