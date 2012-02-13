package org.Zeitline;

import org.Zeitline.GUI.FormGenerator;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.InputFilter.InputFilter;
import sun.reflect.generics.tree.IntSignature;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * NEEDS A MAJOR REWRITING!
 * <p/>
 * I'VE JUST MODIFIED HOW THE LOADED PLUGIN IS
 * INITIALIZED BUT A LOT OF THE CODE REPEATS
 */


public class PluginLoader extends ClassLoader {
    private static final String PACKAGE_NAME = "org.Zeitline.filters";
    private static final String FILE_EXTENSION = ".class";
    private IFormGenerator formGenerator = new FormGenerator();

    private FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.toLowerCase(Locale.ENGLISH).endsWith(FILE_EXTENSION);
        }
    };


    public Enumeration getPluginsFromJar(String jarLocation, String pluginDirName) {
        Vector result = new Vector();
        String jarFileName = (new File(jarLocation)).getAbsolutePath();

        // create the JarFile object
        JarFile jarFile = SafelyLoadJarFile(jarFileName);

        if (jarFile == null) {
            System.err.println("Jar file provided as an input filter is null");
            return result.elements();
        }

        // look through all of the Jar file's entries for classes in the
        // provided plugin directory
        Enumeration<JarEntry> jarFileEntries = jarFile.entries();
        String entryName = "";
        BufferedInputStream inputStream = null;
        int classSize = -1;
        byte[] classData = null;
        Class classDefinition = null;
        InputFilter classInstantiation = null;

        while (jarFileEntries.hasMoreElements()) {
            entryName = jarFileEntries.nextElement().toString();

            if (entryName.startsWith(pluginDirName) && (entryName.contains(FILE_EXTENSION))) { // instead of 'contains' it should read 'endsWith' + toLowerCase(Locale.ENGLISH)
                String fileName = Utils.getFileName(entryName);
                String className = Utils.stripFileExtension(fileName);

                // pull the data of the Class out of the JarEntry
                JarEntry jarEntry = jarFile.getJarEntry(entryName);
                classSize = new Long(jarEntry.getSize()).intValue();
                classData = new byte[classSize];

                if (!ReadClassFileFromJar(jarFile, entryName, classSize, classData))
                    continue;

                // create the Class object
                classDefinition = defineClass(className, classData, 0, classSize);
                resolveClass(classDefinition);

                // instantiate a copy of the new org.Zeitline.InputFilter.org.Zeitline.InputFilter object
                try {
                    classInstantiation = (InputFilter) classDefinition.newInstance();
                } catch (InstantiationException inst) {
                    System.err.println(inst);
                    continue;
                } catch (IllegalAccessException ill) {
                    System.err.println(ill);
                    continue;
                } catch (ClassCastException cc) {
                    System.err.println(cc);
                    continue;
                }

                // add the new org.Zeitline.InputFilter.org.Zeitline.InputFilter to the results
                result.addElement(classInstantiation);
            }
        }

        return result.elements();
    }

    public List<InputFilter> getPluginsFromDir(String dirName) {
        List<InputFilter> result = new ArrayList<InputFilter>();
        Class classDefinition;

        // Get all files ending in ".class" from the provided directory
        File pluginDir = new File(dirName);
        if (!pluginDir.exists())
            return null;

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
                System.err.println("Warning -- filter of name: " + className + " is already loaded, not loading this instance.");
                continue;
            }

            InputFilter classInstance = InstantiateDefinedClass(classDefinition);

            if (classInstance != null)
                result.add(classInstance);
        }

        return result;
    }

    private InputFilter InstantiateDefinedClass(Class classDef) {
        InputFilter classInst = null;

        try {
            classInst = InstantiateClassWithConstructor(classDef);
            //class_inst = (InputFilter) class_def.newInstance();
        } catch (InstantiationException inst) {
            System.err.println(inst);
        } catch (IllegalAccessException ill) {
            System.err.println(ill);
        } catch (ClassCastException cc) {
            // It's an IDE bug. To disable it: go via context menu Run > Edit Configuration and then Application > Zeitline.
            System.err.println(cc);
            System.err.println("If you're using IntelliJ IDEA, make sure you turn off \"Enable Capturing Form Snapshots\"");
        } catch (NoSuchMethodException nsm) {
            System.err.println(nsm);
        } catch (InvocationTargetException it) {
            System.err.println(it);
        }

        return classInst;
    }

    private InputFilter InstantiateClassWithConstructor(Class classDef) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class[] constructorArgs = {IFormGenerator.class};
        Object[] constructorParams = {formGenerator};
        Constructor constructor = classDef.getConstructor(constructorArgs);

        return (InputFilter) constructor.newInstance(constructorParams);
    }

    private Class DefineClassFromReadBytes(String className, int classSize, byte[] classData) {
        Class classDef = defineClass(PACKAGE_NAME + "." + className, classData, 0, classSize);
        resolveClass(classDef);

        return classDef;
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

    private boolean ReadClassFileFromJar(JarFile jarFile, String fileName, int classSize, byte[] classData) {
        return ReadClassFile(jarFile, fileName, classSize, classData, Streamer.Buffer);
    }

    private boolean ReadClassFileDirectly(String fileName, int classSize, byte[] classData) {
        return ReadClassFile(null, fileName, classSize, classData, Streamer.File);
    }

    enum Streamer {
        File,
        Buffer
    }
}
