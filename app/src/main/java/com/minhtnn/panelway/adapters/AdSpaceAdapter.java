package com.minhtnn.panelway.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.models.AdvertisementSpace;

public class AdSpaceAdapter extends ListAdapter<AdvertisementSpace, AdSpaceAdapter.AdSpaceViewHolder> {
    private static final DiffUtil.ItemCallback<AdvertisementSpace> DIFF_CALLBACK = new DiffUtil.ItemCallback<AdvertisementSpace>() {
        @Override
        public boolean areItemsTheSame(@NonNull AdvertisementSpace oldItem, @NonNull AdvertisementSpace newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AdvertisementSpace oldItem, @NonNull AdvertisementSpace newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final OnAdSpaceClickListener clickListener;

    public interface OnAdSpaceClickListener {
        void onAdSpaceClick(AdvertisementSpace adSpace);
    }

    public AdSpaceAdapter(OnAdSpaceClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdSpaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ad_space_manage, parent, false);
        return new AdSpaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdSpaceViewHolder holder, int position) {
        AdvertisementSpace space = getItem(position);
        holder.bind(space, clickListener);
    }

    static class AdSpaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView statusText;
        private final View editButton;
        private final View deleteButton;

        AdSpaceViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            statusText = itemView.findViewById(R.id.statusText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(AdvertisementSpace space, OnAdSpaceClickListener clickListener) {
            titleText.setText(space.getTitle());
            statusText.setText(space.getStatus());
            editButton.setOnClickListener(v -> clickListener.onAdSpaceClick(space));
            deleteButton.setOnClickListener(v -> clickListener.onAdSpaceClick(space));
        }
    }
}