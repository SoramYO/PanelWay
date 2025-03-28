package com.minhtnn.panelway.ui.advertisement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.minhtnn.panelway.R;
import com.minhtnn.panelway.api.AppointmentRepository;
import com.minhtnn.panelway.databinding.FragmentBookAdBinding;
import com.minhtnn.panelway.models.Appointment;
import com.minhtnn.panelway.models.request.CreateAppointmentRequest;
import io.reactivex.rxjava3.disposables.Disposable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BookAdFragment extends Fragment {

    private FragmentBookAdBinding binding;
    private String selectedDate = "";
    private String selectedFromTime = "";
    private String selectedToTime = "";
    private String adContent = "";
    private AppointmentRepository appointmentRepository;
    private Disposable disposable; // Để quản lý RxJava subscription

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookAdBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo AppointmentRepository
        appointmentRepository = new AppointmentRepository(requireContext());

        // Lấy thông tin từ Bundle (nhưng không hiển thị lên UI)
        Bundle args = getArguments();

        // Thiết lập danh sách ngày
        setupDateOptions();

        // Thiết lập danh sách thời gian
        setupTimeOptions();

        // Thiết lập Spinner
        binding.adContentSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                adContent = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                adContent = "";
            }
        });

        // Xử lý nút "Đặt lịch"
        binding.nextButton.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || selectedFromTime.isEmpty() || selectedToTime.isEmpty() || adContent.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng CreateAppointmentRequest để gửi lên API
            CreateAppointmentRequest request = createAppointmentRequest(args);
            if (request != null) {
                // Gọi API thông qua AppointmentRepository
                callCreateAppointmentApi(request);
            } else {
                Toast.makeText(requireContext(), "Không thể tạo lịch hẹn do thiếu thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CreateAppointmentRequest createAppointmentRequest(Bundle args) {
        try {
            // Tạo mã ngẫu nhiên (có thể thay bằng logic của bạn)
            String code = String.valueOf(System.currentTimeMillis());

            // Đảm bảo giờ có định dạng 2 chữ số (HH:mm)
            String formattedFromTime = selectedFromTime.length() == 4 ? "0" + selectedFromTime : selectedFromTime; // Chuyển "9:00" thành "09:00"

            // Kiểm tra và sửa selectedDate nếu cần
            String fullDateStr;
            if (!selectedDate.contains("/")) { // Nếu selectedDate chỉ là ngày (ví dụ: "28")
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                String monthYear = monthYearFormat.format(calendar.getTime()); // Lấy tháng và năm hiện tại
                fullDateStr = selectedDate + "/" + monthYear; // Ví dụ: "28/03/2025"
            } else {
                fullDateStr = selectedDate; // Đã có định dạng đầy đủ (ví dụ: "28/03/2025")
            }

            // Chuyển đổi ngày và giờ thành định dạng Date
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String bookingDateStr = fullDateStr + " " + formattedFromTime; // Ví dụ: "28/03/2025 09:00"
            Date bookingDate = inputDateFormat.parse(bookingDateStr);

            // Chuyển đổi thời gian sang múi giờ UTC
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Đặt múi giờ là UTC
            String utcBookingDateStr = utcFormat.format(bookingDate);
            Date utcBookingDate = utcFormat.parse(utcBookingDateStr); // Parse lại để đảm bảo thời gian là UTC

            // Lấy thông tin từ Bundle
            String place = args != null ? args.getString("diaChi", "") : "";
            String rentalLocationId = args != null ? args.getString("maQuangCao", "") : "";
            String adContentId = String.valueOf(System.currentTimeMillis()); // Chỉ lấy id từ System.currentTimeMillis()

            // Tạo đối tượng CreateAppointmentRequest
            CreateAppointmentRequest request = new CreateAppointmentRequest();
            request.setCode(code);
            request.setBookingDate(utcBookingDate); // Đặt bookingDate là thời gian UTC
            request.setPlace(place);
            request.setPriority(0); // Priority mặc định là 0
            request.setAdContentId(adContentId);
            request.setRentalLocationId(rentalLocationId);

            return request;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void callCreateAppointmentApi(CreateAppointmentRequest request) {
        // Gọi API thông qua AppointmentRepository
        disposable = appointmentRepository.createAppointment(request)
                .subscribe(
                        appointment -> {
                            // Thành công
                            Toast.makeText(requireContext(), "Đã đặt lịch thành công!\nNgày: " + selectedDate + "\nTừ: " + selectedFromTime + "\nĐến: " + selectedToTime + "\nNội dung: " + adContent, Toast.LENGTH_LONG).show();
                            // Quay lại màn hình trước
                            requireActivity().onBackPressed();
                        },
                        throwable -> {
                            // Thất bại
                            Toast.makeText(requireContext(), "Đặt lịch thất bại: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                );
    }

    private void setupDateOptions() {
        List<String> days = new ArrayList<>();
        List<String> dayNames = new ArrayList<>();
        List<String> fullDates = new ArrayList<>(); // Lưu ngày đầy đủ (dd/MM/yyyy)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        // Tạo 5 ngày tiếp theo
        for (int i = 0; i < 5; i++) {
            days.add(dayFormat.format(calendar.getTime()));
            fullDates.add(fullDateFormat.format(calendar.getTime())); // Lưu ngày đầy đủ
            dayNames.add(dayNameFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        LinearLayout dateContainer = binding.dateContainer;
        for (int i = 0; i < days.size(); i++) {
            View dateView = LayoutInflater.from(requireContext()).inflate(R.layout.item_date, dateContainer, false);
            TextView dayNameText = dateView.findViewById(R.id.dayNameText);
            TextView dayText = dateView.findViewById(R.id.dayText);

            dayNameText.setText(dayNames.get(i));
            dayText.setText(days.get(i));

            int finalI = i;
            dateView.setOnClickListener(v -> {
                selectedDate = fullDates.get(finalI); // Lưu ngày đầy đủ (dd/MM/yyyy)
                Log.d("BookAdFragment", "Selected Date: " + selectedDate); // Thêm log để kiểm tra
                // Reset trạng thái các view khác
                for (int j = 0; j < dateContainer.getChildCount(); j++) {
                    dateContainer.getChildAt(j).setBackgroundResource(R.drawable.bg_unselected);
                }
                // Đặt trạng thái được chọn
                dateView.setBackgroundResource(R.drawable.bg_selected);
            });

            dateContainer.addView(dateView);
        }
    }

    private void setupTimeOptions() {
        List<String> times = new ArrayList<>();
        // Tạo các ca từ 9:00 đến 16:00, mỗi ca 1h30
        times.add("9:00");
        times.add("10:30");
        times.add("12:00");
        times.add("13:30");
        times.add("15:00");

        // From time
        LinearLayout fromTimeContainer = binding.fromTimeContainer;
        for (int i = 0; i < times.size(); i++) {
            View timeView = LayoutInflater.from(requireContext()).inflate(R.layout.item_time, fromTimeContainer, false);
            TextView timeText = timeView.findViewById(R.id.timeText);
            timeText.setText(times.get(i));

            int finalI = i;
            timeView.setOnClickListener(v -> {
                selectedFromTime = times.get(finalI);
                // Cập nhật danh sách "Thời gian kết thúc" dựa trên "Thời gian bắt đầu"
                updateToTimeOptions(times, finalI);
                for (int j = 0; j < fromTimeContainer.getChildCount(); j++) {
                    fromTimeContainer.getChildAt(j).setBackgroundResource(R.drawable.bg_unselected);
                }
                timeView.setBackgroundResource(R.drawable.bg_selected);
            });

            fromTimeContainer.addView(timeView);
        }

        // To time (ban đầu để trống, sẽ cập nhật sau khi chọn "Thời gian bắt đầu")
        updateToTimeOptions(times, -1);
    }

    private void updateToTimeOptions(List<String> times, int selectedFromIndex) {
        LinearLayout toTimeContainer = binding.toTimeContainer;
        toTimeContainer.removeAllViews();

        // Nếu chưa chọn "Thời gian bắt đầu", hiển thị tất cả các ca
        int startIndex = selectedFromIndex + 1; // Bắt đầu từ ca tiếp theo
        if (selectedFromIndex == -1) {
            startIndex = 0; // Hiển thị tất cả nếu chưa chọn
        }

        for (int i = startIndex; i < times.size(); i++) {
            View timeView = LayoutInflater.from(requireContext()).inflate(R.layout.item_time, toTimeContainer, false);
            TextView timeText = timeView.findViewById(R.id.timeText);
            // Tính thời gian kết thúc (thêm 1h30 vào thời gian bắt đầu)
            String endTime = calculateEndTime(times.get(i));
            timeText.setText(endTime);

            int finalI = i;
            timeView.setOnClickListener(v -> {
                selectedToTime = times.get(finalI);
                for (int j = 0; j < toTimeContainer.getChildCount(); j++) {
                    toTimeContainer.getChildAt(j).setBackgroundResource(R.drawable.bg_unselected);
                }
                timeView.setBackgroundResource(R.drawable.bg_selected);
            });

            toTimeContainer.addView(timeView);
        }
    }

    private String calculateEndTime(String startTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(startTime));
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.add(Calendar.MINUTE, 30);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return startTime; // Trả về thời gian gốc nếu có lỗi
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy subscription để tránh memory leak
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        binding = null;
    }
}