package com.ubi.combinedcasual;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class GameViewHolder extends RecyclerView.ViewHolder {
    TextView gameName, gameCategory;
    ImageView gameIcon;

    public GameViewHolder(View itemView, TextView nameView, TextView categoryView, ImageView iconView) {
        super(itemView);
        this.gameName = nameView;
        this.gameCategory = categoryView;
        this.gameIcon = iconView;
    }
}
