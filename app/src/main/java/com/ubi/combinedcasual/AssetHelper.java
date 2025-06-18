package com.ubi.combinedcasual;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;

public class AssetHelper {
    public static String[] listSolitaireGames(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(""); // Lấy danh sách tất cả thư mục/tệp trong assets
            if (files == null) return new String[0];

            return java.util.Arrays.stream(files)
                    .filter(name -> name.startsWith("1_1_"))
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static String[] listAllGames(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(""); // Lấy danh sách tất cả thư mục/tệp trong assets
            if (files == null) return new String[0];

            return java.util.Arrays.stream(files)
                    .filter(name -> name.startsWith("1_"))
                    .toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }
}