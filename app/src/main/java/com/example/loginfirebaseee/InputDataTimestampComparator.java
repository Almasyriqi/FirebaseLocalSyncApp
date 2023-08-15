package com.example.loginfirebaseee;

import java.util.Comparator;

public class InputDataTimestampComparator implements Comparator<InputData>{
    @Override
    public int compare(InputData inputData1, InputData inputData2) {
        return Long.compare(inputData2.getTimestamp(), inputData1.getTimestamp());
    }
}
