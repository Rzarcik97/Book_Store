package bookstore.service.impl;

import bookstore.model.Book;
import bookstore.repository.BookRepository;
import bookstore.service.BookService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (book != null) {
            return bookRepository.save(book);
        }
        throw new RuntimeException("Cannot save Book: Book is null");
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
