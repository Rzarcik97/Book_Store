package bookstore.service;

import bookstore.dto.book.BookDto;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    List<BookDto> getBooksByCategoryId(Long id, Pageable pageable);

    CategoryDto save(CategoryRequestDto categoryRequestDto);

    CategoryDto update(Long id, CategoryRequestDto categoryRequestDto);

    void deleteById(Long id);
}
