package bookstore.validation;

import bookstore.repository.book.BookRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

    private final BookRepository bookRepository;
    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return true;
        }
        if ("PUT".equals(httpServletRequest.getMethod())) {
            return true;
        }
        return !bookRepository.existsByIsbn(isbn);
    }
}
