package com.universe.mobileapi;

import java.security.MessageDigest;
import java.util.HexFormat;

final class QrVerifier {

    static final int ROTATION_SECONDS = 5;
    static final int ALLOWED_SLOT_SKEW = 2;

    private QrVerifier() {
    }

    static ParsedQr parse(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new ServiceException(400, "Mã QR trống.");
        }
        String[] parts = payload.split("\\|", -1);
        if (parts.length != 8 || !"UNIVERSE".equals(parts[0])) {
            throw new ServiceException(400, "Mã QR không thuộc hệ thống UniVerse.");
        }
        try {
            return new ParsedQr(
                    parts[1],
                    parts[2],
                    Long.parseLong(parts[3]),
                    Double.parseDouble(parts[4]),
                    Double.parseDouble(parts[5]),
                    Double.parseDouble(parts[6]),
                    parts[7]);
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, "Mã QR có dữ liệu không hợp lệ.");
        }
    }

    static boolean hasValidSignature(ParsedQr qr, long currentTimeMillis, String secret) {
        long currentSlot = currentTimeMillis / (ROTATION_SECONDS * 1000L);
        if (Math.abs(currentSlot - qr.timeSlot()) > ALLOWED_SLOT_SKEW) {
            return false;
        }
        String expected = signature(qr.qrId(), qr.sessionId(), qr.timeSlot(), secret);
        return MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                qr.signature().getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    }

    static String signature(String qrId, String sessionId, long timeSlot, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(
                    (qrId + "|" + sessionId + "|" + timeSlot)
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash, 0, 8);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể kiểm tra chữ ký QR.", ex);
        }
    }

    static double distanceMeters(double latitude1, double longitude1,
                                 double latitude2, double longitude2) {
        double earthRadius = 6_371_000;
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double deltaLat = Math.toRadians(latitude2 - latitude1);
        double deltaLng = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    record ParsedQr(
            String qrId,
            String sessionId,
            long timeSlot,
            double embeddedLatitude,
            double embeddedLongitude,
            double embeddedRadius,
            String signature) {
    }
}

