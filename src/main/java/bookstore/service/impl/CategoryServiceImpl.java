package bookstore.service.impl;

import bookstore.dto.book.BookDtoWithoutCategoryIds;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CreateCategoryDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Category;
import bookstore.repository.book.BookRepository;
import bookstore.repository.category.CategoryRepository;
import bookstore.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id: " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public CategoryDto save(CreateCategoryDto createCategoryDto) {
        if (createCategoryDto != null) {
            Category category = categoryMapper.toModel(createCategoryDto);
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toDto(savedCategory);
        }
        throw new EntityNotFoundException("Cannot save Category: Category is null");
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryDto createCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category with id: " + id + " not found"));
        if (createCategoryDto != null) {
            categoryMapper.updateModelFromDto(createCategoryDto, category);
            Category savedCategory = categoryRepository.save(category);
            return categoryMapper.toDto(savedCategory);
        }
        throw new EntityNotFoundException("Cannot update Category: Category is null");
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
