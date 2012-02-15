//package org.Zeitline.filters;
//
//import org.Zeitline.Event.AtomicEvent;
//import org.Zeitline.Event.SyslogEvent;
//import org.Zeitline.FileInputFilter;
//import org.Zeitline.GUI.IFormGenerator;
//import org.Zeitline.Plugin.Input.InputFilter;
//import org.Zeitline.Source;
//import org.Zeitline.Timestamp.Timestamp;
//
//import javax.swing.*;
//import javax.swing.filechooser.FileFilter;
//import java.awt.*;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.text.NumberFormat;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class SyslogFilter extends InputFilter {
//    protected RandomAccessFile file_input;
//    private static FileFilter filter;
//    protected int current_year;
//    protected int last_month;
//    protected GregorianCalendar now;
//    private JComponent[] parameter_fields;
//    private String[] parameter_names;
//    private static final int CURRENT_YEAR;
//
//    static {
//        Calendar cal = Calendar.getInstance();
//        CURRENT_YEAR = cal.get(Calendar.YEAR);
//    }
//
//
//    public SyslogFilter(IFormGenerator formGenerator) {
//        super(formGenerator);
//        if (filter == null) filter = new FileInputFilter(null, null);
//        parameter_fields = new JComponent[1];
//        NumberFormat nf = NumberFormat.getIntegerInstance();
//        nf.setGroupingUsed(false);
//        JFormattedTextField ftf = new JFormattedTextField(nf);
//        ftf.setValue(new Integer(CURRENT_YEAR));
//        parameter_fields[0] = ftf;
//        parameter_names = new String[1];
//        parameter_names[0] = new String("Start year: ");
//
//    } // org.Zeitline.inputFilters.SyslogFilter
//
//    public Source init(String filename, Component parent) {
//        try {
//            file_input = new RandomAccessFile(filename, "r");
//        } catch (IOException ioe) {
//            return null;
//        }
//
//        /*
//      String s = (String)JOptionPane.showInputDialog(
//              parent,
//              "Enter start year:",
//                      "Syslog import filter",
//                      JOptionPane.PLAIN_MESSAGE,
//                      null,
//                      null,
//                      "");
//
//      if ((s != null) && (s.length() > 0)) {
//          try {
//          current_year = (new Integer(s)).intValue();
//          }
//          catch (NumberFormatException nfe) {
//          return null;
//          }
//      }
//      else
//          return null;
//      */
//
//        current_year = ((Integer) ((JFormattedTextField) parameter_fields[0]).getValue()).intValue();
//
//        last_month = -1;
//        now = new GregorianCalendar();
//        now.clear();
//
//        return new Source("Syslog filter", filename, Source.GRANULARITY_SEC);
//    } // init
//
//    public AtomicEvent getNextEvent() {
//
//        String line;
//        String[] fields;
//
//        try {
//            line = file_input.readLine();
//        } catch (IOException ioe) {
//            return null;
//        }
//
//        if (line == null) return null;
//
//        // Pattern to match: <timestamp> <hostname/IP> [<generating instance>] <message>
//        // timestamp: <three-character month> <day> <hh:mm:ss>
//        // hostname: <non-whitespace sequence>
//        // generating instance: (<non-colon sequence>|<non-colon sequence>[digits]):
//        // message: remainder of line
//
//        Pattern p = Pattern.compile("(...)\\s+(\\d+)\\s+(\\d\\d):(\\d\\d):(\\d\\d)\\s+(\\S+)\\s+([^:]*)\\[(\\d+)\\]:\\s(.+)");
//        Matcher m = p.matcher(line);
//
//        if (m.matches()) {
//            int month = getMonth(m.group(1));
//            if (month < last_month)
//                current_year++;
//            last_month = month;
//            now.set(current_year, month,
//                    (new Integer(m.group(2))).intValue(),
//                    (new Integer(m.group(3))).intValue(),
//                    (new Integer(m.group(4))).intValue(),
//                    (new Integer(m.group(5))).intValue());
//
//            return new SyslogEvent(new Timestamp(now.getTime().getTime()), m.group(6), m.group(7), new Integer(m.group(8)), m.group(9), formGenerator);
//        }
//
//        p = Pattern.compile("(...)\\s+(\\d+)\\s+(\\d\\d):(\\d\\d):(\\d\\d)\\s+(\\S+)\\s+([^:]*):\\s(.+)");
//        m = p.matcher(line);
//
//        if (m.matches()) {
//            int month = getMonth(m.group(1));
//            if (month < last_month)
//                current_year++;
//            last_month = month;
//            now.set(current_year, month,
//                    (new Integer(m.group(2))).intValue(),
//                    (new Integer(m.group(3))).intValue(),
//                    (new Integer(m.group(4))).intValue(),
//                    (new Integer(m.group(5))).intValue());
//
////		System.out.println("Host: " + m.group(6) + " Generator: " + m.group(7));
//
//            return new SyslogEvent(new Timestamp(now.getTime().getTime()), m.group(6), m.group(7), null, m.group(8), formGenerator);
////		return new org.Zeitline.Event.GeneralEvent(m.group(8), "", new Timestamp(now.getTime().getTime()));
//        }
//
//        System.err.println("No match for line: " + line);
//        return null;
//    } // getNextEvent
//
//    public FileFilter getFileFilter() {
//        return filter;
//    } // getFileFilter
//
//    public String getName() {
//        return "Syslog Filter";
//    } // getName
//
//    public String getDescription() {
//        return "Linux syslog event filter";
//    } // getDescription
//
//    public long getExactCount() {
//        return 0;
//    } // getExactCount
//
//    public long getTotalCount() {
//        try {
//            return file_input.length();
//        } catch (IOException ie) {
//            return 0;
//        }
//    } // getTotalCount
//
//    public long getProcessedCount() {
//        try {
//            return file_input.getFilePointer();
//        } catch (IOException ie) {
//            return 0;
//        }
//    } // getProcessedCount
//
//    public String[] getParameterLabels() {
//        return parameter_names;
//    }
//
//    public JComponent[] getParameterFields() {
//        return parameter_fields;
//    }
//
//    protected int getMonth(String name) {
//
//        if (name.equals("Jan"))
//            return 0;
//        if (name.equals("Feb"))
//            return 1;
//        if (name.equals("Mar"))
//            return 2;
//        if (name.equals("Apr"))
//            return 3;
//        if (name.equals("May"))
//            return 4;
//        if (name.equals("Jun"))
//            return 5;
//        if (name.equals("Jul"))
//            return 6;
//        if (name.equals("Aug"))
//            return 7;
//        if (name.equals("Sep"))
//            return 8;
//        if (name.equals("Oct"))
//            return 9;
//        if (name.equals("Nov"))
//            return 10;
//        if (name.equals("Dec"))
//            return 11;
//
//        return 0;
//
//    }
//
//} // org.Zeitline.inputFilters.SyslogFilter
