package org.jaoed.target;

public interface ConfigArea {
    public void setConfig(byte[] bytes);
    public boolean isEmpty();
    public boolean isCompleteMatch(byte[] bytes);
    public boolean isPrefixMatch(byte[] prefix);
}
