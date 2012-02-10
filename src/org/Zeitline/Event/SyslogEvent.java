package org.Zeitline.Event;

//import org.Zeitline.Event.GUI.FormGenerator;
import org.Zeitline.Event.GUI.IFormGenerator;
import org.Zeitline.Timestamp.Timestamp;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SyslogEvent
        extends AtomicEvent
        implements Serializable {

    protected static JPanel panel = null;
    protected static JLabel lbl_host;
    protected static JLabel lbl_process;
    protected static JLabel lbl_pid;
    protected static JLabel lbl_message;

    private String host;
    private String process;
    private Integer pid;
    private String message;
    private IFormGenerator formGenerator;

//    public SyslogEvent(Timestamp start_time, String host, String process, Integer pid, String message) {
//        this(start_time, host, process, pid, message, new FormGenerator());
//    }

    public SyslogEvent(Timestamp start_time, String host, String process, Integer pid, String message, IFormGenerator formGenerator) {
        this.formGenerator = formGenerator;

        if (panel == null) {
            initPanel();
        }

        this.startTime = start_time;
        this.host = host;
        this.process = process;
        this.pid = pid;
        this.message = message;
        this.uniqueId = idCounter;
        idCounter++;

        adjustedTime = start_time;
        reportedTime = start_time;

    }

    public String getName() {
        return process + ": " + message;
    } // getName

    public String getDescription() {

        return "Host: " + host +
                "\nProcess: " + process +
                "\nPID: " + ((pid == null) ? "-" : pid.toString()) +
                "\nMessage: " + message;
    } // getDescription

    public String toString() {
        return getName();
    } // toString


//    public ImageIcon getIcon() {
//	return icon;
//    }

    public JPanel getPanel() {
        if (panel == null)
            initPanel();
        return panel;
    }

    public void setPanelValues() {
        if (panel == null)
            initPanel();
        lbl_host.setText(host);
        lbl_process.setText(process);
        if (pid == null)
            lbl_pid.setText("-");
        else
            lbl_pid.setText(pid.toString());
        lbl_message.setText(message);
    }


    private void initPanel() {

        Vector items = new Vector();
        lbl_host = new JLabel("", JLabel.LEADING);
        items.add(formGenerator.getFormItem("Host:", lbl_host));
        lbl_process = new JLabel("", JLabel.LEADING);
        items.add(formGenerator.getFormItem("Process:", lbl_process));
        lbl_pid = new JLabel("", JLabel.LEADING);
        items.add(formGenerator.getFormItem("PID:", lbl_pid));
        lbl_message = new JLabel("", JLabel.LEADING);
        items.add(formGenerator.getFormItem("Message:", lbl_message));

        panel = formGenerator.createForm(items);

    }

} // class org.Zeitline.Event.SyslogEvent
