package org.Zeitline.GUI.Graphics;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.net.URL;

public class IconRepository implements IIconRepository<ImageIcon> {
    private static final String ICONS_DIR = "icons";
    private static final String ICONS_EXTENSION = ".png";

    private String getFileName(IconNames name) {
        String fileName = null;

        switch (name) {
            case FileSave:
                fileName = "filesave";
                break;
            case FileOpen:
                fileName = "fileopen";
                break;
            case EditCut:
                fileName = "editcut";
                break;
            case EditPaste:
                fileName = "editpaste";
                break;
            case Find:
                fileName = "find";
                break;
            case Group:
                fileName = "group_events";
                break;
            case DeleteEvent:
                fileName = "delete_event";
                break;
            case Import:
                fileName = "import";
                break;
            case NewTimeline:
                fileName = "new_timeline";
                break;
            case CreateTimeline:
                fileName = "create_timeline";
                break;
            case DeleteTimeline:
                fileName = "delete_timeline";
                break;
            case MoveLeft:
                fileName = "moveleft";
                break;
            case MoveRight:
                fileName = "moveright";
                break;
            case Filter:
                fileName = "filter";
                break;
            case Edit:
                fileName = "edit";
                break;
            case Cancel:
                fileName = "cancel";
                break;
            case AtomicSmall:
                fileName = "atomic_small";
                break;
            case ComplexSmall:
                fileName = "complex_small";
                break;
            case Unknown:
                fileName = "unknown";
                break;
            case Info:
                fileName = "info";
                break;
            case Exit:
                fileName = "exit";
                break;
            case Orphan:
                fileName = "orphan";
                break;
            case DateFormat:
                fileName = "date_format";
                break;
            case DateFull:
                fileName = "date_full";
                break;
            case DateShort:
                fileName = "date_short";
                break;
            case Sort:
                fileName = "sort_az";
                break;
            case SortAsc:
                fileName = "sort_asc";
                break;
            case SortDesc:
                fileName = "sort_desc";
                break;
            case FileExport:
                fileName = "file_export";
                break;
            case GraphicDesign:
                fileName = "graphic_design";
                break;
            case Appearance:
                fileName = "appearance";
                break;
            default:
                System.err.println("Could not find the requested icon: " + name.toString());
                break;
        }

        return fileName;
    }


    public ImageIcon getIcon(IconNames name) {
        return getImage(name);
    }

    @Override
    public URL getIconUrl(IconNames imageName) {
        String fileName = getFileName(imageName);

        String imgLocation = ICONS_DIR + "/" + fileName + ICONS_EXTENSION;
        return Zeitline.class.getResource(imgLocation);
    }

    private ImageIcon getImage(IconNames name) {
        // Avoid using Utils.PathJoin() below!
        // That's because it concatenates using '\' which breaks the .JAR file.
        //
        String fileName = getFileName(name);
        String imgLocation = ICONS_DIR + "/" + fileName + ICONS_EXTENSION;
        URL imageURL = Zeitline.class.getResource(imgLocation);

        if (imageURL != null) {
            return new ImageIcon(imageURL);
        }

        System.err.println("Resource not found: " + imgLocation);
        return null;
    }
}
