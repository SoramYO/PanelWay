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
import com.minhtnn.panelway.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotificationsAdapter extends ListAdapter<Notification, NotificationsAdapter.NotificationViewHolder> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    private final OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationsAdapter(OnNotificationClickListener clickListener) {
        super(new DiffUtil.ItemCallback<Notification>() {
            @Override
            public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
                return oldItem.isRead() == newItem.isRead() &&
                        oldItem.getMessage().equals(newItem.getMessage());
            }
        });
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = getItem(position);
        holder.bind(notification, clickListener);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView messageText;
        TextView dateText;
        View unreadIndicator;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }

        void bind(Notification notification, OnNotificationClickListener listener) {
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            dateText.setText(dateFormat.format(notification.getCreatedAt()));
            unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}