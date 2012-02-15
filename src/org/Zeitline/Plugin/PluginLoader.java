package org.Zeitline.Plugin;

import java.util.List;

public abstract class PluginLoader<T> {
    protected PluginLoaderBase<T> pluginLoader;
    private final String folderName;

    public PluginLoader(String folderName) {
        this.folderName = folderName;
    }

    protected void setInstantiator(IClassInstantiator<T> instantiator){
        pluginLoader = new PluginLoaderBase<T>(folderName, instantiator);
    }

    public List<T> getPlugins() {
        if (pluginLoader == null) {
            throw new InternalError("Error: you haven't assigned a class instantiator!");
        }

        return pluginLoader.getPlugins();
    }
}
