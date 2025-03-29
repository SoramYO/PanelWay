package com.minhtnn.panelway.ui.advertisement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.databinding.FragmentAdDetailsBinding;
import com.minhtnn.panelway.models.RentalLocation;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdDetailsFragment extends Fragment {
    private FragmentAdDetailsBinding binding;
    private AdDetailsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdDetailsFragmentArgs args = AdDetailsFragmentArgs.fromBundle(getArguments());
        String adId = args.getAdId();

        viewModel = new ViewModelProvider(this).get(AdDetailsViewModel.class);
        viewModel.loadAdDetails(adId);

        setupViews();
        observeData();
    }

    private void setupViews() {
        // Nút đặt thuê
        binding.bookNowButton.setOnClickListener(v -> {
            RentalLocation location = viewModel.getRentalLocation().getValue();
            if (location != null && location.getId() != null && !location.getId().isEmpty()) {
                // Tạo Bundle để truyền thông tin đến BookAdFragment
                Bundle bundle = new Bundle();
                bundle.putString("maQuangCao", location.getId());
                bundle.putString("diaChi", location.getAddress());
                bundle.putString("kichThuoc", location.getPanelSize());
                bundle.putString("gia", String.valueOf(location.getPrice()));
                bundle.putString("ngayKhaDung", location.getAvailableDate() != null ? location.getAvailableDate().toString() : "");

                // Điều hướng đến BookAdFragment
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.action_adDetailsFragment_to_bookAdFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Invalid rental location ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeData() {
        viewModel.getRentalLocation().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(RentalLocation location) {
        if (location == null) return;

        binding.rentalCodeText.setText(location.getCode() != null ? location.getCode() : "N/A");
        binding.rentalLocationText.setText(location.getAddress() != null ? location.getAddress() : "No address");

        double price = location.getPrice();
        if (price > 0) {
            // Sử dụng Locale của Việt Nam
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            // Định dạng giá và loại bỏ ký hiệu mặc định (nếu cần), sau đó thêm "₫"
            String formattedPrice = currencyFormat.format(price).replace("₫", "").trim() + " ₫";
            binding.rentalPriceText.setText(formattedPrice);
        } else {
            binding.rentalPriceText.setText("N/A");
        }
        binding.rentalSizeText.setText(location.getPanelSize() != null ? location.getPanelSize() : "Unknown size");

        // Hiển thị ngày có sẵn
        if (location.getAvailableDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.rentalAvailableDateText.setText(dateFormat.format(location.getAvailableDate()));
        } else {
            binding.rentalAvailableDateText.setText("Not specified");
        }

        binding.rentalDescriptionText.setText(location.getDescription() != null ? location.getDescription() : "No description available");

        // Load ảnh vào ImageView với placeholder và xử lý lỗi
        if (location.getRentalLocationImageList() != null && !location.getRentalLocationImageList().isEmpty()) {
            Glide.with(this)
                    .load(location.getRentalLocationImageList().get(0))
                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.image_placeholder)  // Placeholder khi ảnh chưa tải
//                            .error(R.drawable.image_error)  // Ảnh lỗi khi không tải được
                            .centerCrop())
                    .into(binding.rentalImageView);
        } else {
//            binding.rentalImageView.setImageResource(R.drawable.image_placeholder);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
