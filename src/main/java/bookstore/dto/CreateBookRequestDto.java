package bookstore.dto;

import bookstore.validation.UniqueField;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CreateBookRequestDto {
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    @UniqueField
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    private String description;
    @NotNull
    private String coverImage;
}
