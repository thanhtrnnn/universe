package com.universe.mobileapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeofencePolicyTest {

    @Test
    void capsConfiguredRadiusAtFiftyMeters() {
        assertEquals(50.0, GeofencePolicy.effectiveRadius(100));
        assertEquals(30.0, GeofencePolicy.effectiveRadius(30));
    }

    @Test
    void rejectsLocationWithOneHundredSeventeenMeterAccuracy() {
        assertFalse(GeofencePolicy.hasRequiredAccuracy(117));
        assertTrue(GeofencePolicy.hasRequiredAccuracy(25));
    }

    @Test
    void appliesCircularFiftyMeterFence() {
        assertTrue(GeofencePolicy.isInside(49.9, 50));
        assertFalse(GeofencePolicy.isInside(50.1, 50));
        assertFalse(GeofencePolicy.isInside(60, 100));
    }
}
