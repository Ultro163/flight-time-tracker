package com.example.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Модель данных для представления информации о полетах в рамках одного месяца.
 *
 * <p>Этот класс содержит информацию о времени полетов за месяц и флаги, которые определяют
 * различные ограничения по рабочим часам, такие как превышение 80 часов, 36 часов в неделю
 * и 8 часов в день.</p>
 *
 * <p>Также используется аннотация <code>@EqualsAndHashCode</code> для определения
 * равенства объектов по полю <code>month</code>.</p>
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "month")
public class MonthlyData {
    private String month;
    private long flightTimeHours;
    private Flags flags = new Flags();

    /**
     * Метод для добавления времени полета к общему числу часов.
     *
     * <p>Если переданное время меньше нуля, будет выброшено исключение
     * {@link IllegalArgumentException}.</p>
     *
     * @param hours количество часов, которое добавляется к общему времени полета.
     */
    public void addFlightTime(long hours) {
        if (hours < 0) {
            log.debug("Часы не могут быть отрицательными");
            throw new IllegalArgumentException("Время полета не может быть отрицательным.");
        }
        this.flightTimeHours += hours;
    }

    /**
     * Метод для обновления флагов на основе максимальных рабочих часов за день и неделю.
     *
     * <p>Флаги обновляются в зависимости от следующих условий:
     * <ul>
     *     <li>Если общее время полетов больше 80 часов, флаг <code>over80Hours</code> устанавливается в <code>true</code></li>
     *     <li>Если количество рабочих часов в неделю превышает 36, флаг <code>weeksOver36Hours</code> устанавливается в <code>true</code></li>
     *     <li>Если количество рабочих часов в день превышает 8, флаг <code>daysOver8Hours</code> устанавливается в <code>true</code></li>
     * </ul>
     * </p>
     *
     * @param dailyMaxHours  максимальное количество рабочих часов в день.
     * @param weeklyMaxHours максимальное количество рабочих часов в неделю.
     */
    public void updateFlags(long dailyMaxHours, long weeklyMaxHours) {
        this.flags.setOver80Hours(this.flightTimeHours > 80);
        this.flags.setWeeksOver36Hours(weeklyMaxHours > 36);
        this.flags.setDaysOver8Hours(dailyMaxHours > 8);
    }
}