package com.example.assetmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetmanager.R;
import com.example.assetmanager.data.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder> {
    private final List<Location> locationList;
    private final OnLocationClickListener onLocationClickListener;
    private final OnLocationActionListener onLocationActionListener;

    public LocationAdapter(List<Location> locationList, OnLocationClickListener onLocationClickListener, OnLocationActionListener onLocationActionListener) {
        this.locationList = locationList;
        this.onLocationClickListener = onLocationClickListener;
        this.onLocationActionListener = onLocationActionListener;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        Location location = locationList.get(position);
        holder.bind(location);

        // Set click listeners for update and delete actions
        holder.ivUpdate.setOnClickListener(v -> onLocationActionListener.onLocationUpdate(location));
        holder.ivDelete.setOnClickListener(v -> onLocationActionListener.onLocationDelete(location));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public interface OnLocationClickListener {
        void onLocationClicked(Location location);
    }

    public interface OnLocationActionListener {
        void onLocationUpdate(Location location);
        void onLocationDelete(Location location);
    }

    public class LocationHolder extends RecyclerView.ViewHolder {
        private final TextView tvLocationName;
        private final TextView tvCoordinates;
        ImageView ivUpdate;
        ImageView ivDelete;

        public LocationHolder(View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvCoordinates = itemView.findViewById(R.id.tvCoordinates);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(Location location) {
            tvLocationName.setText(location.getName());
            tvCoordinates.setText(String.format("(%s, %s)", location.getLatitude(), location.getLongitude()));

            itemView.setOnClickListener(v -> onLocationClickListener.onLocationClicked(location));
        }
    }
}
