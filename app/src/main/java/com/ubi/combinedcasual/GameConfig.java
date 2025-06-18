package com.ubi.combinedcasual;

public class GameConfig {
    public String gameName;
    public GameConfig(String gameName, int gameOriontation, int gameShowBanner) {
        this.gameName = gameName;
        this.gameOriontation = gameOriontation;
        this.gameShowBanner = gameShowBanner;
    }
    public int gameOriontation; // -1 is landscape, 1 is portrait
    public int gameShowBanner; // 0 is off, 1 is on
    public GameConfig(){
    }
}
