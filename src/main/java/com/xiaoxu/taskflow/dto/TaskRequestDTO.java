package com.xiaoxu.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    private String status;


}