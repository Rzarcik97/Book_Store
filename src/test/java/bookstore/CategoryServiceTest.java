package bookstore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import bookstore.dto.book.BookDto;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.mapper.CategoryMapper;
import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.book.BookRepository;
import bookstore.repository.category.CategoryRepository;
import bookstore.service.impl.CategoryServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
            Verify that the category is successfully saved in database
            """)
    public void save_WithValidCreateCategoryDto_categorySavedSuccessfully() {
        //Given
        Long id = 1L;
        Category savedCategory = new Category()
                .setId(id)
                .setName("Fantasy")
                .setDescription("Fantasy Description")
                .setDeleted(false);
        CategoryRequestDto given = new CategoryRequestDto(
                 "Fantasy",
                "Fantasy Description"
        );
        CategoryDto expected = new CategoryDto(
                1L,
                "Fantasy",
                "Fantasy Description"
        );

        when(categoryMapper.toModel(given)).thenReturn(savedCategory);
        when(categoryRepository.save(savedCategory)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expected);
        // When
        CategoryDto actual = categoryService.save(given);
        // Then
        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).save(savedCategory);
        verify(categoryMapper, times(1)).toModel(given);
        verify(categoryMapper, times(1)).toDto(savedCategory);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that the category with null throws exception
            """)
    public void save_WithNullCreateCategoryDto_throwsException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.save(null),
                "Cannot save Category: Category is null");
    }

    @Test
    @DisplayName("""
            Verify that the categories are loaded from the database
            and that pagination works correctly.
            """)
    public void findAll_ValidRequestWithPageable_CategoriesFounded() {
        Category category1 = new Category()
                .setId(1L)
                .setName("Fantasy")
                .setDescription("Fantasy Description")
                .setDeleted(false);

        Category category2 = new Category()
                .setId(1L)
                .setName("Science")
                .setDescription("Science Description")
                .setDeleted(false);
        CategoryDto categoryDto1 = new CategoryDto(
                1L,
                "Fantasy",
                "Fantasy Description"
        );
        CategoryDto categoryDto2 = new CategoryDto(
                2L,
                "Science",
                "Science Description"
        );
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> expected = List.of(category1, category2);
        Page<Category> categoriesPage = new PageImpl<>(expected, pageable, expected.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoriesPage);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);
        // When
        List<CategoryDto> actual = categoryService.findAll(pageable);
        //Then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEqualTo(categoryDto1);
        assertThat(actual.get(1)).isEqualTo(categoryDto2);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category1);
        verify(categoryMapper, times(1)).toDto(category2);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify that the category with given id is loaded successfully from database.
            """)
    public void getById_ValidId_Success() {
        //Given
        Long id = 1L;
        Category category1 = new Category()
                .setId(1L)
                .setName("Fantasy")
                .setDescription("Fantasy Description")
                .setDeleted(false);
        CategoryDto expected = new CategoryDto(
                1L,
                "Fantasy",
                "Fantasy Description"
        );
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category1));
        when(categoryMapper.toDto(category1)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.getById(id);
        //then
        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toDto(category1);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify that the category with invalid id throws an exception.
            """)
    public void getById_invalidId_throwsException() {
        //given
        Long id = 100L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        //Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(id),
                "Category with id: " + id + " not found");
    }

    @Test
    @DisplayName("""
            Verify that the Books with given category id is loaded successfully from database.
            """)
    public void getBooksByCategoryId_ValidId_Success() {
        //Given
        Long id = 1L;
        Book book1 = new Book()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg")
                .setDeleted(false);
        Book book2 = new Book()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg")
                .setDeleted(false);
        BookDto bookDto1 = new BookDto()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        BookDto bookDto2 = new BookDto()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg");
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> expected = List.of(book1, book2);
        Page<Book> booksPage = new PageImpl<>(expected, pageable, expected.size());

        when(bookRepository.findAllByCategoryId(id,pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        //When
        List<BookDto> actual = categoryService.getBooksByCategoryId(id,pageable);
        //then
        verify(bookRepository, times(1)).findAllByCategoryId(id,pageable);
        verify(bookMapper, times(1)).toDto(book1);
        verify(bookMapper, times(1)).toDto(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that the book is successfully updated with valid Dto and id.
            """)
    public void updateBookById_ValidIdAndDto_bookUpdatedSuccessfully() {
        Long id = 1L;
        Category category = new Category()
                .setId(id)
                .setName("Fantasy")
                .setDescription("Fantasy Description")
                .setDeleted(false);
        CategoryRequestDto given = new CategoryRequestDto(
                "Science",
                "Science Description"
        );
        CategoryDto expected = new CategoryDto(
                1L,
                "Science",
                "Science Description"
        );
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        doAnswer(c -> {
            CategoryRequestDto requestDto = c.getArgument(0);
            category.setName(requestDto.name());
            category.setDescription(requestDto.description());
            return null;
        }).when(categoryMapper).updateModelFromDto(given, category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        //When
        CategoryDto actual = categoryService.update(id, given);
        //Then
        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify that the Category update with null Dto throws an exception.
            """)
    public void update_nullDto_throwsException() {
        //given
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.of(new Category()));
        //Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(id, null),
                "Cannot update Category: Category is null");
    }

}
