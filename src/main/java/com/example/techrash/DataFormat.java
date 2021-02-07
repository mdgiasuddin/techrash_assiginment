package com.example.techrash;

import lombok.Data;

import java.util.List;

@Data
public class DataFormat {
    private List<String> header;
    private List<List<String>> otherRowList;
}
