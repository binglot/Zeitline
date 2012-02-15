package org.Zeitline;

import org.Zeitline.GUI.Graphics.IIconRepository;
import org.Zeitline.GUI.Graphics.IconRepository;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class QueryDisplay extends JPanel {

    protected JButton change, remove;
    protected JTextField text;
    private IIconRepository<ImageIcon> icons = new IconRepository();

    public QueryDisplay(Query q, ActionListener al) {

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        text = new JTextField(q.toString(), 1);
        text.setBackground(this.getBackground());
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        this.add(text);
        this.add(Box.createHorizontalGlue());

        change = new JButton(icons.getIcon("edit"));
        change.setBorderPainted(false);
        change.setMargin(new Insets(0, 0, 0, 0));
        change.setActionCommand("change");
        change.addActionListener(al);
        change.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (change.isEnabled())
                    change.setBorderPainted(true);
            }

            public void mouseExited(MouseEvent e) {
                change.setBorderPainted(false);
            }
        });

        this.add(change);

        remove = new JButton(icons.getIcon("cancel"));
        remove.setBorderPainted(false);
        remove.setMargin(new Insets(0, 0, 0, 0));
        remove.setActionCommand("remove");
        remove.addActionListener(al);
        remove.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (remove.isEnabled())
                    remove.setBorderPainted(true);
            }

            public void mouseExited(MouseEvent e) {
                remove.setBorderPainted(false);
            }
        });
        this.add(remove);
    } // org.Zeitline.QueryDisplay

    public void setEnabled(boolean enable) {
        text.setEnabled(enable);
        change.setEnabled(enable);
        remove.setEnabled(enable);
    } // setEnabled

    public String toString() {
        return text.getText();
    } // toString

} // class org.Zeitline.QueryDisplay
