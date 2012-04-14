package org.Zeitline.Event.Mask;

import org.Zeitline.Event.L2tEvent;
import org.Zeitline.Source;

import javax.swing.*;
import java.awt.*;

public class L2tEventMask extends JPanel {

    private final static int FIELD_WIDTH = 15;

    private JPanel display;
    private JPanel descriptionPanel;

    private L2tEvent current;

    private JLabel lblTimeValue = new JLabel();
    private JLabel lblTimezoneValue = new JLabel();
    private JTextField txtNameValue = new JTextField(FIELD_WIDTH);
    private JTextArea txtDescriptionValue = new JTextArea();
    private JLabel lblSourceNameValue = new JLabel();
    private JLabel lblSourceCreatedValue = new JLabel();

    public L2tEventMask() {
        display = new JPanel();
        descriptionPanel = new JPanel(new GridBagLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        FillTheTop(topPanel, c);
        FillTheMiddle(descriptionPanel, c);
        FillTheBottom(bottomPanel, c);

        topPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        bottomPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        this.setLayout(new BorderLayout()); // IS IT NECESSARY? 14/04/2012
        this.add(topPanel, BorderLayout.PAGE_START);
        //
        // what's happened to the middle panel?!
        //
        this.add(display, BorderLayout.WEST);
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }

    // Needs setting a better name!
    private void FillTheTop(JPanel panel, GridBagConstraints c) {
        // Time
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        panel.add(new JLabel("Time: ", JLabel.TRAILING), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        panel.add(lblTimeValue, c);

        // Time Zone
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        panel.add(new JLabel("Time zone: ", JLabel.TRAILING), c);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        panel.add(lblTimezoneValue, c);

        // Adjusted Time
//        c.gridx = 0;
//        c.gridy = 1;
//        panel.add(new JLabel("Adjusted time: ", JLabel.TRAILING), c);
//
//        c.gridx = 1;
//        c.gridy = 1;
//        panel.add(lblAdjustedValue, c);

        // Reported Time
//        c.gridx = 0;
//        c.gridy = 2;
//        panel.add(new JLabel("Reported time: ", JLabel.TRAILING), c);
//
//        c.gridx = 1;
//        c.gridy = 2;
//        panel.add(lblReportedValue, c);


        // Name

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.PAGE_START; //?
        panel.add(new JLabel("Name: ", JLabel.TRAILING), c);

//	lblNameCapture.setLabelFor(txtNameValue);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1; // ?
        c.anchor = GridBagConstraints.CENTER;
        panel.add(txtNameValue, c);
    }

    private void FillTheMiddle(JPanel panel, GridBagConstraints c) {
        JLabel lblDescriptionCapture = new JLabel("Description: ", JLabel.TRAILING);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        panel.add(lblDescriptionCapture, c);

        txtDescriptionValue = new JTextArea(6, FIELD_WIDTH);
        lblDescriptionCapture.setLabelFor(txtDescriptionValue);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JScrollPane(txtDescriptionValue, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), c);
    }

    private void FillTheBottom(JPanel panel, GridBagConstraints c) {
        // Source
        JLabel lblSourceNameCapture = new JLabel("Source: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        panel.add(lblSourceNameCapture, c);

        lblSourceNameValue = new JLabel();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(lblSourceNameValue, c);

        // Created
        JLabel lblSourceCreatedCapture = new JLabel("Created: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        panel.add(lblSourceCreatedCapture, c);

        lblSourceCreatedValue = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(lblSourceCreatedValue, c);
    }

    public void set(L2tEvent e) {
        // Needs further checking
        Source s = e.getSource();
        // =

        if ((current == null) || !(current.getClass().equals(e.getClass())))
            display.removeAll();


        lblTimeValue.setText(e.getTime().toString());
        lblTimezoneValue.setText(e.getTimezone());
        txtNameValue.setText(e.getName());
        txtNameValue.setCaretPosition(0);
        txtDescriptionValue.setText(e.getDescription());

        if (s != null) {
            lblSourceNameValue.setText(s.getName());
            lblSourceCreatedValue.setText(s.getTimestamp().toString());
        }

        if ((current == null) || !(current.getClass().equals(e.getClass()))){
            JPanel p = e.getPanel();

            if (p != null) {
                display.add(p);
            } else
                display.add(descriptionPanel);
        }

        e.setPanelValues();
        current = e;
    }

}
