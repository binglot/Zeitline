package org.Zeitline;

import java.io.File;
import java.util.regex.Pattern;

// Non-instantiable utility class
public class Utils {
    // Suppress default constructor
    private Utils() {}

    static boolean containsCaseInsensitive(String target, String pattern) {
        // taken from http://stackoverflow.com/questions/86780/is-the-contains-method-in-java-lang-string-case-sensitive
        return Pattern.compile(Pattern.quote(target), Pattern.CASE_INSENSITIVE).matcher(pattern).find();
    }

    static String pathJoin(String path1, String path2) {
        // taken from http://stackoverflow.com/questions/711993/does-java-have-a-path-joining-method
        return new File(path1, path2).toString();
    }

    static String stripFileExtension(String str) {
        // taken from http://stackoverflow.com/questions/924394/how-to-get-file-name-without-the-extension
        if (str == null)
            return null;

        int pos = str.lastIndexOf(".");
        if (pos == -1)
            return str;

        return str.substring(0, pos);
    }
}
