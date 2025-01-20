package com.example.services;

import lombok.extern.slf4j.Slf4j;
import com.example.data.models.Flags;
import com.example.data.models.Flight;
import com.example.data.models.InputData;
import com.example.data.models.MonthlyData;
import com.example.data.models.OutputData;
import com.example.data.models.Specialist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;

/**
 * Сервис для обработки данных о полетах и специалистах.
 *
 * <p>Этот класс обрабатывает входные данные, группирует полеты по специалистам, вычисляет количество часов,
 * затраченных специалистами на полеты, и обновляет флаги для каждого месяца на основе этих данных.</p>
 */
@Slf4j
public class JsonFileProcessorImpl implements JsonFileProcessor {
    private final Map<Long, Specialist> specialistMap = new HashMap<>();
    private final Map<Long, List<Flight>> specialistFlightsMap = new HashMap<>();

    /**
     * Обрабатывает входные данные, выполняет группировку полетов по специалистам и обновление данных по месяцам.
     *
     * <p>Этот метод обрабатывает входные данные и генерирует выходные данные, включающие обновленные
     * данные по специалистам, которые включают часы работы и флаги для каждого месяца.</p>
     *
     * @param inputData объект {@link InputData}, содержащий данные о полетах и специалистах.
     * @return объект {@link OutputData}, содержащий список специалистов с обновленными данными.
     */
    @Override
    public OutputData processInputData(InputData inputData) {
        log.info("Начало обработки входных данных.");
        OutputData outputData = new OutputData();

        initializeSpecialists(inputData);
        log.debug("Инициализировано {} специалистов.", specialistMap.size());

        groupFlightsBySpecialist(inputData.getFlights());
        log.debug("Группировка полетов по специалистам завершена.");

        processSpecialistFlights();
        log.debug("Обработка полетов специалистов завершена.");

        specialistMap.values().forEach(specialist -> {
            List<MonthlyData> sortedMonthlyData = specialist.getMonthlyData().stream()
                    .sorted(Comparator.comparing(MonthlyData::getMonth))
                    .toList();
            specialist.setMonthlyData(sortedMonthlyData);
        });

        outputData.getSpecialists().addAll(specialistMap.values());
        log.info("Обработка входных данных завершена.");

        return outputData;
    }

    /**
     * Инициализирует специалистов на основе входных данных.
     *
     * @param inputData объект {@link InputData}, содержащий информацию о специалистах.
     */
    private void initializeSpecialists(InputData inputData) {
        log.debug("Инициализация специалистов.");
        inputData.getSpecialists().forEach(specialist -> {
            specialistMap.put(specialist.getId(), specialist);
            log.trace("Инициализирован специалист: {}", specialist);
        });
    }

    /**
     * Группирует полеты по специалистам.
     *
     * @param flights список полетов, который необходимо сгруппировать.
     */
    private void groupFlightsBySpecialist(List<Flight> flights) {
        log.debug("Группировка полетов по специалистам.");
        for (Flight flight : flights) {
            for (Long specialistId : flight.getCrew()) {
                specialistFlightsMap
                        .computeIfAbsent(specialistId, id -> new java.util.ArrayList<>())
                        .add(flight);
                log.trace("Добавлен полет {} для специалиста с ID {}", flight, specialistId);
            }
        }
    }

    /**
     * Обрабатывает полеты специалистов и обновляет данные по месяцам.
     */
    private void processSpecialistFlights() {
        log.debug("Обработка полетов специалистов.");
        for (Map.Entry<Long, List<Flight>> entry : specialistFlightsMap.entrySet()) {
            Long specialistId = entry.getKey();
            Specialist specialist = specialistMap.get(specialistId);
            if (specialist == null) {
                log.warn("Специалист с ID {} не найден.", specialistId);
                continue;
            }

            log.trace("Обработка полетов для специалиста: {}", specialist);
            Map<String, List<Flight>> flightsByMonth = entry.getValue().stream()
                    .collect(Collectors.groupingBy(Flight::getTakeoffMonth));

            for (Map.Entry<String, List<Flight>> monthEntry : flightsByMonth.entrySet()) {
                List<Flight> flights = monthEntry.getValue();
                log.trace("Обновление данных за месяц {} для специалиста {}", monthEntry.getKey(), specialist);
                updateMonthlyData(specialist, flights);
            }
        }
    }

    /**
     * Возвращает или создает данные по месяцу для специалиста.
     *
     * @param specialist специалист, для которого необходимо создать данные по месяцу.
     * @param month      месяц, для которого требуется данные.
     * @return объект {@link MonthlyData} для указанного месяца.
     */
    private MonthlyData getOrCreateMonthlyData(Specialist specialist, String month) {
        return specialist.getMonthlyData().stream()
                .filter(data -> data.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    MonthlyData newData = new MonthlyData(month, 0, new Flags());
                    specialist.getMonthlyData().add(newData);
                    log.trace("Созданы новые данные за месяц {} для специалиста {}", month, specialist);
                    return newData;
                });
    }

    /**
     * Обновляет данные по месяцам на основе полетов специалистов.
     *
     * @param specialist специалист, для которого обновляются данные.
     * @param flights    список полетов, на основе которых обновляются данные.
     */
    private void updateMonthlyData(Specialist specialist, List<Flight> flights) {
        log.debug("Обновление данных по месяцам для специалиста {}.", specialist);

        Map<String, Long> monthlyFlightHours = new HashMap<>();
        Map<LocalDate, Long> dailyFlightHours = new HashMap<>();
        Map<LocalDate, Long> weeklyFlightHours = new HashMap<>();

        for (Flight flight : flights) {
            if (flight.getTakeoffTime().isAfter(flight.getLandingTime())) {
                log.error("Данные не учтены, некорректные данные: время взлета {} после времени посадки {}. " +
                                "Специалист: {}, Полет: {}",
                        flight.getTakeoffTime(), flight.getLandingTime(),
                        specialist.getName(), flight);
            } else {
                log.trace("Обработка полета {} для специалиста {}", flight, specialist);
                LocalDate startDate = flight.getTakeoffTime().toLocalDate();
                LocalDate endDate = flight.getLandingTime().toLocalDate();

                distributeFlightHoursByMonth(startDate, endDate, flight.getFlightDurationHours(), monthlyFlightHours,
                        flight.getTakeoffTime(), flight.getLandingTime()
                );


                distributeFlightHours(flight.getTakeoffTime(), flight.getLandingTime(),
                        flight.getFlightDurationHours(), dailyFlightHours);
            }
        }

        monthlyFlightHours.forEach((month, hours) -> {
            MonthlyData data = getOrCreateMonthlyData(specialist, month);
            data.addFlightTime(hours);
            log.trace("Обновлены часы за месяц {}: {} часов для специалиста {}", month, hours, specialist);
        });

        Map<String, Long> maxWeeklyHoursPerMonth = new HashMap<>();

        dailyFlightHours.forEach((date, hours) -> {
            LocalDate startOfWeek = date.with(WeekFields.ISO.dayOfWeek(), 1);
            String month = startOfWeek.getYear() + "-" + String.format("%02d", startOfWeek.getMonthValue());
            weeklyFlightHours.merge(startOfWeek, hours, Long::sum);
            maxWeeklyHoursPerMonth.merge(month, weeklyFlightHours.get(startOfWeek), Long::max);
        });

        Map<String, Long> dailyMaxHoursPerMonth = new HashMap<>();

        dailyFlightHours.forEach((date, hours) -> {
            String month = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            dailyMaxHoursPerMonth.merge(month, hours, Long::max);
        });

        monthlyFlightHours.keySet().forEach(month -> {
            MonthlyData data = getOrCreateMonthlyData(specialist, month);
            long maxDailyHoursForMonth = dailyMaxHoursPerMonth.getOrDefault(month, 0L);
            long maxWeeklyHoursForMonth = maxWeeklyHoursPerMonth.getOrDefault(month, 0L);
            data.updateFlags(maxDailyHoursForMonth, maxWeeklyHoursForMonth);
            log.trace("Обновлены флаги за месяц {} для специалиста {}", month, specialist);
        });
    }

    /**
     * Распределяет часы полета по месяцам.
     *
     * @param startDate          дата начала полета.
     * @param endDate            дата окончания полета.
     * @param totalDuration      продолжительность полета в часах.
     * @param monthlyFlightHours карта для накопления часов по месяцам.
     * @param takeoffTime        время взлета.
     * @param landingTime        время посадки.
     */
    private void distributeFlightHoursByMonth(LocalDate startDate, LocalDate endDate, long totalDuration,
                                              Map<String, Long> monthlyFlightHours,
                                              LocalDateTime takeoffTime, LocalDateTime landingTime) {
        long remainingDuration = totalDuration;

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            long hoursForDay;

            if (currentDate.equals(takeoffTime.toLocalDate())) {
                hoursForDay = Math.min(24L - takeoffTime.getHour(), remainingDuration);
            } else if (currentDate.equals(landingTime.toLocalDate())) {
                hoursForDay = Math.min(landingTime.getHour(), remainingDuration);
            } else {
                hoursForDay = Math.min(24, remainingDuration);
            }
            remainingDuration -= hoursForDay;

            String currentMonth = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue());
            monthlyFlightHours.merge(currentMonth, hoursForDay, Long::sum);
            log.trace("Добавлено {} часов за день {} в месяц {}", hoursForDay, currentDate, currentMonth);

            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Распределяет часы полета по дням.
     *
     * @param takeoffTime      время взлета.
     * @param landingTime      время посадки.
     * @param totalDuration    продолжительность полета в часах.
     * @param dailyFlightHours карта для накопления часов по дням.
     */
    private void distributeFlightHours(LocalDateTime takeoffTime, LocalDateTime landingTime, long totalDuration,
                                       Map<LocalDate, Long> dailyFlightHours) {
        long remainingDuration = totalDuration;

        LocalDate startDate = takeoffTime.toLocalDate();
        LocalDate endDate = landingTime.toLocalDate();

        ObjLongConsumer<LocalDate> addHoursForDay = (date, hours) -> {
            dailyFlightHours.merge(date, hours, Long::sum);
            log.trace("Добавлено {} часов за день {}", hours, date);
        };

        if (startDate.equals(endDate)) {
            long hoursForDay = Math.min(remainingDuration, 24L);
            addHoursForDay.accept(startDate, hoursForDay);
            remainingDuration -= hoursForDay;
        } else {
            long hoursForFirstDay = Math.min(24L - takeoffTime.getHour(), remainingDuration);
            addHoursForDay.accept(startDate, hoursForFirstDay);
            remainingDuration -= hoursForFirstDay;

            long hoursForLastDay = Math.min(landingTime.getHour(), remainingDuration);
            addHoursForDay.accept(endDate, hoursForLastDay);
            remainingDuration -= hoursForLastDay;

            LocalDate currentDate = startDate.plusDays(1);
            while (currentDate.isBefore(endDate)) {
                long hoursForDay = Math.min(24, remainingDuration);
                addHoursForDay.accept(currentDate, hoursForDay);
                remainingDuration -= hoursForDay;
                currentDate = currentDate.plusDays(1);
            }
        }
        while (remainingDuration > 0) {
            long hoursForDay = Math.min(24, remainingDuration);
            addHoursForDay.accept(startDate, hoursForDay);
            remainingDuration -= hoursForDay;
            startDate = startDate.plusDays(1);
        }
    }
}