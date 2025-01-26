package com.example.assetmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Asset;
import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.BeanHolder> {
    private final List<Asset> list;
    private final LayoutInflater layoutInflater;
    private final OnAssetItemClick onAssetItemClick;
    private final OnAssetActionListener onAssetActionListener;

    public AssetsAdapter(List<Asset> list, Context context, OnAssetItemClick listener, OnAssetActionListener onAssetActionListener) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.onAssetItemClick = listener;
        this.onAssetActionListener = onAssetActionListener;
    }

    @NonNull
    @Override
    public BeanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_asset, parent, false);
        return new BeanHolder(view);
    }

    @Override
    public void onBindViewHolder(BeanHolder holder, int position) {
        Asset asset = list.get(position);
        holder.tvAssetName.setText(asset.getName());
        holder.itemView.setOnClickListener(v -> onAssetItemClick.onAssetClick(asset));

        holder.ivUpdate.setOnClickListener(v -> onAssetActionListener.onAssetUpdate(asset));
        holder.ivDelete.setOnClickListener(v -> onAssetActionListener.onAssetDelete(asset));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class BeanHolder extends RecyclerView.ViewHolder {

        TextView tvAssetName;
        ImageView ivUpdate;
        ImageView ivDelete;

        public BeanHolder(View itemView) {
            super(itemView);
            tvAssetName = itemView.findViewById(R.id.tvAssetName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
        }


    }

    public interface OnAssetItemClick {
        void onAssetClick(Asset asset);
    }

    public interface OnAssetActionListener {
        void onAssetUpdate(Asset asset);
        void onAssetDelete(Asset asset);
    }
}
