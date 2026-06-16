package com.universe.mobileapi;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocationCalibrationServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-11T08:00:00Z"),
            ZoneOffset.UTC);

    @Test
    void acceptsPrecisePhoneLocation() throws Exception {
        LocationCalibrationService service = new LocationCalibrationService(
                (session, section, lecturer) -> true,
                CLOCK);
        JsonObject created = service.create("SESSION-1", "SECTION-1", "LECTURER-1");
        String token = created.get("token").getAsString();

        service.submit(token, "LECTURER-1", 21.028511, 105.804817, 8.5);

        JsonObject status = service.status(token, "LECTURER-1");
        assertEquals("ready", status.get("status").getAsString());
        assertEquals(8.5, status.get("accuracy").getAsDouble());
    }

    @Test
    void rejectsLocationWorseThanFiftyMeters() throws Exception {
        LocationCalibrationService service = new LocationCalibrationService(
                (session, section, lecturer) -> true,
                CLOCK);
        String token = service.create("SESSION-1", "SECTION-1", "LECTURER-1")
                .get("token").getAsString();

        ServiceException error = assertThrows(
                ServiceException.class,
                () -> service.submit(
                        token,
                        "LECTURER-1",
                        21.028511,
                        105.804817,
                        51));

        assertEquals(422, error.status());
    }

    @Test
    void rejectsSessionOutsideLecturerClass() {
        LocationCalibrationService service = new LocationCalibrationService(
                (session, section, lecturer) -> false,
                CLOCK);

        ServiceException error = assertThrows(
                ServiceException.class,
                () -> service.create("SESSION-1", "SECTION-1", "LECTURER-2"));

        assertEquals(403, error.status());
    }
}
