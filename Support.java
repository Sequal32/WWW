package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

public class Support {
    private static String errorMessage = "";
    private static boolean wasError = false;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
    static SimpleDateFormat readableDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static final int DATE_LENGTH = readableDateFormat.toPattern().length();

    public static Date getDate(String s) {
        try {
            return dateFormat.parse(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String dateToString(Date d) {
        return readableDateFormat.format(d);
    }

    public static String getErrorMessage() {
        return errorMessage;
    }

    public static boolean wasError() {
        return wasError;
    }

    static void clearError() {
        wasError = false;
        errorMessage = "";
    }

    static void setError(final Exception e) {
        wasError = true;
        errorMessage = e.getMessage();
    }

    static void setErrorMessage(final String s) {
        wasError = true;
        errorMessage = s;
    }

    static public String readTextFile(final String file) {
        clearError();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (final Exception e) {
            setError(e);
            return "";
        }
        String line = null;
        final StringBuilder stringBuilder = new StringBuilder();
        final String ls = System.getProperty("line.separator");
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (final Exception e) {
            setError(e);
            return "";
        }
    }

    static public boolean fileExists(String file) {
        return new File(file).exists();
    }

    static public void writeTextFile(final String file, final String text) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            writer.write(text);
        } catch (Exception e) {
            setError(e);
        }
    }

    static public String[] splitStringIntoParts(String s) {
        return s.split("\\s+");
    }

    static public String[] splitStringIntoLines(String s) {
        return s.split("\\r?\\n");
    }

    static public String fit(String s, int size) {
        String result = "";
        int sSize = s.length();
        if (sSize == size) {
            return s;
        }
        if (size < sSize) {
            return s.substring(0, size);
        }
        result = s;
        String addon = "";
        int num = size-sSize;
        for (int i = 0; i < num; i++) {
            addon += " ";
        }
        return result + addon;
    }

    static public int getLongestStringSize(Collection<String> array) {
        int maxSize = -1;
        for (String string : array) {
            int length = string.length();
            if (length > maxSize) {
                maxSize = length;
            }
        }

        return maxSize;
    }

    static public int getLongestStringSizeGeneric(Collection<?> array, Function<Object, Integer> getLength) {
        int maxSize = -1;
        for (Object object : array) {
            int length = getLength.apply(object);
            if (length > maxSize) {
                maxSize = length;
            }
        }

        return maxSize;
    }

    static public String getEmptySpaces(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result += " ";
        }
        return result;
    }

    static public String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}