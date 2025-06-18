package com.ubi.combinedcasual;
import java.util.List;

public class Section {
    private String title; // Tiêu đề của danh sách (Featured, Recently Played, Puzzle)
    private List<GameItem> gameItems; // Danh sách các game item trong section này
    private boolean isGrid; // true: section là lưới, false: section là ngang

    public Section(String title, List<GameItem> gameItems, boolean isGrid) {
        this.title = title;
        this.gameItems = gameItems;
        this.isGrid = isGrid;
    }

    public String getTitle() {
        return title;
    }

    public List<GameItem> getGameItems() {
        return gameItems;
    }

    public boolean isGrid() {
        return isGrid;
    }
}