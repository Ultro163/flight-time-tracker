package com.example.data.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.utils.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Модель данных для представления информации о рейсе.
 *
 * <p>Этот класс используется для описания рейса, включая информацию о типе и номере
 * воздушного судна, времени вылета и посадки, аэропортах отправления и прибытия, а также
 * о составе экипажа. В класс встроены методы для вычисления продолжительности рейса и
 * форматирования времени вылета.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    @JsonProperty("aircraft_type")
    private String aircraftType;

    @JsonProperty("aircraft_number")
    private Integer aircraftNumber;

    @JsonProperty("takeoff_time")
    private LocalDateTime takeoffTime;

    @JsonProperty("landing_time")
    private LocalDateTime landingTime;

    @JsonProperty("departure_airport")
    private String departureAirport;

    @JsonProperty("arrival_airport")
    private String arrivalAirport;

    @JsonProperty("crew")
    private List<Long> crew;

    /**
     * Метод для вычисления продолжительности рейса в часах, основанный на времени вылета и посадки.
     *
     * @return продолжительность рейса в часах.
     */
    public long getFlightDurationHours() {
        return DateUtils.calculateFlightDuration(takeoffTime, landingTime);
    }

    /**
     * Метод для получения месяца вылета в формате <code>yyyy-MM</code>.
     *
     * @return месяц вылета или <code>null</code>, если время вылета не задано.
     */
    public String getTakeoffMonth() {
        if (takeoffTime != null) {
            return takeoffTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return null;
    }
}