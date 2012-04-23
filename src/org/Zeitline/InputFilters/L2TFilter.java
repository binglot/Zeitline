package org.Zeitline.InputFilters;

import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.Event.L2TEvent;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.Plugin.Input.InputFilter;
import org.Zeitline.Source;
import org.Zeitline.Timestamp.ITimestamp;
import org.Zeitline.Timestamp.Timestamp;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class L2TFilter extends InputFilter {

    private static final String NAME = "Log2Timeline Filter";
    private static final String DESCRIPTION = "Reads in standard CSV output file by Kristinn Gudjonsson's log2timeline tool.";
    private static final String INPUT_FILE_EXTENSION = ".txt"; // ADD: '.csv'

    private static final String FIELDS_SEPARATOR = ",";
    private static final int FIELDS_NUMBER = 17;

    private LinkedList event_queue = new LinkedList();
    private BufferedReader fileInput;
    private int linesNo;
    private int currentLineNo;
    private int descLinesNo;


    public L2TFilter(IFormGenerator formGenerator) {
        super(formGenerator, NAME, INPUT_FILE_EXTENSION, DESCRIPTION);
    }

    @Override
    public Source init(String filename, Component parent) {
        try {
            fileInput = new BufferedReader(new FileReader(filename));
            linesNo = countLines(filename);
        } catch (IOException ioe) {
            return null;
        }

        if (FirstLineIsExtra(fileInput))
            descLinesNo = 1;

        return new Source(NAME, filename, Source.GRANULARITY_SEC);
    }

    private int countLines(String filename) throws IOException {
        int counter = 0;

        while((fileInput.readLine()) != null) {
            counter++;
        }

        fileInput.close();
        fileInput = new BufferedReader(new FileReader(filename));
        
        // The reader's not closed for a purpose
        return counter;
    }

    private boolean FirstLineIsExtra(BufferedReader fileInput) {
        try {
            String desc = fileInput.readLine();
            String[] fields = desc.split(FIELDS_SEPARATOR);
            if (fields.length != FIELDS_NUMBER)
                return false;

            if (fields[0].equals("date") && fields[1].equals("time") && fields[10].equals("desc") && fields[16].equals("extra"))
                return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public AtomicEvent getNextEvent() {
        String line;
        String[] fields;

        if (event_queue.isEmpty()) {

            while (true) {

                try {
                    line = fileInput.readLine();
                    currentLineNo++;
                } catch (IOException ioe) {
                    return null;
                }

                if (line == null) return null;

                fields = line.split(FIELDS_SEPARATOR);

                if (fields.length != FIELDS_NUMBER)
                    System.err.println("Line not in a proper format: " + line);
                else
                    break;
            }

            String date = fields[0];
            String time = fields[1];
            ITimestamp timestamp = CreateTimestamp(date, time);

            //
            // If the order of fields change, remember to correct them in the Coloring Template!
            //
            return new L2TEvent(timestamp, fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8], fields[9],
                    fields[10], Integer.decode(fields[11]), fields[12], fields[13], fields[14], fields[15], fields[16],
                    formGenerator);
        } else {
            return (AtomicEvent) event_queue.removeFirst();
        }
    }

    private ITimestamp CreateTimestamp(String date, String time) {
        String[] dateFields = date.split("/"); // format: MM/DD/YYYY
        String[] timeFields = time.split(":"); // format: HH:MM:SS

        if (dateFields.length != 3 || timeFields.length != 3)
            return null;


        // Timestamp class needs updating so that the code below doesn't need to subtract any values!
        //
        // if you use decode() instead of valueOf() then if you pass octal string (e.g. 08) it can throw an exception
        int year = Integer.valueOf(dateFields[2]) - 1900;
        int month = Integer.valueOf(dateFields[0]) - 1;
        int day = Integer.valueOf(dateFields[1]);

        int hour = Integer.valueOf(timeFields[0]);
        int minute = Integer.valueOf(timeFields[1]);
        int second = Integer.valueOf(timeFields[2]);

        return new Timestamp(year, month, day, hour, minute, second,
                0); // nanoseconds
    }

    @Override
    public long getExactCount() {
        return 0;
    }

    @Override
    public long getTotalCount() {
        return linesNo - descLinesNo;
    }

    @Override
    public long getProcessedCount() {
        return currentLineNo;
    }


}
