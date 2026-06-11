package com.universe.mobileapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QrVerifierTest {

    @Test
    void acceptsDesktopQrSignatureInCurrentSlot() {
        long now = System.currentTimeMillis();
        long slot = now / (QrVerifier.ROTATION_SECONDS * 1000L);
        String signature = QrVerifier.signature("QR01", "SES01", slot);
        QrVerifier.ParsedQr qr = QrVerifier.parse(
                "UNIVERSE|QR01|SES01|" + slot
                        + "|21.028511|105.804817|50.0|" + signature);

        assertTrue(QrVerifier.hasValidSignature(qr, now));
        assertEquals("QR01", qr.qrId());
    }

    @Test
    void rejectsExpiredSlot() {
        long now = System.currentTimeMillis();
        long slot = now / (QrVerifier.ROTATION_SECONDS * 1000L) - 10;
        String signature = QrVerifier.signature("QR01", "SES01", slot);
        QrVerifier.ParsedQr qr = QrVerifier.parse(
                "UNIVERSE|QR01|SES01|" + slot
                        + "|21.028511|105.804817|50.0|" + signature);

        assertFalse(QrVerifier.hasValidSignature(qr, now));
    }

    @Test
    void calculatesShortDistance() {
        double distance = QrVerifier.distanceMeters(
                21.028511, 105.804817,
                21.028520, 105.804810);

        assertTrue(distance < 2);
    }
}

