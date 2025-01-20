package com.example.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Модель данных для входных данных, содержащих информацию о рейсах и специалистах.
 *
 * <p>Этот класс представляет собой структуру, в которой хранится список объектов {@link Flight},
 * представляющих рейсы, и список объектов {@link Specialist}, представляющих специалистов,
 * связанных с этими рейсами.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputData {
    private List<Flight> flights;
    private List<Specialist> specialists;
}