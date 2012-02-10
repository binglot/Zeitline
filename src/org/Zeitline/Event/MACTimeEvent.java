package org.Zeitline.Event;

import org.Zeitline.FormGenerator;
import org.Zeitline.Timestamp.ITimestamp;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class for the basic, discrete events that are imported from
 * various sources. In addition to the fields and methods from the
 * abstract {@link org.Zeitline.Event.TimeEvent org.Zeitline.Event.TimeEvent} class, there are also fields
 * and methods for managing the source from where the event is
 * imported and a user identifier (though not currently used).
 */
public class MACTimeEvent
    extends AtomicEvent
    implements Serializable {
    
    public static final int TYPE_M = 0;
    public static final int TYPE_A = 1;
    public static final int TYPE_C = 2;
    public static final int TYPE_MA = 3;
    public static final int TYPE_MC = 4;
    public static final int TYPE_AC = 5;
    public static final int TYPE_MAC = 6;

    /* This can be uncommented and modified for a custom icon
       files need to be in PNG format (e.g. cancel.png) and the
       image should be 16x16 pixels
      
    protected static ImageIcon icon = org.Zeitline.Zeitline.createNavigationIcon("cancel");
    */
    protected static JPanel panel = null;
    protected static JLabel lbl_mtime;
    protected static JLabel lbl_atime;
    protected static JLabel lbl_ctime;
    protected static JLabel lbl_uid;
    protected static JLabel lbl_gid;
    protected static JLabel lbl_permissions;
    protected static JLabel lbl_size;
    protected static JLabel lbl_filename;
    protected static JLabel lbl_filepath;
    
    protected String filename;
    protected ITimestamp mtime;
    protected ITimestamp atime;
    protected ITimestamp ctime;
    protected int user_id;
    protected int group_id;
    protected int mode;
    protected long size;
    protected int type;


    public MACTimeEvent(String filename,
			ITimestamp mtime,
			ITimestamp atime,
			ITimestamp ctime,
			int user_id,
			int group_id,
			int mode,
			long size,
			int type) {

	if (panel == null) {
		initPanel();
	}
	
	this.filename = filename;
	this.mtime = mtime;
	this.atime = atime;
	this.ctime = ctime;
	this.user_id = user_id;
	this.group_id = group_id;
	this.mode = mode;
	this.size = size;
	this.type = type;
        this.uniqueId = new Long(idCounter);
	idCounter++;

	if (type == TYPE_M  || type == TYPE_MA ||
	    type == TYPE_MC || type == TYPE_MAC)
	    this.startTime = mtime;
	else if (type == TYPE_A || type == TYPE_AC)
	    this.startTime = atime;
	else
	    this.startTime = ctime;

	adjustedTime = startTime;
	reportedTime = startTime;
	
    }
    
    /**
     * Returns the name of the event.
     *
     * @return the name of the event, {@link #name name}
     */
    public String getName() {
	return getPrefix(type) + filename;
	//	return name;
    } // getName
    
    /**
     * Returns the description of the event.
     *
     * @return the description of the event, {@link #description description}
     */
    public String getDescription() {

	return "Type: " + getPrefix(type) +
	    "\nFilename: " + filename +
	    "\nMtime: " + mtime + 
	    "\nATime: " + atime +
	    "\nCTime: " + ctime + 
	    "\nPermissions: " + getPermissions(mode)+
	    "\nSize: " + size +
	    "\nUser ID: " + user_id +
	    "\nGroup ID: " + group_id +
	    "\nMode: " + mode;
    } // getDescription
    
    /**
     * Returns a string representation of the event. This is currently the
     * {@link #name name} field.
     *
     * @return a string representation of the event
     */
    public String toString() {
	return getName();
    } // toString

    
/* This can be uncommented for a custom icon    
    public ImageIcon getIcon() {
	return icon;
    }
*/
    
    public JPanel getPanel() {
	if (panel == null)
		initPanel();
	return panel;
    }
	
    public void setPanelValues() {
	    if (panel == null)
		    initPanel();
	    File f = new File(filename);
	    lbl_filename.setText(f.getName());
	    lbl_filepath.setText(f.getParent());
	    lbl_size.setText(new Long(size).toString());
	    lbl_mtime.setText(mtime.toString());
	    lbl_atime.setText(atime.toString());
	    lbl_ctime.setText(ctime.toString());
	    lbl_uid.setText(new Integer(user_id).toString());
	    lbl_gid.setText(new Integer(group_id).toString());
	    lbl_permissions.setText(getPermissions(mode));
    }

    protected static String getPrefix(int type) {
	if (type == TYPE_M)  return "M.. ";
	if (type == TYPE_A)  return ".A. ";
	if (type == TYPE_C)  return "..C ";
	if (type == TYPE_MA) return "MA. ";
	if (type == TYPE_MC) return "M.C ";
	if (type == TYPE_AC) return ".AC ";
	return "MAC ";
    }

    public static String getPermissions(int mode) {

	int i;
	String ret = "";
	for (i = 0; i < 9; i++) {
		if ((mode & (1 << i)) > 0 ) {
			switch (i % 3) {
				case 0:
				  if ((i == 3) && ((mode & 01000) > 0))
					  ret = "t" + ret;
				  else if ((i == 6) && ((mode & 02000) > 0))
					  ret = "s" + ret;
				  else if ((i == 9) && ((mode & 04000) > 0))
					  ret = "s" + ret;
				  else
					  ret = "x" + ret;
				  break;
				case 1:
				  ret = "w" + ret;
				  break;
				case 2:
				  ret = "r" + ret;
			}
		}
		else {
			if ((i == 3) && ((mode & 01000) > 0))
				ret = "T" + ret;
			else if ((i == 6) && ((mode & 02000) > 0))
				ret = "S" + ret;
			else if ((i == 9) && ((mode & 04000) > 0))
				ret = "S" + ret;
			else
				ret = "-" + ret;
		}
	}
	switch (mode >> 12) {
		case 1:
		  ret = "p" + ret;
		  break;
		case 2:
		  ret = "c" + ret;
		  break;
		case 4:
		  ret = "d" + ret;
		  break;
		case 6:
		  ret = "b" + ret;
		  break;
		case 8:
		  ret = "-" + ret;
		  break;
		case 10:
		  ret = "l" + ret;
		  break;
		case 12:
		  ret = "s" + ret;
		  break;	
	}
	return ret;

    }

    private void initPanel() {
	
	    Vector items = new Vector();
	    lbl_filename = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Filename:", lbl_filename));
	    lbl_filepath = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Path:", lbl_filepath));		
	    lbl_size = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Size:", lbl_size));
	    lbl_mtime = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("MTime:", lbl_mtime));
	    lbl_atime = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("ATime:", lbl_atime));
	    lbl_ctime = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("CTime:", lbl_ctime));
	    lbl_uid = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("User ID:", lbl_uid));
	    lbl_gid = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Group ID:", lbl_gid));
	    lbl_permissions = new JLabel("", JLabel.LEADING);
	    items.add(FormGenerator.getFormItem("Permissions:", lbl_permissions));

	    panel = FormGenerator.createForm(items);
	    
    }
    
} // class org.Zeitline.Event.MACTimeEvent
