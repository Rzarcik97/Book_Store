package bookstore.repository.book;

import bookstore.exeptions.SpecificationSearchFailedException;
import bookstore.model.Book;
import bookstore.repository.SpecificationProvider;
import bookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(spec -> spec.getKey().equals(key))
                .findFirst().orElseThrow(()
                        -> new SpecificationSearchFailedException(
                                "cannot find specification for " + key));
    }
}
