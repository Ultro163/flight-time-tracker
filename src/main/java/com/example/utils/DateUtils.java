package com.example.utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Утилитный класс для работы с датами и временем.
 * Этот класс предназначен для выполнения различных операций с временными метками, таких как вычисление продолжительности полета.
 * <p>
 * Данный класс является утилитным и не должен создавать экземпляры.
 * </p>
 */
public class DateUtils {

    /**
     * Приватный конструктор, чтобы предотвратить создание экземпляров данного класса.
     */
    private DateUtils() {
        throw new UnsupportedOperationException("Этот класс не предназначен для создания экземпляров");
    }

    /**
     * Вычисляет продолжительность полета в часах на основе времени взлета и посадки.
     *
     * <p>Метод рассчитывает разницу между временем взлета и посадки и возвращает продолжительность в часах.</p>
     *
     * @param takeoff время взлета.
     * @param landing время посадки.
     * @return продолжительность полета в часах.
     */
    public static long calculateFlightDuration(LocalDateTime takeoff, LocalDateTime landing) {

        return Duration.between(takeoff, landing).toHours();
    }
}