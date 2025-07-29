package bookstore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.BookSearchParametersDto;
import bookstore.dto.book.CreateBookRequestDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.BookMapper;
import bookstore.model.Book;
import bookstore.repository.book.BookRepository;
import bookstore.repository.book.BookSpecificationBuilder;
import bookstore.service.impl.BookServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    
    @InjectMocks
    private BookServiceImpl bookServiceImpl;
    
    @Test
    @DisplayName("""
            Verify that the book is successfully saved in database
            """)
    public void save_WithValidCreateBookDto_BookSavedSuccessfully() {
        //Given
        Long id = 1L;
        Book savedBook = new Book();
        savedBook.setId(id)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg")
                .setDeleted(false);
        CreateBookRequestDto given = new CreateBookRequestDto();
        given.setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        BookDto expected = new BookDto();
        expected.setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        when(bookMapper.toModel(given)).thenReturn(savedBook);
        when(bookRepository.save(savedBook)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expected);
        // When
        BookDto actual = bookServiceImpl.save(given);
        // Then
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).save(savedBook);
        verify(bookMapper, times(1)).toModel(given);
        verify(bookMapper, times(1)).toDto(savedBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that the book with null throws exception
            """)
    public void save_WithNullCreateBookDto_throwsException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookServiceImpl.save(null),
                "Cannot save Book: Book is null");
    }

    @Test
    @DisplayName("""
            Verify that the books are loaded from the database and that pagination works correctly.
            """)
    public void findAll_ValidRequestWithPageable_BookFounded() {
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

        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        // When
        List<BookDto> actual = bookServiceImpl.findAll(pageable);
        //Then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEqualTo(bookDto1);
        assertThat(actual.get(1)).isEqualTo(bookDto2);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book1);
        verify(bookMapper, times(1)).toDto(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that the book with given id is loaded successfully from database.
            """)
    public void getBookById_ValidId_Success() {
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
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        when(bookRepository.findById(id)).thenReturn(Optional.of(book1));
        when(bookMapper.toDto(book1)).thenReturn(expected);
        //When
        BookDto actual = bookServiceImpl.getBookById(id);
        //then
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findById(id);
        verify(bookMapper, times(1)).toDto(book1);
        verifyNoMoreInteractions(bookRepository,bookMapper);
    }

    @Test
    @DisplayName("""
            Verify that the book with invalid id throws an exception.
            """)
    public void getBookById_invalidId_throwsException() {
        //given
        Long id = 100L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        //Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookServiceImpl.getBookById(id),
                "Book with id: " + id + " not found");
    }

    @Test
    @DisplayName("""
            Verify that the book is successfully updated with valid Dto and id.
            """)
    public void updateBookById_ValidIdAndDto_bookUpdatedSuccessfully() {
        Long id = 1L;
        Book book = new Book()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg")
                .setDeleted(false);
        CreateBookRequestDto given = new CreateBookRequestDto();
        given.setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg");
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg");
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookServiceImpl.updateBookById(id, given);
        //Then
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify that the book update with null Dto throws an exception.
            """)
    public void updateBookById_nullDto_throwsException() {
        //given
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.of(new Book()));
        //Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookServiceImpl.updateBookById(id, null),
                "Cannot save Book: Book is null");
    }

    @Test
    @DisplayName("""
            Verify that the searched book with valid params is loaded successfully from database.
            """)
    public void search_validParamsAndPagination_success() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                List.of("The Pragmatic Programmer"),
                List.of("Robert C. Martin"),
                null);
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
        Specification<Book> specificationMock = (
                root, query, cb) -> cb.conjunction();
        when(bookSpecificationBuilder.build(params)).thenReturn(specificationMock);
        when(bookRepository.findAll(specificationMock,pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        //when
        List<BookDto> actual = bookServiceImpl.search(params, pageable);
        //then
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).isEqualTo(bookDto1);
        assertThat(actual.get(1)).isEqualTo(bookDto2);

        verify(bookRepository, times(1)).findAll(specificationMock, pageable);
        verify(bookMapper, times(1)).toDto(book1);
        verify(bookMapper, times(1)).toDto(book2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}

