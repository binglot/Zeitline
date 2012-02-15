package org.Zeitline.Plugin;

import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.Plugin.Input.InputFilter;
import org.Zeitline.Utils;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PluginLoader extends AbstractPluginLoader {

    public PluginLoader(String folderName, IFormGenerator formGenerator) {
        super(folderName, formGenerator);
    }

    private enum Streamer {
        File,
        Buffer
    }

    final protected List<InputFilter> getPluginsFromDir(String dirName) {
        List<InputFilter> result = new ArrayList<InputFilter>();
        Class classDefinition;

        File pluginDir = new File(dirName);
        if (!pluginDir.exists())
            return null;

        // Look for the '.class' files in the provided directory
        String[] plugins = pluginDir.list(filter);
        for (final String plugin : plugins) {
            String fileName = Utils.pathJoin(dirName, plugin);

            // Determine the size of the class file in bytes
            String className = Utils.stripFileExtension(plugin);
            int classSize = new Long(new File(fileName).length()).intValue();
            byte[] classData = new byte[classSize];

            if (!ReadClassFileDirectly(fileName, classSize, classData))
                continue;

            if (findLoadedClass(className) == null) {
                classDefinition = DefineClassFromReadBytes(className, classSize, classData);
            } else {
                System.err.println("Warning: the filter of name `" + className + "` is already loaded, not loading this instance.");
                continue;
            }

            InputFilter classInstance = InstantiateDefinedClass(classDefinition);

            if (classInstance != null)
                result.add(classInstance);
        }

        return result;
    }

    final protected  List<InputFilter> getPluginsFromJar(String jarLocation, String pluginDirName) {
        List<InputFilter> result = new ArrayList<InputFilter>();
        String jarFilePath = (new File(jarLocation)).getAbsolutePath();

        JarFile jarFile = SafelyLoadJarFile(jarFilePath);
        if (jarFile == null) {
            System.err.println("Could not load the jar file provided as a source for input inputFilters");
            return result;
        }

        // look through the Jar file's entries for '.class' files
        Enumeration<JarEntry> jarFileEntries = jarFile.entries();
        Class classDefinition;

        while (jarFileEntries.hasMoreElements()) {
            String entryName = jarFileEntries.nextElement().toString();

            if (entryName.startsWith(pluginDirName) && Utils.endsWithCaseInsensitive(entryName, FILE_EXTENSION)) {
                String fileName = Utils.getFileName(entryName);
                String className = Utils.stripFileExtension(fileName);

                JarEntry jarEntry = jarFile.getJarEntry(entryName);
                int classSize = new Long(jarEntry.getSize()).intValue();
                byte[] classData = new byte[classSize];

                if (!ReadClassFileFromJar(jarFile, entryName, classSize, classData))
                    continue;

                if (findLoadedClass(className) == null) {
                    classDefinition = DefineClassFromReadBytes(className, classSize, classData);
                } else {
                    System.err.println("Warning: the filter of name `" + className + "` is already loaded, not loading this instance.");
                    continue;
                }

                InputFilter classInstance = InstantiateDefinedClass(classDefinition);

                if (classInstance != null)
                    result.add(classInstance);
            }
        }

        return result;
    }

    private JarFile SafelyLoadJarFile(String jarFileName) {
        JarFile jarFile = null;

        try {
            jarFile = new JarFile(jarFileName);
        } catch (java.io.FileNotFoundException fnf) {
            System.err.println(fnf);
        } catch (java.io.IOException io) {
            System.err.println(io);
        }

        return jarFile;
    }

    private boolean ReadClassFileFromJar(JarFile jarFile, String fileName, int classSize, byte[] classData) {
        return ReadClassFile(jarFile, fileName, classSize, classData, Streamer.Buffer);
    }

    private boolean ReadClassFileDirectly(String fileName, int classSize, byte[] classData) {
        return ReadClassFile(null, fileName, classSize, classData, Streamer.File);
    }

    private boolean ReadClassFile(JarFile jarFile, String fileName, int classSize, byte[] classData, Streamer streamer) {
        InputStream inputStream = null;
        int readSize;

        try {
            switch (streamer) {
                case File:
                    inputStream = new FileInputStream(fileName);
                    break;
                case Buffer:
                    JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    inputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry));
                    break;
            }

            readSize = inputStream.read(classData, 0, classSize);
            inputStream.close();
        } catch (IOException io) {
            System.err.println(io);
            return true;
        }

        return readSize == classSize;
    }

    private InputFilter InstantiateDefinedClass(Class classDef) {
        InputFilter classInst = null;

        try {
            classInst = InstantiateClassViaConstructorWithArgs(classDef);
            //class_inst = (InputFilter) class_def.newInstance(); // instantiate class with default constructor
        } catch (InstantiationException inst) {
            System.err.println(inst);
        } catch (IllegalAccessException ill) {
            System.err.println(ill);
        } catch (ClassCastException cc) {
            // It's probably an IDE bug. Workaround: via context menu go to Run > Edit Configuration and then Application > Zeitline.
            System.err.println(cc);
            System.err.println("If you're using IntelliJ IDEA, make sure you turn off \"Enable Capturing Form Snapshots\"");
        } catch (NoSuchMethodException nsm) {
            System.err.println(nsm);
        } catch (InvocationTargetException it) {
            System.err.println(it);
        }

        return classInst;
    }

    private InputFilter InstantiateClassViaConstructorWithArgs(Class classDef) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class[] constructorArgs = {IFormGenerator.class};
        Object[] constructorParams = {formGenerator};
        Constructor constructor = classDef.getConstructor(constructorArgs);

        return (InputFilter) constructor.newInstance(constructorParams);
    }
}
