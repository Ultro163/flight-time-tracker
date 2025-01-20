package com.example;

import com.example.config.AppConfig;
import com.example.data.FileService;
import com.example.services.JsonFileProcessor;
import com.example.services.JsonFileProcessorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

/**
 * Основной класс приложения FlightTimeTrackerApp.
 * Этот класс служит точкой входа в приложение для отслеживания времени полета.
 * <p>
 * Приложение загружает входные данные о полетах, обрабатывает их, рассчитывает данные и сохраняет результаты.
 * </p>
 */
@Slf4j
public class FlightTimeTrackerApp {
    public static void main(String[] args) {
        log.info("Запуск приложения FlightTimeTracker");

        String inputFilePath = AppConfig.getInputFilePath();
        log.debug("Путь к входному файлу: {}", inputFilePath);
        String outputFilePath = AppConfig.getOutputFilePath();
        log.debug("Путь к выходному файлу: {}", outputFilePath);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FileService fileService = new FileService(objectMapper);
        JsonFileProcessor jsonFileProcessorImpl = new JsonFileProcessorImpl();

        try {
            log.info("Загрузка входных данных из файла: {}", inputFilePath);
            var inputData = fileService.loadInputData(inputFilePath);
            log.info("Входные данные успешно загружены. Начинается обработка...");

            var outputData = jsonFileProcessorImpl.processInputData(inputData);
            log.info("Обработка данных завершена. Сохранение результатов в файл: {}", outputFilePath);

            fileService.saveOutputData(outputFilePath, outputData);
            log.info("Выходные данные успешно сохранены в файл: {}", outputFilePath);
        } catch (Exception e) {
            log.error("Во время выполнения приложения произошла ошибка: {}", e.getMessage(), e);
        }
        log.info("Приложение FlightTimeTracker завершило выполнение.");
    }
}