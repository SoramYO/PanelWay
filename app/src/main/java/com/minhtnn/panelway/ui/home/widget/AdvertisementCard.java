package com.minhtnn.panelway.ui.home.widget;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.minhtnn.panelway.R;
import com.minhtnn.panelway.ui.advertisement.AdDetailsFragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdvertisementCard extends LinearLayout {
    private String rentalLocationId;
    private String imageUrl;
    private String title;
    private String location;
    private String price;
    private String minDuration;
    private String traffic;
    private String type;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView locationTextView;
    private TextView detailsTextView;
    private CardView detailsCard;
    private ProgressBar loadingProgressBar;

    public AdvertisementCard(Context context) {
        super(context);
        initializeViews(context);
    }

    public AdvertisementCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.advertisement_card, this, true);

        imageView = view.findViewById(R.id.advertisement_image);
        titleTextView = view.findViewById(R.id.title_text_view);
        locationTextView = view.findViewById(R.id.location_text_view);
        detailsTextView = view.findViewById(R.id.details_text_view);
        detailsCard = view.findViewById(R.id.details_card);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);

        detailsCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdDetailsFragment.class);
            intent.putExtra("RENTAL_LOCATION_ID", rentalLocationId);
            context.startActivity(intent);
        });
    }

    public void setData(String rentalLocationId, String imageUrl, String title,
                        String location, String price, String minDuration,
                        String traffic, String type) {
        this.rentalLocationId = rentalLocationId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.location = location;
        this.price = price;
        this.minDuration = minDuration;
        this.traffic = traffic;
        this.type = type;

        updateViews();
        validateAndLoadImage();
    }

    private void updateViews() {
        titleTextView.setText(title);
        locationTextView.setText(location);

        String details = String.format("From %s$/month\n%s years min\n%s\n%s",
                price, minDuration, traffic, type);
        detailsTextView.setText(details);
    }

    private void validateAndLoadImage() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            setPlaceholderImage();
            return;
        }

        new ImageValidationTask().execute(imageUrl);
    }

    private void setPlaceholderImage() {
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image_not_found));
    }

    private class ImageValidationTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            loadingProgressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(urls[0]).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000);
                int responseCode = connection.getResponseCode();
                return (responseCode >= 200 && responseCode < 300);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            loadingProgressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            if (isValid) {
                // You would typically use a library like Glide or Picasso here
                // For this example, I'll use placeholder loading
                new ImageLoadTask().execute(imageUrl);
            } else {
                setPlaceholderImage();
            }
        }
    }

    private class ImageLoadTask extends AsyncTask<String, Void, Drawable> {
        @Override
        protected Drawable doInBackground(String... urls) {
            try {
                return Drawable.createFromStream(new URL(urls[0]).openStream(), "src");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
            } else {
                setPlaceholderImage();
            }
        }
    }
}