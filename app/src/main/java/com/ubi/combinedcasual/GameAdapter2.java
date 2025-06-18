package com.ubi.combinedcasual;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubigamebase.R;

import java.util.List;
public class GameAdapter2 extends RecyclerView.Adapter<GameAdapter2.GameViewHolder2> {
    private List<GameItem> gameList;
    private Context context;
    private OnGameClickListener2 listener; // Interface để xử lý click
    public interface OnGameClickListener2 {
        void onGameClick(String gameFolder);
    }
    public GameAdapter2(List<GameItem> gameList, Context context, OnGameClickListener2 listener) {
        this.gameList = gameList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(GameAdapter2.GameViewHolder2 holder, int position) {
        GameItem gameItem = gameList.get(position);
        holder.tvGameName.setText(gameItem.getName());

        if (gameItem.getIconBitmap() != null) {
            holder.imgGameIcon.setImageBitmap(gameItem.getIconBitmap());
        } else {
            holder.imgGameIcon.setImageResource(R.drawable.app_icon); // Icon mặc định nếu cần
        }

        // Thiết lập sự kiện click cho CardView
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(gameItem.getFolderName()); // Gọi callback với gameItem được nhấp
            }
        });
    }
    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder2 extends RecyclerView.ViewHolder {
        public ImageView imgGameIcon;
        public TextView tvGameName;
        public CardView cardView;

        public GameViewHolder2(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imgGameIcon = itemView.findViewById(R.id.imgGameIcon);
            tvGameName = itemView.findViewById(R.id.tvGameName);
        }
    }
}