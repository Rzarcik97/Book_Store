package bookstore.dto;

import bookstore.validation.UniqueField;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateBookRequestDto {
    @NotBlank(message = "title must not be blank")
    private String title;
    @NotBlank(message = "author must not be blank")
    private String author;
    @NotNull
    @UniqueField
    @Pattern(regexp = "\\d{13}", message = "isbn must be a 13-digit number")
    private String isbn;
    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", message = "price must be non-negative")
    private BigDecimal price;
    private String description;
    @NotNull
    private String coverImage;
}
