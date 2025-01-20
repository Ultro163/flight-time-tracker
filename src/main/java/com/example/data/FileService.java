package com.example.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.data.models.InputData;
import com.example.data.models.OutputData;

import java.io.File;
import java.io.IOException;

/**
 * Сервис для работы с файлами, содержащими данные в формате JSON.
 *
 * <p>Этот класс предоставляет методы для загрузки входных данных из файла и сохранения выходных данных
 * в файл с использованием библиотеки Jackson для сериализации и десериализации объектов.</p>
 */
@RequiredArgsConstructor
public class FileService {

    private final ObjectMapper objectMapper;

    /**
     * Метод для загрузки входных данных из файла.
     *
     * <p>Метод десериализует содержимое JSON-файла в объект {@link InputData}.</p>
     *
     * @param filePath путь к файлу с входными данными.
     * @return объект {@link InputData}, загруженный из файла.
     * @throws IOException если произошла ошибка при чтении файла.
     */
    public InputData loadInputData(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), InputData.class);
    }

    /**
     * Метод для сохранения выходных данных в файл.
     *
     * <p>Метод сериализует объект {@link OutputData} в JSON и сохраняет его в указанный файл.</p>
     *
     * @param filePath   путь к файлу для сохранения данных.
     * @param outputData объект {@link OutputData}, который необходимо сохранить.
     * @throws IOException если произошла ошибка при записи в файл.
     */
    public void saveOutputData(String filePath, OutputData outputData) throws IOException {
        objectMapper.writeValue(new File(filePath), outputData);
    }
}