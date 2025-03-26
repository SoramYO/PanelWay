package com.minhtnn.panelway.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.models.RentalLocation;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RentalLocationAdapter extends ListAdapter<RentalLocation, RentalLocationAdapter.ViewHolder> {

    private final OnLocationActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnLocationActionListener {
        void onEditClick(RentalLocation location);
        void onDeleteClick(RentalLocation location);
        void onStatusClick(RentalLocation location);
    }

    public RentalLocationAdapter(OnLocationActionListener listener) {
        super(new DiffUtil.ItemCallback<RentalLocation>() {
            @Override
            public boolean areItemsTheSame(@NonNull RentalLocation oldItem, @NonNull RentalLocation newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull RentalLocation oldItem, @NonNull RentalLocation newItem) {
                return oldItem.getCode().equals(newItem.getCode())
                        && oldItem.getStatus().equals(newItem.getStatus())
                        && oldItem.getPrice() == newItem.getPrice();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rental_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalLocation location = getItem(position);

        holder.bindData(location);

        holder.editButton.setOnClickListener(v -> listener.onEditClick(location));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(location));
        holder.statusButton.setOnClickListener(v -> listener.onStatusClick(location));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView codeText;
        final TextView addressText;
        final TextView priceText;
        final TextView panelSizeText;
        final TextView statusText;
        final TextView dateText;
        final MaterialButton editButton;
        final MaterialButton deleteButton;
        final MaterialButton statusButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.codeText);
            addressText = itemView.findViewById(R.id.addressText);
            priceText = itemView.findViewById(R.id.priceText);
            panelSizeText = itemView.findViewById(R.id.panelSizeText);
            statusText = itemView.findViewById(R.id.statusText);
            dateText = itemView.findViewById(R.id.dateText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            statusButton = itemView.findViewById(R.id.statusButton);
        }

        void bindData(RentalLocation location) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            codeText.setText(location.getCode());
            addressText.setText(location.getAddress());
            priceText.setText(String.format("%,.0f₫", location.getPrice()));
            panelSizeText.setText(location.getPanelSize());
            statusText.setText(location.getStatus());

            if (location.getAvailableDate() != null) {
                dateText.setText(dateFormat.format(location.getAvailableDate()));
            }

            // Set status color based on status
            int statusColor;
            switch (location.getStatus()) {
                case "Available":
                    statusColor = itemView.getContext().getColor(R.color.colorSuccess);
                    statusButton.setText("Occupied");
                    break;
                case "Occupied":
                    statusColor = itemView.getContext().getColor(R.color.colorWarning);
                    statusButton.setText("Available");
                    break;
                case "Pending":
                    statusColor = itemView.getContext().getColor(R.color.colorPrimary);
                    statusButton.setText("Approve");
                    break;
                default:
                    statusColor = itemView.getContext().getColor(R.color.colorError);
                    statusButton.setText("Change Status");
            }
            statusText.setTextColor(statusColor);
        }
    }
}