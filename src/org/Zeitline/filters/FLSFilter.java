package org.Zeitline.filters;

import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.Event.MACTimeEvent;
import org.Zeitline.FileInputFilter;
import org.Zeitline.InputFilter.InputFilter;
import org.Zeitline.Source;

import java.awt.Component;
import java.io.RandomAccessFile;
import java.io.IOException;

import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Timestamp.Timestamp;

import java.util.LinkedList;
import javax.swing.filechooser.FileFilter;

public class FLSFilter extends InputFilter {
    protected LinkedList event_queue;
    protected RandomAccessFile file_input;
    private static FileFilter filter;

    protected String filename;
    protected Timestamp mtime;
    protected Timestamp atime;
    protected Timestamp ctime;
    protected int user_id;
    protected int group_id;
    protected int mode;
    protected int permissions;
    protected long size;
    protected int type;

    public FLSFilter(IFormGenerator formGenerator) {
        super(formGenerator);
        event_queue = new LinkedList();
        if (filter == null) filter = new FileInputFilter(".fls", "*.fls");
    }

    public Source init(String filename, Component parent) {

        try {
            //	    file_input = new BufferedReader(new FileReader(filename));
            file_input = new RandomAccessFile(filename, "r");
        } catch (IOException ioe) {
            return null;
        }

        return new Source("FLS filter", filename, Source.GRANULARITY_SEC);
    } // init

    public AtomicEvent getNextEvent() {

        String line;
        String[] fields;

        if (event_queue.isEmpty()) {
            while (true) {

                try {
                    line = file_input.readLine();
                } catch (IOException ioe) {
                    return null;
                }

                if (line == null) return null;

                fields = line.split("\\|");

                if (fields.length < 16)
                    System.err.println("Line not in proper format: " + line);
                else
                    break;

            }


            // get timestamps, we have second granularity but need to
            // convert to ms

            ITimestamp mtime = new Timestamp(Long.decode(fields[12]).intValue() * (long) 1000);
            ITimestamp atime = new Timestamp(Long.decode(fields[11]).intValue() * (long) 1000);
            ITimestamp ctime = new Timestamp(Long.decode(fields[13]).intValue() * (long) 1000);

            String name = fields[1];
            int user_id = Integer.decode(fields[7]).intValue();
            int group_id = Integer.decode(fields[8]).intValue();
            int mode = Integer.decode(fields[4]).intValue();
            long size = Long.decode(fields[10]).intValue();


            if ((mtime.compareTo(atime) == 0) && (mtime.compareTo(ctime) == 0))
                return new MACTimeEvent(name, mtime, atime, ctime, user_id,
                        group_id, mode, size,
                        MACTimeEvent.TYPE_MAC, formGenerator);


            if (mtime.compareTo(atime) == 0) {
                event_queue.add(new MACTimeEvent(name, mtime, atime, ctime,
                        user_id, group_id, mode, size,
                        MACTimeEvent.TYPE_MA, formGenerator));

                return new MACTimeEvent(name, mtime, atime, ctime, user_id,
                        group_id, mode, size,
                        MACTimeEvent.TYPE_C, formGenerator);
            }

            if (mtime.compareTo(ctime) == 0) {
                event_queue.add(new MACTimeEvent(name, mtime, atime, ctime,
                        user_id, group_id, mode, size,
                        MACTimeEvent.TYPE_MC, formGenerator));

                return new MACTimeEvent(name, mtime, atime, ctime, user_id,
                        group_id, mode, size,
                        MACTimeEvent.TYPE_A, formGenerator);
            }

            if (atime.compareTo(ctime) == 0) {
                event_queue.add(new MACTimeEvent(name, mtime, atime, ctime,
                        user_id, group_id, mode, size,
                        MACTimeEvent.TYPE_AC, formGenerator));

                return new MACTimeEvent(name, mtime, atime, ctime, user_id,
                        group_id, mode, size,
                        MACTimeEvent.TYPE_M, formGenerator);
            }

            event_queue.add(new MACTimeEvent(name, mtime, atime, ctime,
                    user_id, group_id, mode, size,
                    MACTimeEvent.TYPE_M, formGenerator));

            event_queue.add(new MACTimeEvent(name, mtime, atime, ctime,
                    user_id, group_id, mode, size,
                    MACTimeEvent.TYPE_A, formGenerator));

            return new MACTimeEvent(name, mtime, atime, ctime, user_id,
                    group_id, mode, size,
                    MACTimeEvent.TYPE_C, super.formGenerator);


            /* old code for general events

           if ((mtime == atime) && (mtime == ctime))
           return new org.Zeitline.Event.GeneralEvent("MAC " + name, description,
                          new Timestamp(mtime));

           if (mtime == atime) {
           event_queue.add(new org.Zeitline.Event.GeneralEvent("MA. " + name, description,
                           new Timestamp(mtime)));
           return new org.Zeitline.Event.GeneralEvent("..C " + name, description,
                          new Timestamp(ctime));
           }

           if (mtime == ctime) {
           event_queue.add(new org.Zeitline.Event.GeneralEvent("M.C " + name, description,
                           new Timestamp(mtime)));
           return new org.Zeitline.Event.GeneralEvent(".A. " + name, description,
                          new Timestamp(atime));
           }

           if (atime == ctime) {
           event_queue.add(new org.Zeitline.Event.GeneralEvent(".AC " + name, description,
                           new Timestamp(atime)));
           return new org.Zeitline.Event.GeneralEvent("M.. " + name, description,
                          new Timestamp(mtime));
           }

           event_queue.add(new org.Zeitline.Event.GeneralEvent("M.. " + name, description,
                           new Timestamp(mtime)));
           event_queue.add(new org.Zeitline.Event.GeneralEvent(".A. " + name, description,
                           new Timestamp(atime)));
           return new org.Zeitline.Event.GeneralEvent("..C " + name, description,
                      new Timestamp(ctime));

           */

        } else return (MACTimeEvent) event_queue.removeFirst();

    } // getNextEvent

    public FileFilter getFileFilter() {
        return filter;
    } // getFileFilter

    public String getName() {
        return "FLS Filter";
    } // getName

    public String getDescription() {
        return "Reads in MAC times as output by Brian Carrier's FLS tool.";
    } // getDescription

    public long getExactCount() {
        return 0;
    } // getExactCount

    public long getTotalCount() {
        try {
            return file_input.length();
        } catch (IOException ie) {
            return 0;
        }
    } // getTotalCount

    public long getProcessedCount() {
        try {
            return file_input.getFilePointer();
        } catch (IOException ie) {
            return 0;
        }
    } // getProcessedCount

} // org.Zeitline.filters.FLSFilter
