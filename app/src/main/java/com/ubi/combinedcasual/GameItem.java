package com.ubi.combinedcasual;

import android.graphics.Bitmap;

public class GameItem {
    private String name;
    private String folderName;
    private Bitmap iconBitmap;

    public GameItem(String name, String folderName, Bitmap iconBitmap) {
        this.name = name;
        this.folderName = folderName;
        this.iconBitmap = iconBitmap;
    }

    public String getName() {
        return name;
    }

    public String getFolderName() {
        return folderName;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }
}
