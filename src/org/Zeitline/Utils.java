package org.Zeitline;

import java.util.regex.Pattern;

// Non-instantiable utility class
public class Utils {
    // Suppress default constructor
    private Utils() {}

    static boolean ContainsCaseInsensitive(String target, String pattern) {
        // taken from http://stackoverflow.com/questions/86780/is-the-contains-method-in-java-lang-string-case-sensitive
        return Pattern.compile(Pattern.quote(target), Pattern.CASE_INSENSITIVE).matcher(pattern).find();
    }
}
