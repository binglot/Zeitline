package org.Zeitline; /********************************************************************

 This file is part of org.Zeitline.Zeitline: a forensic timeline editor

 Written by Florian Buchholz and Courtney Falk.

 Copyright (c) 2004-2006 Florian Buchholz, Courtney Falk, Purdue
 University. All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal with the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimers.
 Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimers in the
 documentation and/or other materials provided with the distribution.
 Neither the names of Florian Buchholz, Courtney Falk, CERIAS, Purdue
 University, nor the names of its contributors may be used to endorse
 or promote products derived from this Software without specific prior
 written permission.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NON-INFRINGEMENT.  IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 SOFTWARE.

 **********************************************************************/

import org.Zeitline.Plugin.Input.InputFilter;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class ImportDlg
        extends JDialog
        implements ActionListener, ItemListener {

    public static final int OK_OPTION = 0,
            CANCEL_OPTION = 1;

    // single actual instance of the org.Zeitline.ImportDlg object
    private static ImportDlg instance;
    private static int return_value;
    private static JFileChooser fc;

    private static JComboBox cbx_filter_types;
    private static JTextField txt_file_name;
    private JButton btn_file_name, btn_ok, btn_cancel;
    private JPanel fieldPane;
    private GridBagConstraints c;
    private JComponent[] parameter_objects;
    private Vector label_set;

    /*
    * Set up and show the dialog.  The first Component argument
    * determines which frame the dialog depends on; it should be
    * a component in the dialog's controlling frame. The second
    * Component argument should be null if you want the dialog
    * to come up with its left corner in the center of the screen;
    * otherwise, it should be the component on top of which the
    * dialog should appear.
    */
    public static int showDialog(Frame frameComp,
                                 List<InputFilter> filters) {

        if (instance == null) {
            instance = new ImportDlg(frameComp, filters);
        } else {
            /*
           if(instance.getOwner() == frameComp)
               instance = new org.Zeitline.ImportDlg(frameComp, inputFilters);
           else */
            instance.setFilters(filters);
            txt_file_name.setText("");
        }
        instance.setLocationRelativeTo(frameComp);
        return_value = -1;
        instance.setVisible(true);

        return return_value;
    } // showDialog

    private ImportDlg(Frame frame,
                      List<InputFilter> filters_enum) {

        // call JDialog constructor
        super(frame, "Import Data", true);

        fc = new JFileChooser(System.getProperty("user.dir"));
        // create and initialize the buttons.
        btn_cancel = new JButton("Cancel");
        btn_cancel.addActionListener(this);
        btn_ok = new JButton("Import");
        btn_ok.setActionCommand("Ok");
        btn_ok.addActionListener(this);
        getRootPane().setDefaultButton(btn_ok);

        // create a container so that we can add a title around
        // the scroll pane.  Can't add a title directly to the
        // scroll pane because its background would be white.
        // lay out the label and scroll pane from top to bottom.
        fieldPane = new JPanel();
        fieldPane.setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
        c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        cbx_filter_types = new JComboBox();
        setFilters(filters_enum);
        cbx_filter_types.setEditable(false);
        cbx_filter_types.addItemListener(this);

        JLabel label = new JLabel("Filter type: ", JLabel.TRAILING);
        label.setLabelFor(cbx_filter_types);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        fieldPane.add(label, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        fieldPane.add(cbx_filter_types, c);

        txt_file_name = new JTextField(20);
        btn_file_name = new JButton("...");
        btn_file_name.addActionListener(this);
        JPanel pnl_file_name = new JPanel();
        pnl_file_name.add(txt_file_name);
        pnl_file_name.add(btn_file_name);

        label = new JLabel("File name: ", JLabel.TRAILING);
        label.setLabelFor(pnl_file_name);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        fieldPane.add(label, c);

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        fieldPane.add(pnl_file_name, c);

        // lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(btn_ok);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(btn_cancel);
        buttonPane.add(Box.createHorizontalGlue());


        // put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        //       contentPane.add(fieldPane, BorderLayout.CENTER);
        //       contentPane.add(buttonPane, BorderLayout.PAGE_END);
        contentPane.add(fieldPane);
        contentPane.add(buttonPane);
        label_set = new Vector();
        setResizable(false);
        pack();
    } // org.Zeitline.ImportDlg

    private void setFilters(List<InputFilter> filters) {
        Collections.sort(filters, new FilterComparator());
        cbx_filter_types.removeAllItems();

        for (InputFilter filter : filters) {
            cbx_filter_types.addItem(filter);
        }
        //cbx_filter_types = new JComboBox(filters_enum);
    }

    /*
     * Handles button clicks.  This is required for implementing
     * the ActionListener interface.
     *
     * @param e ActionEvent generated by a button click
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btn_ok) {
            // Ok button was clicked
            File temp_file = new File(txt_file_name.getText());
            if (!temp_file.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Couldn't find the desired file \""
                                + temp_file.getAbsolutePath()
                                + "\"",
                        "File Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            return_value = OK_OPTION;
            setVisible(false);
        } else if (source == btn_file_name) {
            // ... button to choose file was clicked
            fc.resetChoosableFileFilters();
            FileFilter filter = ((InputFilter) ImportDlg.getFilter()).getFileFilter();

            if (filter != null) {
                fc.addChoosableFileFilter(filter);
                fc.setFileFilter(filter);
            }

            int returnVal = fc.showOpenDialog(this);
            if (returnVal != JFileChooser.APPROVE_OPTION) return;

            File temp_file = new File(fc.getSelectedFile().getAbsolutePath());
            if (!temp_file.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Couldn't find the desired file \""
                                + temp_file.getAbsolutePath()
                                + "\"",
                        "File Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            txt_file_name.setText(temp_file.getAbsolutePath());
        } else if (source == btn_cancel) {
            // Cancel button was clicked
            return_value = CANCEL_OPTION;
            setVisible(false);
        }
    } // actionPerformed

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            InputFilter input = (InputFilter) e.getItem();
            String[] labels;
            if ((labels = input.getParameterLabels()) == null) {
                removeFields();
                pack();
                return;
            }
            if ((parameter_objects = input.getParameterFields()) == null) {
                removeFields();
                pack();
                return;
            }

            Vector items = new Vector();
//	    for (int i = 0; i < labels.length; i++) {
//		    items.add(org.Zeitline.GUI.FormGenerator.getFormItem(labels[i], parameter_objects[i]));
//	    }
            label_set.removeAllElements();
            for (int i = 0; i < labels.length; i++) {
                JLabel l = new JLabel(labels[i], JLabel.TRAILING);
                label_set.add(l);
//		    label.setLabelFor(pnl_file_name);
                c.gridx = 0;
                c.gridy = 2 + i;
                c.anchor = GridBagConstraints.PAGE_START;
                c.weightx = 0;
                fieldPane.add(l, c, 4 + 2 * i);

                c.gridx = 1;
                c.gridy = 2 + i;
                c.anchor = GridBagConstraints.CENTER;
                c.weightx = 1;
                fieldPane.add(parameter_objects[i], c, 5 + 2 * i);
            }
            pack();
        }
    }

    private void removeFields() {
        if (parameter_objects == null)
            return;
        for (int i = 0; i < parameter_objects.length; i++) {
            fieldPane.remove(parameter_objects[i]);
            fieldPane.remove((JLabel) label_set.elementAt(i));
        }
    }

    public static String getFileName() {
        return instance.txt_file_name.getText();
    } // getFileName

    public static InputFilter getFilter() {
        return (InputFilter) instance.cbx_filter_types.getSelectedItem();
    } // getFilter
} // class org.Zeitline.ImportDlg
