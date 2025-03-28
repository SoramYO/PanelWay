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
import com.minhtnn.panelway.databinding.ItemRentalLocationBinding; // Sử dụng binding
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
        // Sử dụng binding thay vì inflate thủ công
        ItemRentalLocationBinding binding = ItemRentalLocationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalLocation location = getItem(position);
        holder.bindData(location);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRentalLocationBinding binding;

        ViewHolder(@NonNull ItemRentalLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Gắn sự kiện cho nút "Book Ad"
            binding.bookAdButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusClick(getItem(getAdapterPosition()));
                }
            });
        }

        void bindData(RentalLocation location) {
            binding.codeText.setText(location.getCode());
            binding.addressText.setText(location.getAddress());
            binding.priceText.setText(String.format("%,.0f₫", location.getPrice()));
            binding.panelSizeText.setText(location.getPanelSize());
            if (location.getAvailableDate() != null) {
                binding.dateText.setText(dateFormat.format(location.getAvailableDate()));
            } else {
                binding.dateText.setText("");
            }

            // Tùy chỉnh nút "Book Ad" dựa trên trạng thái
            if ("Available".equals(location.getStatus())) {
                binding.bookAdButton.setText("Đặt Quảng Cáo");
                binding.bookAdButton.setEnabled(true);
            } else {
                binding.bookAdButton.setText("Đã Đặt");
                binding.bookAdButton.setEnabled(false); // Vô hiệu hóa nếu không khả dụng
            }
        }
    }
}