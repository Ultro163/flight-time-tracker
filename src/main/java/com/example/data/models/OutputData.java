package com.example.data.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель данных для вывода информации о специалистах.
 *
 * <p>Этот класс представляет собой структуру данных, в которой хранится список специалистов,
 * который будет использоваться для вывода информации. Изначально список специалистов
 * инициализируется пустым.</p>
 */
@Data
public class OutputData {
    private List<Specialist> specialists = new ArrayList<>();
}