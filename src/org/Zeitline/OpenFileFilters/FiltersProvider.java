package org.Zeitline.OpenFileFilters;

import javax.swing.filechooser.FileFilter;
import java.util.*;

public class FiltersProvider {
    List<FileFilter> list = new ArrayList<FileFilter>();

    public List<FileFilter> getFilters(){
        list.add(new ZeitlineProject());
        // ...

        return list;
    }
}
