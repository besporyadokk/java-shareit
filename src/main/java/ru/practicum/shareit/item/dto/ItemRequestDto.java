package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {
    private Integer id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private User owner;
    @NotNull(message = "Поле available обязательно")
    private Boolean available;


}
