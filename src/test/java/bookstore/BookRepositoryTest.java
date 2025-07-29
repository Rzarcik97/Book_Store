package bookstore;

import bookstore.model.Book;
import bookstore.model.Category;
import bookstore.repository.book.BookRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    public static void beforeAll(
            @Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categorytest/add-three-default-books-with-categories.sql")
            );
        }
    }

    @AfterAll
    public static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/bookstest/delete-tables.sql")
            );
        }
    }

    @Test
    @DisplayName(""" 
            Verify that findAllByCategoryId returns all books that contains the given category.
            """)
    void findAllByCategoryId_CategoryWithId1_ReturnOneBook() {
        //Given
        Category expectedCategory = new Category()
                .setId(1L)
                .setName("fantasy")
                .setDescription("fantasy description")
                .setDeleted(false);
        List<Book> expected = new ArrayList<>();
        expected.add(new Book()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory(Set.of(expectedCategory))
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg"));
        //when
        Page<Book> actual = bookRepository.findAllByCategoryId(1L, Pageable.unpaged());
        //then
        Assertions.assertEquals(expected, actual.stream().toList());
    }
}
