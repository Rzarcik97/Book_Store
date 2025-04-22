package bookstore.repository.book;

import bookstore.dto.BookSearchParametersDto;
import bookstore.model.Book;
import bookstore.repository.SpecificationBuilder;
import bookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParameter) {
        Specification<Book> bookSpecification = Specification.where(null);
        if (bookSearchParameter.titles() != null && bookSearchParameter.titles().length > 0) {
            bookSpecification = bookSpecification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(bookSearchParameter.titles()));
        }
        if (bookSearchParameter.authors() != null && bookSearchParameter.authors().length > 0) {
            bookSpecification = bookSpecification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(bookSearchParameter.authors()));
        }
        if (bookSearchParameter.isbn() != null && bookSearchParameter.isbn().length > 0) {
            bookSpecification = bookSpecification.and(bookSpecificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(bookSearchParameter.isbn()));
        }
        return bookSpecification;
    }
}
