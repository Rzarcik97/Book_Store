package bookstore.validation.uniquefield;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

    private final HttpServletRequest httpServletRequest;
    private final EntityManager entityManager;
    private String fieldName;
    private Class<?> entityClass;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
        this.entityClass = constraintAnnotation.entity();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context) {
        if (fieldValue == null) {
            return true;
        }
        if ("PUT".equals(httpServletRequest.getMethod())) {
            return true;
        }
        String query = "SELECT COUNT(e) FROM " + entityClass.getSimpleName()
                + " e WHERE e." + fieldName + " = :value";
        return entityManager.createQuery(query, Long.class)
                .setParameter("value", fieldValue)
                .getSingleResult() == 0;
    }
}
