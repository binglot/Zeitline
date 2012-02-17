package org.Zeitline.Plugin.Input;

import java.awt.Component;
import javax.swing.filechooser.FileFilter;
import javax.swing.JComponent;
import org.Zeitline.Event.AtomicEvent;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.OpenFileFilters.FileFilterBase;
import org.Zeitline.Source;

public abstract class InputFilter {
    protected IFormGenerator formGenerator;
    private FileFilter openFileFilter;
    private final String name;
    //private final String fileExtension;
    private final String description;

    protected InputFilter(IFormGenerator formGenerator, String name, String fileExtension, String description){
        this.formGenerator = formGenerator;
        this.name = name;
        //this.fileExtension = fileExtension;
        this.description = description;

        openFileFilter = new FileFilterBase(name, fileExtension);
    }

    public FileFilter getFileFilter() { return openFileFilter; }

    public String getName() { return name; }

    public String getDescription() { return description; }


    public abstract Source init(String location, Component parent);
    public abstract AtomicEvent getNextEvent();
    public abstract long getExactCount();
    public abstract long getTotalCount();
    public abstract long getProcessedCount();
    public String[] getParameterLabels() { return null; }
    public JComponent[] getParameterFields() { return null; }
    public void setParameterValues(Object[] values) {}
    public final String toString() { return getName(); }
}
