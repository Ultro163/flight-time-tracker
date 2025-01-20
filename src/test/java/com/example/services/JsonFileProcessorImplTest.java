package com.example.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.data.models.Flight;
import com.example.data.models.InputData;
import com.example.data.models.MonthlyData;
import com.example.data.models.OutputData;
import com.example.data.models.Specialist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFileProcessorImplTest {

    private JsonFileProcessor jsonFileProcessorImpl;

    @BeforeEach
    void setUp() {
        jsonFileProcessorImpl = new JsonFileProcessorImpl();
    }

    @Test
    void testSingleDayFlight() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 10, 30, 9, 0),
                LocalDateTime.of(2024, 10, 30, 18, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData monthlyData = outputData.getSpecialists().getFirst().getMonthlyData().getFirst();
        assertEquals(9, monthlyData.getFlightTimeHours());
        assertTrue(monthlyData.getFlags().isDaysOver8Hours());
    }

    @Test
    void testMultiDayFlight() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 6, 30, 10, 0),
                LocalDateTime.of(2024, 7, 2, 15, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData novemberData = outputData.getSpecialists().getFirst().getMonthlyData().get(0);
        MonthlyData decemberData = outputData.getSpecialists().getFirst().getMonthlyData().get(1);

        assertEquals(14, novemberData.getFlightTimeHours());
        assertEquals(39, decemberData.getFlightTimeHours());
        assertTrue(decemberData.getFlags().isDaysOver8Hours());
        assertTrue(decemberData.getFlags().isWeeksOver36Hours());
        assertTrue(novemberData.getFlags().isDaysOver8Hours());
        assertFalse(novemberData.getFlags().isWeeksOver36Hours());
    }

    @Test
    void testFlightSpanningMultipleMonths() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 10, 31, 22, 0),
                LocalDateTime.of(2024, 12, 1, 5, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData octoberData = outputData.getSpecialists().getFirst().getMonthlyData().get(0);
        MonthlyData novemberData = outputData.getSpecialists().getFirst().getMonthlyData().get(1);
        MonthlyData decemberData = outputData.getSpecialists().getFirst().getMonthlyData().get(2);

        assertEquals(2, octoberData.getFlightTimeHours());
        assertEquals(720, novemberData.getFlightTimeHours());
        assertEquals(5, decemberData.getFlightTimeHours());
        assertTrue(novemberData.getFlags().isDaysOver8Hours());
        assertTrue(novemberData.getFlags().isOver80Hours());
        assertTrue(novemberData.getFlags().isWeeksOver36Hours());
        assertFalse(octoberData.getFlags().isDaysOver8Hours());
        assertFalse(octoberData.getFlags().isOver80Hours());
        assertTrue(octoberData.getFlags().isWeeksOver36Hours());
        assertFalse(decemberData.getFlags().isDaysOver8Hours());
        assertFalse(decemberData.getFlags().isOver80Hours());
        assertFalse(decemberData.getFlags().isWeeksOver36Hours());
    }

    @Test
    void testZeroDurationFlight() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 11, 30, 10, 0),
                LocalDateTime.of(2024, 11, 30, 10, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData monthlyData = outputData.getSpecialists().getFirst().getMonthlyData().getFirst();
        assertEquals(0, monthlyData.getFlightTimeHours());
    }

    @Test
    void testMultipleSpecialistsOnSameFlight() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 6, 30, 10, 0),
                LocalDateTime.of(2024, 6, 30, 19, 0),
                "KUF",
                "VVO",
                List.of(4L, 5L)
        );

        Specialist specialist1 = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        Specialist specialist2 = new Specialist(5L, "Владимир Машков", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist1, specialist2));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData monthlyData1 = outputData.getSpecialists().get(0).getMonthlyData().getFirst();
        MonthlyData monthlyData2 = outputData.getSpecialists().get(1).getMonthlyData().getFirst();

        assertEquals(9, monthlyData1.getFlightTimeHours());
        assertEquals(9, monthlyData2.getFlightTimeHours());
    }

    @Test
    void testFlightSpanningMonthEnd() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 1, 31, 22, 0),
                LocalDateTime.of(2024, 2, 1, 5, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData januaryData = outputData.getSpecialists().getFirst().getMonthlyData().get(0);
        MonthlyData februaryData = outputData.getSpecialists().getFirst().getMonthlyData().get(1);

        assertEquals(2, januaryData.getFlightTimeHours());
        assertEquals(5, februaryData.getFlightTimeHours());
    }

    @Test
    void testFlightSpanningMultipleYears() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 12, 31, 22, 0),
                LocalDateTime.of(2025, 1, 1, 5, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData decemberData = outputData.getSpecialists().getFirst().getMonthlyData().get(0);
        MonthlyData januaryData = outputData.getSpecialists().getFirst().getMonthlyData().get(1);

        assertEquals(2, decemberData.getFlightTimeHours());
        assertEquals(5, januaryData.getFlightTimeHours());

        assertFalse(decemberData.getFlags().isDaysOver8Hours());
        assertFalse(januaryData.getFlags().isDaysOver8Hours());
        assertFalse(decemberData.getFlags().isWeeksOver36Hours());
        assertFalse(januaryData.getFlags().isWeeksOver36Hours());
    }

    @Test
    void testNoFlightsForSpecialists() {
        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(new ArrayList<>(), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        assertTrue(outputData.getSpecialists().getFirst().getMonthlyData().isEmpty());
    }

    @Test
    void testInvalidFlightData() {
        Flight flight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 6, 30, 10, 0),
                LocalDateTime.of(2024, 6, 29, 19, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );
        Flight secondFlight = new Flight(
                "Boeing 767",
                101,
                LocalDateTime.of(2024, 6, 29, 10, 0),
                LocalDateTime.of(2024, 6, 29, 19, 0),
                "KUF",
                "VVO",
                List.of(4L)
        );

        Specialist specialist = new Specialist(4L, "Данила Козловский", new ArrayList<>());
        InputData inputData = new InputData(List.of(flight,secondFlight), List.of(specialist));

        OutputData outputData = jsonFileProcessorImpl.processInputData(inputData);

        MonthlyData monthlyData = outputData.getSpecialists().getFirst().getMonthlyData().getFirst();

        assertEquals(9, monthlyData.getFlightTimeHours());
        assertTrue(monthlyData.getFlags().isDaysOver8Hours());
    }
}