package com.universe.mobileapi;

final class GeofencePolicy {

    static final double RADIUS_METERS = 50.0;
    static final double MAX_LOCATION_ACCURACY_METERS = 50.0;

    private GeofencePolicy() {
    }

    static double effectiveRadius(double configuredRadius) {
        if (!Double.isFinite(configuredRadius) || configuredRadius <= 0) {
            return RADIUS_METERS;
        }
        return Math.min(configuredRadius, RADIUS_METERS);
    }

    static boolean hasRequiredAccuracy(double accuracy) {
        return Double.isFinite(accuracy)
                && accuracy >= 0
                && accuracy <= MAX_LOCATION_ACCURACY_METERS;
    }

    static boolean isInside(double distance, double configuredRadius) {
        return Double.isFinite(distance)
                && distance <= effectiveRadius(configuredRadius);
    }
}
