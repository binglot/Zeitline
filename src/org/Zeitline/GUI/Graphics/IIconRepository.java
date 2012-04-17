package org.Zeitline.GUI.Graphics;

import java.net.URL;

public interface IIconRepository<T> {
    T getIcon(IconNames imageName);
    URL getIconUrl(IconNames imageName);
}

