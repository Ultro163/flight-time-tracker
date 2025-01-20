package com.example.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс конфигурации приложения, предназначенный для загрузки и предоставления
 * свойств конфигурации из файла <code>config.properties</code>.
 *
 * <p>Класс не предназначен для создания экземпляров, поэтому конструктор объявлен
 * как private и выбрасывает исключение {@link UnsupportedOperationException}, если
 * вызывается.</p>
 *
 * <p>Свойства конфигурации загружаются статически при первом обращении к классу.
 * Если файл конфигурации отсутствует или не может быть загружен, ошибка будет
 * зарегистрирована в логах.</p>
 */
@Slf4j
public class AppConfig {
    private static final Properties properties = new Properties();

    /**
     * Закрытый конструктор для предотвращения создания экземпляров.
     *
     * @throws UnsupportedOperationException всегда при вызове.
     */
    private AppConfig() {
        throw new UnsupportedOperationException("Этот класс не предназначен для создания экземпляров");
    }

    static {
        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IOException("Файл конфигурации не найден");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Ошибка при загрузке конфигурации: {}", e.getMessage());
        }
    }

    /**
     * Получает путь к входному файлу, указанному в свойстве <code>inputFilePath</code>
     * файла конфигурации. Если свойство отсутствует, используется значение по умолчанию
     * <code>resources/input.json</code>.
     *
     * @return путь к входному файлу.
     */
    public static String getInputFilePath() {
        return properties.getProperty("inputFilePath", "resources/input.json");
    }

    /**
     * Получает путь к выходному файлу, указанному в свойстве <code>outputFilePath</code>
     * файла конфигурации. Если свойство отсутствует, используется значение по умолчанию
     * <code>resources/output.json</code>.
     *
     * @return путь к выходному файлу.
     */
    public static String getOutputFilePath() {
        return properties.getProperty("outputFilePath", "resources/output.json");
    }
}