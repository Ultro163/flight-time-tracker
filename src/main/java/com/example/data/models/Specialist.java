package com.example.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель данных для представления информации о специалисте.
 *
 * <p>Этот класс описывает специалиста с уникальным идентификатором, именем и списком данных о его
 * деятельности за месяц. Каждому специалисту соответствует список {@link MonthlyData}, который
 * хранит информацию о рабочих часах и флагах за каждый месяц.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specialist {
    private Long id;
    private String name;
    private List<MonthlyData> monthlyData = new ArrayList<>();
}