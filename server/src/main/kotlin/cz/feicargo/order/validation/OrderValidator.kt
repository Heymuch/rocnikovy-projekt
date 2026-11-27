package cz.feicargo.order.validation

import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.round
import kotlin.uuid.Uuid

object OrderValidator {
    const val MAX_NAME_LENGTH = 100

    const val MIN_VOLUME = 1.0
    const val MAX_VOLUME = 3000.0

    const val MIN_WEIGHT = 1.0
    const val MAX_WEIGHT = 1500.0

    const val MIN_DEPARTURE_DAYS_AHEAD = 7L

    fun validateCalculationInput(
        stationFrom: Uuid,
        stationTo: Uuid,
        volume: Double,
        weight: Double,
    ) {
        require(stationFrom != stationTo) {
            "Origin and destination stations must be different."
        }
        require(volume in MIN_VOLUME..MAX_VOLUME) {
            "Cargo volume must be between $MIN_VOLUME and $MAX_VOLUME m3."
        }
        require(isHalfStep(volume)) {
            "Cargo volume must be in increments of 0.5 m3."
        }
        require(weight in MIN_WEIGHT..MAX_WEIGHT) {
            "Cargo weight must be between $MIN_WEIGHT and $MAX_WEIGHT t."
        }
        require(isHalfStep(weight)) {
            "Cargo weight must be in increments of 0.5 t."
        }
    }

    fun validateOrderDetails(
        name: String,
        preferredDeparture: Instant,
        preferredArrival: Instant,
        now: Instant = Instant.now(),
    ) {
        val trimmedName = name.trim()
        require(trimmedName.isNotBlank()) {
            "Order name must not be blank."
        }
        require(trimmedName.length <= MAX_NAME_LENGTH) {
            "Order name must be at most $MAX_NAME_LENGTH characters long."
        }

        val minDeparture = now.truncatedTo(ChronoUnit.DAYS).plus(MIN_DEPARTURE_DAYS_AHEAD, ChronoUnit.DAYS)
        require(!preferredDeparture.isBefore(minDeparture)) {
            "Preferred departure must be at least $MIN_DEPARTURE_DAYS_AHEAD days in the future."
        }
        require(preferredArrival.isAfter(preferredDeparture)) {
            "Preferred arrival must be after preferred departure."
        }
    }

    private fun isHalfStep(value: Double): Boolean {
        val doubled = value * 2.0
        return abs(doubled - round(doubled)) < 1e-9
    }
}
