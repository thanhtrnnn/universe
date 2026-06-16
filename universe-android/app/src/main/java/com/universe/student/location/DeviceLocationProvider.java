package com.universe.student.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.Locale;

public final class DeviceLocationProvider {

    public static final float MAX_ACCEPTABLE_ACCURACY_METERS = 50f;

    public interface Callback {
        void onLocation(Location location);
        void onError(String message);
    }

    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationManager locationManager;

    public DeviceLocationProvider(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(Callback callback) {
        getCurrentLocation(MAX_ACCEPTABLE_ACCURACY_METERS, callback);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(float maximumAccuracyMeters, Callback callback) {
        if (Float.isNaN(maximumAccuracyMeters)
                || Float.isInfinite(maximumAccuracyMeters)
                || maximumAccuracyMeters <= 0) {
            callback.onError("Mức sai số GPS yêu cầu không hợp lệ.");
            return;
        }
        if (!isLocationEnabled()) {
            callback.onError("Dịch vụ vị trí đang tắt. Hãy bật GPS/Vị trí rồi thử lại.");
            return;
        }

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        if (hasAccuracyAtMost(location, maximumAccuracyMeters)) {
                            callback.onLocation(location);
                        } else {
                            callback.onError(accuracyError(location, maximumAccuracyMeters));
                        }
                    } else {
                        callback.onError("Không thể lấy được vị trí hiện tại. Thử ở khu vực tín hiệu tốt hơn.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Lỗi khi lấy vị trí: " + e.getMessage());
                });
    }

    private boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private static boolean hasAccuracyAtMost(Location location, float maximum) {
        return location != null
                && location.hasAccuracy()
                && !Float.isNaN(location.getAccuracy())
                && !Float.isInfinite(location.getAccuracy())
                && location.getAccuracy() <= maximum;
    }

    private static String accuracyError(Location location, float maximumAccuracyMeters) {
        if (location == null || !location.hasAccuracy()) {
            return "Không nhận được vị trí GPS. "
                    + "Hãy bật Vị trí chính xác, Wi-Fi và thử ở gần cửa sổ.";
        }
        return String.format(
                Locale.US,
                "GPS hiện chỉ đạt sai số khoảng %.0f m; hệ thống yêu cầu tối đa %.0f m. "
                        + "Hãy bật Vị trí chính xác, Wi-Fi và thử lại.",
                location.getAccuracy(),
                maximumAccuracyMeters);
    }
}
