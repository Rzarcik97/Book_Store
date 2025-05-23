package bookstore.controller;

import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CreateCategoryDto;
import bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category_controller", description = "category management application")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAll(@ParameterObject Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@RequestParam Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @RequestParam Long id,
            @ParameterObject Pageable pageable) {
        return categoryService.getBooksByCategoryId(id,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoryDto createCategory(@RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.save(createCategoryDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@RequestParam Long id) {
        categoryService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@RequestParam Long id,
                                      @RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.update(id, createCategoryDto);
    }
}
