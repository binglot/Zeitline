package org.Zeitline.OpenFileFilters;

import org.Zeitline.Utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

abstract class AbstractFileFilter extends FileFilter {
    private String fileExtension;
    private String description;


    public AbstractFileFilter(String name, String fileExtension) {
        this.description = name + " (" + fileExtension.toUpperCase() + ")";
        this.fileExtension = fileExtension;
    }

    public boolean accept(File file) {
        if (file.isDirectory())
            return true;

        return AcceptInputFile(file.getName());
    }

    public String getDescription() {
        return description;
    }

    private boolean AcceptInputFile(String filename) {
        if (fileExtension == null)
            return false;

        return Utils.endsWithCaseInsensitive(filename, fileExtension);
    }
}
