package org.exqudens.persistence.test.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ClassPathUtils {

    public static String toString(String classpath) throws Exception {
        return toList(classpath).stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> toList(String classpath) throws Exception {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        List<String> strings = null;
        try {
            inputStream = ClassPathUtils.class.getClassLoader().getResourceAsStream(classpath);
            if (inputStream == null) {
                return null;
            }
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);
            strings = bufferedReader
            .lines()
            .collect(Collectors.toList());
            return strings;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private ClassPathUtils() {
        super();
    }

}
