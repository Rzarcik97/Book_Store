package bookstore.service;

import bookstore.dto.BookDto;
import bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {

    BookDto save(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll();

    BookDto getBookById(Long id);

}
