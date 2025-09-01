package bookstore.controller;

import bookstore.dto.book.BookDto;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category_controller", description = "category management application")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get All Categories",
            description = "Get All Categories from DataBase")
    public List<CategoryDto> getAll(@ParameterObject Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category By Category Id",
            description = "Get Category By Category Id from Database")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get All books by Category Id",
            description = "Get All books by Category Id from database")
    public List<BookDto> getBooksByCategoryId(
            @PathVariable Long id,
            @ParameterObject Pageable pageable) {
        return categoryService.getBooksByCategoryId(id,pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Add Category", description = "Add Category to Database")
    public CategoryDto createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        return categoryService.save(categoryRequestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category", description = "Delete Category from Database")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update Category", description = "Update Category in Database")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody CategoryRequestDto categoryRequestDto) {
        return categoryService.update(id, categoryRequestDto);
    }
}
