package com.minhtnn.panelway.adapters;


import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.models.RentalLocation;



public class AdvertisementCardAdapter extends ListAdapter<RentalLocation, AdvertisementCardAdapter.AdvertisementCardViewHolder> {
    private final OnAdvertisementClickListener clickListener;

    public interface OnAdvertisementClickListener {
        void onAdvertisementClick(RentalLocation advertisementCard);
    }

    private static final DiffUtil.ItemCallback<RentalLocation> DIFF_CALLBACK = new DiffUtil.ItemCallback<RentalLocation>() {
        @Override
        public boolean areItemsTheSame(@NonNull RentalLocation oldItem, @NonNull RentalLocation newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RentalLocation oldItem, @NonNull RentalLocation newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AdvertisementCardAdapter(OnAdvertisementClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdvertisementCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.advertisement_card, parent, false);
        return new AdvertisementCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertisementCardViewHolder holder, int position) {
        RentalLocation advertisementCard = getItem(position);
        holder.bind(advertisementCard, clickListener);
    }

    static class AdvertisementCardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView advertisementImage;
        private final ProgressBar loadingProgressBar;
        private final TextView titleTextView;
        private final TextView locationTextView;
        private final TextView detailsTextView;

        AdvertisementCardViewHolder(@NonNull View itemView) {
            super(itemView);
            advertisementImage = itemView.findViewById(R.id.advertisement_image);
            loadingProgressBar = itemView.findViewById(R.id.loading_progress_bar);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            locationTextView = itemView.findViewById(R.id.location_text_view);
            detailsTextView = itemView.findViewById(R.id.details_text_view);
        }

        void bind(RentalLocation rentalLocation, OnAdvertisementClickListener clickListener) {
            // Set loading progress bar
            loadingProgressBar.setVisibility(View.VISIBLE);

            // Load image using Glide (make sure to add Glide dependency)
            Glide.with(itemView.getContext())
                    .load((rentalLocation.getRentalLocationImageList() != null)
                            ? rentalLocation.getRentalLocationImageList().get(0).getUrlImage()
                            : "")
                    .error(R.drawable.image_not_found) // Add this line to specify fallback image
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loadingProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadingProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(advertisementImage);

            // Set text fields
            titleTextView.setText(rentalLocation.getCode());
            locationTextView.setText(rentalLocation.getAddress());
            detailsTextView.setText(rentalLocation.getDescription());

            // Set click listener
            itemView.setOnClickListener(v -> clickListener.onAdvertisementClick(rentalLocation));
        }
    }
}