package com.example.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель данных для флагов, указывающих на превышение различных ограничений по рабочим часам.
 *
 * <p>Класс представляет собой простую структуру данных с полями, обозначающими:
 * <ul>
 *     <li><code>over80Hours</code> — превышение 80 часов за определенный период.</li>
 *     <li><code>weeksOver36Hours</code> — превышение 36 рабочих часов в неделю.</li>
 *     <li><code>daysOver8Hours</code> — превышение 8 рабочих часов в день.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flags {
    private boolean over80Hours;
    private boolean weeksOver36Hours;
    private boolean daysOver8Hours;
}