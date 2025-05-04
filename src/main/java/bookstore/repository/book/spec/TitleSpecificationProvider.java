package bookstore.repository.book.spec;

import bookstore.model.Book;
import bookstore.repository.SpecificationProvider;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {

    public Specification<Book> getSpecification(List<String> params) {
        return (root, query, criteriaBuilder)
                -> root.get("title").in(params.stream().map(String::toLowerCase).toArray());
    }

    @Override
    public String getKey() {
        return "title";
    }
}
