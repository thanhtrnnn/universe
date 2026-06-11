package com.universe.student.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class DeviceLocationProvider {

    public static final float MAX_ACCEPTABLE_ACCURACY_METERS = 50f;

    private static final float TARGET_ACCURACY_METERS = 25f;
    private static final long LOCATION_TIMEOUT_MILLIS = 30_000;
    private static final long MAX_LAST_KNOWN_AGE_MILLIS = 60_000;

    public interface Callback {
        void onLocation(Location location);

        void onError(String message);
    }

    private final LocationManager locationManager;

    public DeviceLocationProvider(Context context) {
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
            callback.onError(
                    "Dịch vụ vị trí đang tắt. Hãy bật GPS/Vị trí rồi thử lại.");
            return;
        }

        List<String> enabledProviders = locationManager.getProviders(true);
        AtomicReference<Location> bestLocation =
                new AtomicReference<>(findBestRecentLocation(enabledProviders));
        Location cached = bestLocation.get();
        float targetAccuracy = Math.min(TARGET_ACCURACY_METERS, maximumAccuracyMeters);
        if (hasAccuracyAtMost(cached, targetAccuracy)) {
            callback.onLocation(cached);
            return;
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        AtomicBoolean completed = new AtomicBoolean();
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null || !location.hasAccuracy()) {
                    return;
                }
                bestLocation.set(moreAccurate(bestLocation.get(), location));
                if (location.getAccuracy() <= targetAccuracy
                        && completed.compareAndSet(false, true)) {
                    locationManager.removeUpdates(this);
                    callback.onLocation(location);
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (!isLocationEnabled() && completed.compareAndSet(false, true)) {
                    locationManager.removeUpdates(this);
                    callback.onError(
                            "Dịch vụ vị trí đã bị tắt trong lúc lấy GPS.");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Retained for API 23 compatibility.
            }
        };

        boolean requested = requestUpdates(enabledProviders, listener);
        if (!requested) {
            callback.onError(
                    "Không có nguồn vị trí khả dụng. Hãy bật GPS và Vị trí chính xác.");
            return;
        }

        mainHandler.postDelayed(() -> {
            if (!completed.compareAndSet(false, true)) {
                return;
            }
            locationManager.removeUpdates(listener);
            Location best = bestLocation.get();
            if (hasAccuracyAtMost(best, maximumAccuracyMeters)) {
                callback.onLocation(best);
                return;
            }
            callback.onError(accuracyError(best, maximumAccuracyMeters));
        }, LOCATION_TIMEOUT_MILLIS);
    }

    @SuppressLint("MissingPermission")
    private Location findBestRecentLocation(List<String> providers) {
        Location best = null;
        long now = System.currentTimeMillis();
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null || !location.hasAccuracy()
                    || now - location.getTime() > MAX_LAST_KNOWN_AGE_MILLIS) {
                continue;
            }
            best = moreAccurate(best, location);
        }
        return best;
    }

    @SuppressLint("MissingPermission")
    private boolean requestUpdates(List<String> providers, LocationListener listener) {
        boolean requested = false;
        String[] requestedProviders = {
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
        };
        for (String provider : requestedProviders) {
            if (!providers.contains(provider)) {
                continue;
            }
            try {
                locationManager.requestLocationUpdates(
                        provider,
                        500,
                        0,
                        listener,
                        Looper.getMainLooper());
                requested = true;
            } catch (IllegalArgumentException ignored) {
                // Provider disappeared while the request was being configured.
            }
        }
        return requested;
    }

    private boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private static Location moreAccurate(Location current, Location candidate) {
        if (current == null) {
            return candidate;
        }
        if (candidate == null) {
            return current;
        }
        return candidate.getAccuracy() < current.getAccuracy() ? candidate : current;
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
            return "Không nhận được vị trí GPS trong 30 giây. "
                    + "Hãy bật Vị trí chính xác, Wi-Fi và thử ở gần cửa sổ.";
        }
        return String.format(
                Locale.US,
                "GPS hiện chỉ đạt sai số khoảng %.0f m; geofence yêu cầu tối đa %.0f m. "
                        + "Hãy bật Vị trí chính xác, Wi-Fi và thử ở gần cửa sổ.",
                location.getAccuracy(),
                maximumAccuracyMeters);
    }
}
