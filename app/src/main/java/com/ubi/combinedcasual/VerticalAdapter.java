package com.ubi.combinedcasual;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubigamebase.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerticalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HORIZONTAL = 0;
    private static final int TYPE_GRID = 1;

    private List<Section> originalSections;
    private List<Section> filteredSections;
    private Context context;
    private GameAdapter2.OnGameClickListener2 gameClickListener;
    private Map<String, Boolean> sectionVisibility;

    public VerticalAdapter(Context context, List<Section> sections, GameAdapter2.OnGameClickListener2 gameClickListener, Map<String, Boolean> sectionVisibility) {
        this.context = context;
        this.originalSections = new ArrayList<>(sections);
        this.gameClickListener = gameClickListener;
        this.sectionVisibility = sectionVisibility;
        filterSections();
    }

    private void filterSections() {
        filteredSections = new ArrayList<>();
        for (Section section : originalSections) {
            if (sectionVisibility.get(section.getTitle())) {
                filteredSections.add(section);
            }
        }
    }

    public void updateFilter(Map<String, Boolean> newVisibility) {
        this.sectionVisibility = newVisibility;
        filterSections();
        notifyDataSetChanged(); // Đảm bảo cập nhật giao diện
    }

    public void updateSections(List<Section> newSections) {
        this.originalSections = new ArrayList<>(newSections); // Cập nhật danh sách gốc
        filterSections(); // Lọc lại sections dựa trên sectionVisibility
        notifyDataSetChanged(); // Đảm bảo làm mới giao diện
    }

    @Override
    public int getItemViewType(int position) {
        return filteredSections.get(position).isGrid() ? TYPE_GRID : TYPE_HORIZONTAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HORIZONTAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
            SectionViewHolder holder = new SectionViewHolder(view);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(holder.recyclerViewHorizontal);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_grid, parent, false);
            return new GridViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Section section = filteredSections.get(position);
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder horizontalHolder = (SectionViewHolder) holder;
            horizontalHolder.tvSectionTitle.setText(section.getTitle());

            GameAdapter2 horizontalAdapter = new GameAdapter2(section.getGameItems(), context, gameClickListener);
            horizontalHolder.recyclerViewHorizontal.setAdapter(horizontalAdapter);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            horizontalHolder.recyclerViewHorizontal.setLayoutManager(layoutManager);

            // Kiểm tra và xóa ItemDecoration cũ (nếu có) trước khi thêm mới
            if (horizontalHolder.recyclerViewHorizontal.getItemDecorationCount() > 0) {
                horizontalHolder.recyclerViewHorizontal.removeItemDecorationAt(0); // Xóa decoration cũ nếu tồn tại
            }
            horizontalHolder.recyclerViewHorizontal.addItemDecoration(new HorizontalSpaceItemDecoration(2));

            // Đảm bảo kích thước item và padding không gây ra khoảng trống dư thừa
            int itemWidth = 120; // Kích thước cố định của item trong item_game.xml
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int visibleItems = Math.min(4, section.getGameItems().size()); // Số item hiển thị tối đa là 4
            int totalWidth = itemWidth * visibleItems;

            int paddingRight = Math.max(0, screenWidth - totalWidth); // Đảm bảo paddingRight không âm
            horizontalHolder.recyclerViewHorizontal.setPadding(0, 0, paddingRight, 0);
            horizontalHolder.recyclerViewHorizontal.setClipToPadding(false);

            horizontalHolder.recyclerViewHorizontal.scrollToPosition(0);
        } else if (holder instanceof GridViewHolder) {
            GridViewHolder gridHolder = (GridViewHolder) holder;
            gridHolder.tvSectionTitle.setText(section.getTitle());

            GridAdapter gridAdapter = new GridAdapter(context, section.getGameItems(), gameClickListener);
            gridHolder.gridViewSection.setAdapter(gridAdapter);

            // Tính toán chiều cao động cho GridView
            int numColumns = 3;
            int itemCount = section.getGameItems().size();
            int rows = (int) Math.ceil((double) itemCount / numColumns);
            int itemHeight = 160; // Chiều cao item từ item_game.xml (160dp)
            int verticalSpacing = 2; // Khoảng cách dọc giữa các item trong GridView
            int extraPadding = 10; // Thêm padding trên/dưới để tránh bị che
            int totalHeight = (rows * itemHeight) + ((rows - 1) * verticalSpacing) + (extraPadding * 2);

            // Chuyển dp sang px
            float scale = context.getResources().getDisplayMetrics().density;
            totalHeight = (int) (totalHeight * scale);

            ViewGroup.LayoutParams params = gridHolder.gridViewSection.getLayoutParams();
            params.height = totalHeight;
            gridHolder.gridViewSection.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return filteredSections.size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle;
        RecyclerView recyclerViewHorizontal;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            recyclerViewHorizontal = itemView.findViewById(R.id.recyclerViewHorizontal);
        }
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionTitle;
        GridView gridViewSection;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
            gridViewSection = itemView.findViewById(R.id.gridViewSection);
        }
    }

    private class GridAdapter extends ArrayAdapter<GameItem> {
        private List<GameItem> gameItems;
        private GameAdapter2.OnGameClickListener2 gameClickListener;

        public GridAdapter(Context context, List<GameItem> gameItems, GameAdapter2.OnGameClickListener2 gameClickListener) {
            super(context, 0, gameItems);
            this.gameItems = gameItems;
            this.gameClickListener = gameClickListener;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_game, parent, false);
                holder = new ViewHolder();
                holder.imgGameIcon = convertView.findViewById(R.id.imgGameIcon);
                holder.tvGameName = convertView.findViewById(R.id.tvGameName);
                holder.cardView = convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GameItem gameItem = gameItems.get(position);
            holder.tvGameName.setText(gameItem.getName());
            if (gameItem.getIconBitmap() != null) {
                holder.imgGameIcon.setImageBitmap(gameItem.getIconBitmap());
            } else {
                holder.imgGameIcon.setImageResource(R.drawable.app_icon);
            }

            holder.cardView.setOnClickListener(v -> {
                if (gameClickListener != null) {
                    gameClickListener.onGameClick(gameItem.getFolderName());
                }
            });

            return convertView;
        }

        private class ViewHolder {
            ImageView imgGameIcon;
            TextView tvGameName;
            View cardView;
        }
    }

    private static class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public HorizontalSpaceItemDecoration(int spaceDp) {
            this.space = (int) (spaceDp * Resources.getSystem().getDisplayMetrics().density);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0;
            } else {
                outRect.left = 0;
            }
        }
    }
}