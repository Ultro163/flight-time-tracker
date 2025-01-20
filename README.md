# Flight Time Tracker

### Разработчик
  Галкин Антон

### Тестовое задание на Java SE

## Описание проекта

Приложение загружает и обрабатывает данные о рабочем времени летных специалистов авиакомпании.
Выходной файл содержит данные о полётном времени за каждый месяц, содержащие отметки при превышении нормы рабочего 
времени.

## Руководство по запуску проекта

1. Установка JDK не ниже 21 версии.
2. Установка Apache Maven.

## Запуск проекта

1. Соберать проект c помощью Maven.
2. Запустить приложение.
3. Результат будет сохранён в указанном выходном файле.

## Тесты

Для запуска тестов выполните:

```bash
mvn test
```

## Формат входного файла

Пример входного файла находится в папке flight-time-tracker\resources, выходной файл будет так же сохранен в
flight-time-tracker\resources

- JSON-файл должен содержать следующие поля:
    - Специалисты (список через запятую)
    - Тип воздушного судна
    - Номер воздушного судна
    - Время взлёта
    - Время посадки
    - Аэропорты вылета и прибытия
    - Экипаж (список через запятую)

## Формат выходного файла

- Формат JSON.
- Данные по каждому летному специалисту, включающие месячное время и отметки при превышении норм.
