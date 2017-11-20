package com.fortify.fod.remediation.standalone;

import com.fortify.fod.remediation.custom.GroupTreeItem;
import com.fortify.fod.remediation.custom.IssueTreeItem;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by jazl on 10/27/17.
 */
public class ListStuff extends JFrame {

    class Person {
        private String firstName;
        private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        public String toString() {
            return this.lastName+", "+this.firstName;
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

        return panel;
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
