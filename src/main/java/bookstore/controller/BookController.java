package bookstore.controller;

import bookstore.dto.BookDto;
import bookstore.dto.BookSearchParametersDto;
import bookstore.dto.CreateBookRequestDto;
import bookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book_store", description = "bookstore management application")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all Books", description = "Get all books from database"
            + ",Method can sort the results and divide them into pages")
    public List<BookDto> getAll(@ParameterObject Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Book by id", description = "Get book by id from database")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search Books",
            description = "Search books by params: Author, Title or Isbn")
    public List<BookDto> searchBooks(@ModelAttribute
                                         @ParameterObject BookSearchParametersDto params,
                                     @ParameterObject Pageable pageable) {
        return bookService.search(params,pageable);
    }

    @PostMapping
    @Operation(summary = "Add Book", description = "Add Book to Database")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.save(createBookRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Book by id", description = "Delete Book from Database by id")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Book by id", description = "Update Book in Database by id")
    public BookDto updateBookById(@PathVariable Long id,
                                  @RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.updateBookById(id,createBookRequestDto);
    }
}
