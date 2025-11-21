package com.example.library_manager.models;

public class PageLink {
    private final int number;
    private final boolean current;

    public PageLink(int number, boolean current) {
        this.number = number;
        this.current=current;
    }
    public int getNumber() { return number; }
    public boolean isCurrent() { return current; }
}
