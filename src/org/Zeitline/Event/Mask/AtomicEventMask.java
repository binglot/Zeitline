package org.Zeitline.Event.Mask;

import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.Source;

import javax.swing.*;
import java.awt.*;

public class AtomicEventMask extends JPanel {
    private final static int FIELD_WIDTH = 15;

    private JLabel lblStartValue;
    private JLabel lblAdjustedValue;
    private JLabel lblReportedValue;
    private JLabel lblNameCapture;
    private JTextField txtNameValue;
    private JLabel lblDescriptionCapture;
    private JTextArea txtDescriptionValue;
    private JLabel lblSourceNameCapture, lblSourceNameValue;
    private JLabel lblSourcecCeatedCapture, lblSourceCreatedValue;
    private JPanel display;
    private JPanel descriptionPanel;

    protected AtomicEvent current;

    public AtomicEventMask() {
        display = new JPanel();
        descriptionPanel = new JPanel(new GridBagLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        current = null;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(new JLabel("Time: ", JLabel.TRAILING), c);

        lblStartValue = new JLabel();
//	lbl_start_cap.setLabelFor(lblStartValue);
        c.gridx = 1;
        c.gridy = 0;
        topPanel.add(lblStartValue, c);

        c.gridx = 0;
        c.gridy = 1;
        topPanel.add(new JLabel("Adjusted time: ", JLabel.TRAILING), c);

        lblAdjustedValue = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
        topPanel.add(lblAdjustedValue, c);

        c.gridx = 0;
        c.gridy = 2;
        topPanel.add(new JLabel("Reported time: ", JLabel.TRAILING), c);

        lblReportedValue = new JLabel();
        c.gridx = 1;
        c.gridy = 2;
        topPanel.add(lblReportedValue, c);

        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.PAGE_START;
        topPanel.add(new JLabel("Name: ", JLabel.TRAILING), c);

        txtNameValue = new JTextField(FIELD_WIDTH);
//	lblNameCapture.setLabelFor(txtNameValue);
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        topPanel.add(txtNameValue, c);

        lblDescriptionCapture = new JLabel("Description: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        descriptionPanel.add(lblDescriptionCapture, c);

        txtDescriptionValue = new JTextArea(6, FIELD_WIDTH);
        lblDescriptionCapture.setLabelFor(txtDescriptionValue);
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        descriptionPanel.add(new JScrollPane(txtDescriptionValue,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), c);

        lblSourceNameCapture = new JLabel("Source: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        bottomPanel.add(lblSourceNameCapture, c);

        lblSourceNameValue = new JLabel();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        bottomPanel.add(lblSourceNameValue, c);

        lblSourcecCeatedCapture = new JLabel("Created: ", JLabel.TRAILING);
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.weightx = 0;
        bottomPanel.add(lblSourcecCeatedCapture, c);

        lblSourceCreatedValue = new JLabel();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        bottomPanel.add(lblSourceCreatedValue, c);

        topPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        bottomPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.PAGE_START);
        this.add(display, BorderLayout.WEST);
        this.add(bottomPanel, BorderLayout.PAGE_END);
    }

    public void set(AtomicEvent e) {
        Source s = e.getSource();
        if ((current == null) || !(current.getClass().equals(e.getClass())))
            this.display.removeAll();
        this.lblStartValue.setText(e.getStartTime().toString());
        this.lblAdjustedValue.setText(e.getAdjustedTime().toString());
        this.lblReportedValue.setText(e.getReportedTime().toString());
        this.txtNameValue.setText(e.getName());
        this.txtNameValue.setCaretPosition(0);
        this.txtDescriptionValue.setText(e.getDescription());

        if (s != null) {
            this.lblSourceNameValue.setText(s.toString());
            this.lblSourceCreatedValue.setText(s.getTimestamp().toString());
        }
        if ((current == null) || !(current.getClass().equals(e.getClass()))) {
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
