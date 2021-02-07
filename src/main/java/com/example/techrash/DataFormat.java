package com.example.techrash;

import lombok.Data;

import java.util.List;

@Data
public class DataFormat {
    private String[] header;
    private List<String[]> otherRowList;
}
