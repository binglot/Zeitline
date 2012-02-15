package org.Zeitline.Plugin;

import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.Plugin.Input.InputFilter;
import org.Zeitline.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPluginLoader extends ClassLoader {

    protected final String FILE_EXTENSION = ".class";
    protected final String folderName;
    protected final IFormGenerator formGenerator;
    protected final List<InputFilter> inputFilters;
    protected final String runningLocation;
    protected final String packageName;

    FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return Utils.endsWithCaseInsensitive(name, FILE_EXTENSION);
        }
    };


    public AbstractPluginLoader(String folderName, IFormGenerator formGenerator) {
        this.folderName = folderName;
        this.formGenerator = formGenerator;

        inputFilters = new ArrayList<InputFilter>();
        runningLocation = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        packageName = getClass().getPackage().getName();
    }


    public List<InputFilter> getPlugins() {
        List<InputFilter> plugins;

        // If the application is run from a JAR file, try to find embedded plugins
        if (Utils.containsCaseInsensitive(runningLocation, ".jar")) {
            if ((plugins = getPluginsFromJar(runningLocation, folderName)) != null)
                inputFilters.addAll(plugins);
        }

        // Look for the plugins in the 'filters' directory
        String pluginsDir = getPluginsDir();
        if ((plugins = getPluginsFromDir(pluginsDir)) != null)
            inputFilters.addAll(plugins);

        return inputFilters;
    }

    private String getPluginsDir() {
        char fileSeparator = System.getProperty("file.separator").toCharArray()[0];
        String packageDir = packageName.replace('.', fileSeparator);

        if (new File(runningLocation).isFile()) {
            String workingDir = new File(runningLocation).getParent();
            return Utils.pathJoin(workingDir, packageDir, folderName);
        }

        return Utils.pathJoin(runningLocation, packageDir, folderName);
    }

    protected Class DefineClassFromReadBytes(String className, int classSize, byte[] classData) {
        String fullClassName = packageName + "." + folderName + "." + className;
        Class classDef = defineClass(fullClassName, classData, 0, classSize);
        resolveClass(classDef);

        return classDef;
    }


    abstract List<InputFilter> getPluginsFromDir(String pluginsDir);

    abstract List<InputFilter> getPluginsFromJar(String runningLocation, String folderName);


}
