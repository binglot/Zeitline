package org.Zeitline.GUI.Graphics;

import org.Zeitline.Zeitline;

import javax.swing.*;
import java.net.URL;

public class IconRepository implements IIconRepository<ImageIcon> {
    private static final String ICONS_DIR = "icons";
    private static final String ICONS_EXTENSION = ".png";

    private String getFileName(IconNames name){
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
            case CreateEvent:
                fileName = "create_event";
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
            case Help:
                fileName = "help";
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
