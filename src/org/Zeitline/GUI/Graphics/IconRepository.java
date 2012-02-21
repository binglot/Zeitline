package org.Zeitline.GUI.Graphics;

import org.Zeitline.GUI.Action.FindAction;
import org.Zeitline.Utils;
import org.Zeitline.Zeitline;

import javax.swing.*;
import java.net.URL;

public class IconRepository implements  IIconRepository<ImageIcon> {
    private static final String ICONS_DIR = "icons";
    private static final String ICONS_EXTENSION = ".png";

//    saveAction = new SaveAction(this, icons.getIcon("filesave"), KeyEvent.VK_S);
//    loadAction = new LoadAction(this, icons.getIcon("fileopen"), KeyEvent.VK_L);
//    cutAction = new CutAction(this, icons.getIcon("editcut"), KeyEvent.VK_T);
//    pasteAction = new PasteAction(this, icons.getIcon("editpaste"), KeyEvent.VK_P);
//    findAction = new FindAction(this, icons.getIcon("find"), KeyEvent.VK_D);
//    createFrom = new CreateFromAction(this, icons.getIcon("create_event"), KeyEvent.VK_C);
//    removeEvents = new RemoveEventsAction(this, icons.getIcon("delete_event"), KeyEvent.VK_R);
//    importAction = new ImportAction(this, icons.getIcon("import"), KeyEvent.VK_I, inputFilters);
//    emptyTimeline = new EmptyTimelineAction(this, icons.getIcon("new_timeline"), KeyEvent.VK_E);
//    createTimelineFrom = new CreateTimelineFromAction(this, icons.getIcon("create_timeline"), KeyEvent.VK_C);
//    deleteTimeline = new DeleteTimelineAction(this, icons.getIcon("delete_timeline"), KeyEvent.VK_D);
//    moveLeft = new MoveLeftAction(this, icons.getIcon("moveleft"), KeyEvent.VK_L);
//    moveRight = new MoveRightAction(this, icons.getIcon("moveright"), KeyEvent.VK_R);
//    filterQueryAction = new FilterQueryAction(this, icons.getIcon("filter"), KeyEvent.VK_F);

    public ImageIcon getIcon(IconNames name) {
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
        }

        return getIcon(fileName);
    }

    private ImageIcon getIcon(String imageName) {
        String imgLocation = Utils.pathJoin(ICONS_DIR, imageName + ICONS_EXTENSION);
        URL imageURL = Zeitline.class.getResource(imgLocation);

        if (imageURL != null)
            return new ImageIcon(imageURL);

        System.err.println("Resource not found: " + imgLocation);
        return null;
    }

}
