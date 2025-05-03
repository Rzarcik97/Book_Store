package bookstore.repository.book;

import bookstore.dto.BookSearchParametersDto;
import bookstore.model.Book;
import bookstore.repository.SpecificationBuilder;
import bookstore.repository.SpecificationProviderManager;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto bookSearchParameter) {

        Map<String, List<String>> paramMap = Map.of(
                "title", bookSearchParameter.titles(),
                "author", bookSearchParameter.authors(),
                "isbn", bookSearchParameter.isbns());
        return paramMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(entry -> bookSpecificationProviderManager
                        .getSpecificationProvider(entry.getKey())
                        .getSpecification(entry.getValue()))
                .reduce(Specification.where(null), Specification::or);
    }
}
