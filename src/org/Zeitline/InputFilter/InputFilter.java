package org.Zeitline.InputFilter;

import java.awt.Component;
import javax.swing.filechooser.FileFilter;
import javax.swing.JComponent;
import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.Source;

public abstract class InputFilter {
    protected IFormGenerator formGenerator;

    public InputFilter(IFormGenerator formGenerator){
        this.formGenerator = formGenerator;
    }
    public abstract Source init(String location, Component parent);
    public abstract AtomicEvent getNextEvent();
    public abstract FileFilter getFileFilter();
    public abstract String getName();
    public abstract String getDescription();
    public abstract long getExactCount();
    public abstract long getTotalCount();
    public abstract long getProcessedCount();
    public String[] getParameterLabels() { return null; }
    public JComponent[] getParameterFields() { return null; }
    public void setParameterValues(Object[] values) {}
    public final String toString() { return getName(); }
}
