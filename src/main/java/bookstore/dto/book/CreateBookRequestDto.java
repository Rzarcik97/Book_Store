package bookstore.dto.book;

import bookstore.model.Book;
import bookstore.validation.uniquefield.UniqueField;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CreateBookRequestDto {
    @NotBlank(message = "must not be blank")
    private String title;
    @NotBlank(message = "must not be blank")
    private String author;
    @NotNull
    @UniqueField(entity = Book.class, fieldName = "isbn",
            message = "already exist in DB")
    @Pattern(regexp = "\\d{13}", message = "must be a 13-digit number")
    private String isbn;
    @NotNull(message = "is required")
    @DecimalMin(value = "0.0", message = "must be non-negative")
    private BigDecimal price;
    private String description;
    @NotNull
    private String coverImage;
}
