package org.jaoed.config.logger;

import org.jaoed.config.Logger;

public class File extends Logger {
    private String fileName;

    public File() {}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        String out = "Logger<File>[" + super.getName() + "]:\n" + " -> file = " + fileName + "\n";
        return out;
    }
}
