package bookstore.service;

import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CreateCategoryDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable);

    CategoryDto save(CreateCategoryDto createCategoryDto);

    CategoryDto update(Long id, CreateCategoryDto createCategoryDto);

    void deleteById(Long id);
}
