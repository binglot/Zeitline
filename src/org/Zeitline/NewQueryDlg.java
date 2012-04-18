package org.Zeitline;

import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Timestamp.Timestamp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;

public class NewQueryDlg extends JDialog
        implements ActionListener, ItemListener, ChangeListener, KeyListener {

    public static final int MODE_FILTER = 0;
    public static final int MODE_SEARCH = 1;

    private static NewQueryDlg dialog;
    private static Query query = null;
    private JTextField keywords;
    private JTextField regex;
    private JSpinner startTime;
    private JCheckBox controlStartTime;
    private JCheckBox wrapSearch;
    private JSpinner endTime;
    private JCheckBox controlEndTime;
    private TimelineView timelines;
    private JButton nextButton, prevButton;
    private int mode;

    private boolean findInit;

    /*
    public static org.Zeitline.Query showxDialog(Component frameComp,
				   Component locationComp,
				   Timestamp sTime,
				   Timestamp eTime) {

        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new org.Zeitline.NewQueryDlg(frame, locationComp, "Filter Events",
				 true, sTime, eTime, null);

        dialog.setVisible(true);
        return query;

    } // showDialog(Component,Component,Timestamp,Timestamp)
    */

    public static Query showDialog(Component frameComp,
                                   Component locationComp,
                                   ITimestamp sTime,
                                   ITimestamp eTime,
                                   Query q) {

        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new NewQueryDlg(frame, locationComp, "Filter Events",
                true, MODE_FILTER, sTime, eTime, q, null);

        dialog.setVisible(true);
        return query;

    }

    public NewQueryDlg(Frame frame,
                       Component locationComp,
                       String title,
                       boolean modal,
                       int m,
                       ITimestamp sTime,
                       ITimestamp eTime,
                       Query preset,
                       TimelineView tl) {

        super(frame, title, modal);

        mode = m;

        if (mode == MODE_SEARCH)
            dialog = this;

        timelines = tl;

        controlStartTime = new JCheckBox("Start time:");
        controlStartTime.addItemListener(this);
        controlStartTime.addChangeListener(this);
        controlEndTime = new JCheckBox("End time:");
        controlEndTime.addItemListener(this);
        controlEndTime.addChangeListener(this);

        keywords = new JTextField(30);
        keywords.addKeyListener(this);

        regex = new JTextField(30);
        regex.addKeyListener(this);

        startTime = new JSpinner(new SpinnerDateModel(new Date(sTime.getTime()),
                null,
                null,
                Calendar.DAY_OF_MONTH));

        startTime.setEditor(new JSpinner.DateEditor(startTime,
                "yyyy-MM-dd HH:mm:ss.S"));
        startTime.addChangeListener(this);
        startTime.setEnabled(false);

        endTime = new JSpinner(new SpinnerDateModel(new Date(eTime.getTime()),
                null,
                null,
                Calendar.DAY_OF_MONTH));

        endTime.setEditor(new JSpinner.DateEditor(endTime,
                "yyyy-MM-dd HH:mm:ss.S"));
        endTime.addChangeListener(this);
        endTime.setEnabled(false);

        if (preset != null) {

            Timestamp t = preset.getIntervalStart();
            if (t != null) {
                startTime.setEnabled(true);
                controlStartTime.setSelected(true);
                startTime.setValue(new Date(t.getTime()));
            }

            t = preset.getIntervalEnd();
            if (t != null) {
                endTime.setEnabled(true);
                controlEndTime.setSelected(true);
                endTime.setValue(new Date(t.getTime()));
            }

            String s = preset.getStringText();
            if (s != null)
                keywords.setText(s);

            String r = preset.getRegexText();
            if (r != null)
                regex.setText(r);
        }

        findInit = false;

        JPanel fieldPane = new JPanel();
        fieldPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel label;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        fieldPane.add(controlStartTime, c);

        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        fieldPane.add(startTime, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        fieldPane.add(controlEndTime, c);

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        fieldPane.add(endTime, c);


        label = new JLabel("Keyword:");
        label.setLabelFor(keywords);
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        fieldPane.add(label, c);

        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridwidth = 2;
        fieldPane.add(keywords, c);

        label = new JLabel("Regular expression:");
        label.setLabelFor(regex);
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LINE_START;
        fieldPane.add(label, c);

        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridwidth = 2;
        fieldPane.add(regex, c);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        if (mode == MODE_FILTER) {

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);

            JButton okButton = new JButton("Filter");
            okButton.setActionCommand("Filter");
            okButton.addActionListener(this);

            getRootPane().setDefaultButton(okButton);
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(okButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createHorizontalGlue());

        } else if (mode == MODE_SEARCH) {

            wrapSearch = new JCheckBox("Wrap search", false);
            wrapSearch.addChangeListener(this);
            c.gridx = 0;
            c.gridy = 6;
            c.anchor = GridBagConstraints.PAGE_START;
            fieldPane.add(wrapSearch, c);

            nextButton = new JButton("Next");
            nextButton.setActionCommand("Next");
            nextButton.addActionListener(this);

            prevButton = new JButton("Prev");
            prevButton.setActionCommand("Prev");
            prevButton.addActionListener(this);
            prevButton.setEnabled(false);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);

            getRootPane().setDefaultButton(nextButton);
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(prevButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(nextButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createHorizontalGlue());


        }

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(fieldPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);


        pack();
        setLocationRelativeTo(locationComp);

        keywords.requestFocus();

    } // org.Zeitline.NewQueryDlg

    // ActionListener interface: Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {

        if ("Filter".equals(e.getActionCommand())) {
            query = buildQuery();
            NewQueryDlg.dialog.setVisible(false);
        } else if ("Next".equals(e.getActionCommand())) {
            if (!findInit) {
                //		System.out.println("Init find");
                timelines.initFindEvent(timelines.getCurrentTree());
                findInit = true;
            }
            if (!timelines.findNextEvent(buildQuery(), wrapSearch.isSelected()))
                nextButton.setEnabled(false);
            else
                prevButton.setEnabled(true);
        } else if ("Prev".equals(e.getActionCommand())) {
            if (!timelines.findPreviousEvent(buildQuery(), wrapSearch.isSelected()))
                prevButton.setEnabled(false);
            else
                nextButton.setEnabled(true);
        } else {
            query = null;
            NewQueryDlg.dialog.setVisible(false);
        }

    } // actionPeformed

    // ItemListener interface for check box buttons
    public void itemStateChanged(ItemEvent e) {

        Object source = e.getItemSelectable();

        if (source == controlStartTime) {
            if (e.getStateChange() == ItemEvent.DESELECTED)
                startTime.setEnabled(false);
            else
                startTime.setEnabled(true);
        } else if (source == controlEndTime) {
            if (e.getStateChange() == ItemEvent.DESELECTED)
                endTime.setEnabled(false);
            else
                endTime.setEnabled(true);
        }
    } // itemStateChanged	

    public void stateChanged(ChangeEvent e) {

        if ((mode == MODE_SEARCH) && findInit) {

            nextButton.setEnabled(true);
            prevButton.setEnabled(true);

        }

    }

    public void keyTyped(KeyEvent e) {

        if ((mode == MODE_SEARCH) && findInit) {

            nextButton.setEnabled(true);
            prevButton.setEnabled(true);

        }

    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }


    private Query buildQuery() {

        Timestamp start, end;

        if (controlStartTime.isSelected())
            start = new Timestamp(((SpinnerDateModel) startTime.getModel())
                    .getDate().getTime());
        else
            start = null;

        if (controlEndTime.isSelected())
            end = new Timestamp(((SpinnerDateModel) endTime.getModel())
                    .getDate().getTime());
        else
            end = null;

        String onlyWhitespace = "\\A\\s*\\Z";

        if ((start == null) && (end == null) &&
                (keywords.getText().matches(onlyWhitespace)) &&
                (regex.getText().matches(onlyWhitespace)))
            return null;
        else
            return new Query(start, end, keywords.getText(), regex.getText());

    }

} // org.Zeitline.NewQueryDlg
