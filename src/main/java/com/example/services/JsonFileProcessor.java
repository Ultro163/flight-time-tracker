package com.example.services;

import com.example.data.models.InputData;
import com.example.data.models.OutputData;

public interface JsonFileProcessor {

    OutputData processInputData(InputData inputData);
}