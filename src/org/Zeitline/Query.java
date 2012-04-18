package org.Zeitline;

import org.Zeitline.Event.AbstractTimeEvent;
import org.Zeitline.Timestamp.Timestamp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {

    protected String actualString = null;
    protected String stringQuery = null;
    protected String regexString = null;
    protected Timestamp intervalStart = null;
    protected Timestamp intervalEnd = null;
    protected Pattern pattern = null;
    protected Source sourceQuery = null;

    public Query(String key, String regex) {
        this(null, null, key, regex);
    }

    public Query(Timestamp start, Timestamp end, String key, String regex) {

        intervalStart = start;
        intervalEnd = end;

        if (key != null && (key.matches("\\A\\s*\\Z")))
            key = null;

        actualString = key;
        regexString = regex;

        if (key == null)
            stringQuery = null;
        else
            // The (?s) sets the DOTALL mode (otherwise \n isn't matched)
            stringQuery = "(?s).*" + key.toLowerCase() + ".*";

        if (regexString != null)
            pattern = Pattern.compile(regexString);
    }

    public Timestamp getIntervalStart() {
        return intervalStart;
    }

    public Timestamp getIntervalEnd() {
        return intervalEnd;
    }

    public String getStringText() {
        return actualString;
    }

    public String getRegexText() {
        return regexString;
    }

    public boolean matches(AbstractTimeEvent t) {

        if ((intervalStart != null) &&
                intervalStart.after(t.getStartTime()))
            return false;

        if ((intervalEnd != null) &&
                intervalEnd.before(t.getStartTime()))
            return false;

        if (stringQuery != null) {
            if (t.getName().toLowerCase().matches(stringQuery))
                return true;

            if (t.getDescription().toLowerCase().matches(stringQuery))
                return true;
        }

        if (regexString != null) {

            Matcher nameMatch = pattern.matcher(t.getName());
            if (nameMatch.matches())
                return true;

            Matcher descMatch = pattern.matcher(t.getDescription());
            if (descMatch.matches())
                return true;
        }

        return false;
    }

    public String toString() {

        StringBuilder ret = new StringBuilder();

        if (actualString != null)
            ret.append("Keyword: \"" + actualString + "\" ");

        if (regexString != null)
            ret.append("Regex: \"" + regexString + "\" ");
        
        if (intervalStart != null)
            ret.append("From: " + intervalStart + " ");

        if (intervalEnd != null)
            ret.append("Until: " + intervalEnd);

        return ret.toString();

    }

}