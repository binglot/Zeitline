package org.Zeitline;

import org.Zeitline.GUI.FormGenerator;
import org.Zeitline.GUI.IFormGenerator;
import org.Zeitline.InputFilter.InputFilter;

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


    public Enumeration getPluginsFromJar(String jarLocation, String dirName) {
        Vector result = new Vector();
        String jarFileName = (new File(jarLocation)).getAbsolutePath();

        // create the JarFile object
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarFileName);
        } catch (java.io.FileNotFoundException fnf) {
            System.err.println(fnf);
            return result.elements();
        } catch (java.io.IOException io) {
            System.err.println(io);
            return result.elements();
        }

        if (jarFile == null) {
            System.err.println("Jar file is null");
            return result.elements();
        }

        // look through all of the Jar file's entries for classes in the
        // provided plugin directory
        Enumeration jar_file_entries = jarFile.entries();
        String entry_name = "";
        JarEntry jar_entry = null;
        BufferedInputStream instream = null;
        String class_name = "";
        int class_size = -1;
        byte[] class_data = null;
        Class class_def = null;
        InputFilter class_inst = null;
        while (jar_file_entries.hasMoreElements()) {
            entry_name = jar_file_entries.nextElement().toString();
            if (entry_name.startsWith(dirName) && (entry_name.indexOf(".class") >= 0)) {
                // get the actual JarEntry
                jar_entry = jarFile.getJarEntry(entry_name);
                class_name = jar_entry.toString();
                class_name = class_name.substring((dirName.length() + 1),
                        class_name.lastIndexOf(".class"));

                // pull the data of the Class out of the JarEntry
                class_size = new Long(jar_entry.getSize()).intValue();
                class_data = new byte[class_size];
                try {
                    // open the stream to the JarEntry
                    instream = new BufferedInputStream(jarFile.getInputStream(jar_entry));
                    instream.read(class_data, 0, class_size);
                    instream.close();
                } catch (IOException io) {
                    System.err.println(io);
                    continue;
                }

                // create the Class object
                class_def = defineClass(class_name, class_data, 0, class_size);
                resolveClass(class_def);

                // instantiate a copy of the new org.Zeitline.InputFilter.org.Zeitline.InputFilter object
                try {
                    class_inst = (InputFilter) class_def.newInstance();
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
                result.addElement(class_inst);
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

            if (!ReadClassFile(fileName, classSize, classData))
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

    private boolean ReadClassFile(String fileName, int classSize, byte[] classData) {
        int readSize;

        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            readSize = inputStream.read(classData, 0, classSize);
            inputStream.close();
        } catch (IOException io) {
            System.err.println(io);
            return true;
        }

        return readSize == classSize;
    }
}
