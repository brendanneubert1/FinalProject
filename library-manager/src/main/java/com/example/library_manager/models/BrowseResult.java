package com.example.library_manager.models;
import java.util.List;



public class BrowseResult {
    private final List<ExpandedBook> books;
    private final int page;
    private final int size;
    private final int totalPages;
    private final boolean hasPrev;
    private final boolean hasNext;
    private final int prevPage;
    private final int nextPage;
    private final List<PageLink> pages;



 public BrowseResult(
            List<ExpandedBook> books,
            int page,
            int size,
            int totalPages,
            boolean hasPrev,
            boolean hasNext,
            int prevPage,
            int nextPage,
            List<PageLink> pages
    ) {
        this.books = books;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.prevPage = prevPage;
        this.nextPage = nextPage;
        this.pages = pages;
    }


    public List<ExpandedBook> getBooks() { return books; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotalPages() { return totalPages; }
    public boolean isHasPrev() { return hasPrev; }
    public boolean isHasNext() { return hasNext; }
    public int getPrevPage() { return prevPage; }
    public int getNextPage() { return nextPage; }
    public List<PageLink> getPages() { return pages; }


}
