package com.finanzmanager.finanzapp.dto;

import com.finanzmanager.finanzapp.model.CategoryType;
import lombok.Data;

@Data
public class CategoryDTO {

    private Long id;
    private String name;
    private CategoryType type;
}