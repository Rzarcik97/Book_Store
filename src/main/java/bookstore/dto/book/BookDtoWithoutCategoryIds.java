package bookstore.dto.book;

import java.math.BigDecimal;

public record BookDtoWithoutCategoryIds(
        Long id,
        String title,
        String author,
        String isbn,
        BigDecimal price,
        String category,
        String description,
        String coverImage) {
}
