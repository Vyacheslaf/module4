package com.epam.esm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria implements Serializable {
    private List<String> tagNameList;
    private String search;
    private List<String> sortList;
    private Integer page;
    private Integer size;
}
