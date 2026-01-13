package yookassa.api.dtos;

import java.util.List;

public record ErrorDto(

        List<String> errorMessages

) {
}
