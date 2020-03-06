package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Support {
    private String errorMessage = "";
    private boolean wasError = false;

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean wasError() {
        return wasError;
    }

    void clearError() {
        wasError = false;
        errorMessage = "";
    }

    void setError(final Exception e) {
        wasError = true;
        errorMessage = e.getMessage();
    }

    public String readTextFile(final String file) {
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

    public void writeTextFile(final String file, final String text) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            writer.write(text);
        } catch (Exception e) {
            setError(e);
        }
    }

    public String[] splitStringIntoParts(String s) {
        return s.split("\\s+");
    }

    public String[] splitStringIntoLines(String s) {
        return s.split("\\r?\\n");
    }
}