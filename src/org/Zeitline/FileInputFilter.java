package org.Zeitline;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public final class FileInputFilter extends FileFilter {
    private String file_extension;
    private String description;

    public FileInputFilter(String file_extension, String descr) {
        this.file_extension = file_extension;
        this.description = descr;
    } // org.Zeitline.FileInputFilter

    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        if (file_extension != null)
            return file.getName().endsWith(file_extension);
        else
            return true;
    } // accept

    public String getDescription() {
        return description;
    } // getDescription
} // class org.Zeitline.FileInputFilter
