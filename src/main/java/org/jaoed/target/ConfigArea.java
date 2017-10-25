package org.jaoed.target;

public interface ConfigArea {
    public static final int MAX_LENGTH = 1024;

    public void setConfig(byte[] bytes, int length);
    public byte[] getConfig();
    public boolean isEmpty();
    public boolean isCompleteMatch(byte[] bytes, int length);
    public boolean isPrefixMatch(byte[] prefix, int length);
}
