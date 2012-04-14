package org.Zeitline.Plugin;

import org.Zeitline.Start;
import org.Zeitline.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractPluginLoader<T> extends ClassLoader {

    // 14 Apr 2012: Dirty solution just for now
    boolean IS_JAR_FILE = false;
    // =

    protected final String PLUGIN_FILE_EXTENSION = ".class";
    protected final String JAR_FILE_EXTENSION = ".jar";

    protected final String folderName;
    protected final List<T> inputFilters;
    protected final String runningLocation;
    protected final String rootPackageName;

    FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return Utils.endsWithCaseInsensitive(name, PLUGIN_FILE_EXTENSION);
        }
    };


    public AbstractPluginLoader(String folderName) {
        this.folderName = folderName;

        inputFilters = new ArrayList<T>();
        runningLocation = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        //noinspection InstantiatingObjectToGetClassObject
        rootPackageName = new Start().getClass().getPackage().getName();
    }


    public List<T> getPlugins() {
        List<T> plugins;

        // If the application is run from a JAR file, try to find embedded plugins
        if (Utils.endsWithCaseInsensitive(runningLocation, JAR_FILE_EXTENSION)) {
            if ((plugins = getPluginsFromJar(runningLocation, folderName)) != null)
                inputFilters.addAll(plugins);
        }

        // Look for the plugins in their directory
        String pluginsDir = getPluginsDir();
        if ((plugins = getPluginsFromDir(pluginsDir)) != null)
            inputFilters.addAll(plugins);

        return inputFilters;
    }

    private String getPluginsDir() {
        if (IS_JAR_FILE)
            return folderName;
        
        final char fileSeparator = File.separatorChar;
        String packageDir = rootPackageName.replace('.', fileSeparator);

        if (new File(runningLocation).isFile()) {
            String workingDir = new File(runningLocation).getParent();
            return Utils.pathJoin(workingDir, packageDir, folderName);
        }

        return Utils.pathJoin(runningLocation, packageDir, folderName);
    }

    protected Class DefineClassFromReadBytes(String className, int classSize, byte[] classData) {
        String fullClassName = rootPackageName + "." + folderName + "." + className;
        Class classDef = defineClass(fullClassName, classData, 0, classSize);
        resolveClass(classDef);

        return classDef;
    }


    abstract List<T> getPluginsFromDir(String pluginsDir);

    abstract List<T> getPluginsFromJar(String runningLocation, String folderName);


}
