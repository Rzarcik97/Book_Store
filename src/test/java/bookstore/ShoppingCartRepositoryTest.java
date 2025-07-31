package bookstore;

import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
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
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    public static void beforeAll(
            @Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingCart/add-shoppingCart-with-items.sql")
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
                    new ClassPathResource("database/shoppingCart/delete-tables.sql")
            );
        }
    }

    @Transactional
    @Test
    @DisplayName(""" 
            Verify that findAllByCategoryId returns all books that contains the given category.
            """)
    void getShoppingCartsByUser_ValidEmail_ReturnUsersShoppingCart() {
        //Given
        User user = new User()
                .setId(1L)
                .setEmail("User@email.com")
                .setPassword("Password")
                .setFirstName("User")
                .setLastName("Pass")
                .setShippingAddress("City")
                .setDeleted(false);
        Book book = new Book()
                .setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg")
                .setDeleted(false);
        Book book2 = new Book()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.55))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg")
                .setDeleted(false);
        ShoppingCart expected = new ShoppingCart()
                .setId(1L)
                .setUser(user);
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setQuantity(3)
                .setBook(book)
                .setShoppingCart(expected);
        CartItem cartItem2 = new CartItem()
                .setId(2L)
                .setQuantity(5)
                .setBook(book2)
                .setShoppingCart(expected);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cartItems.add(cartItem2);
        expected.setCartItems(cartItems);

        //when
        ShoppingCart actual = shoppingCartRepository.getShoppingCartsByUser("User@email.com");
        //then
        Assertions.assertEquals(expected, actual);
    }
}
