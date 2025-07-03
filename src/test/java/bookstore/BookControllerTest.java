package bookstore;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.book.BookDto;
import bookstore.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/bookstest/add-three-default-books.sql")
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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all books from database")
    void getAll_ValidRequestDto_Success() throws Exception {
        //given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg"));
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(37.55))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg"));
        expected.add(new BookDto()
                .setId(3L)
                .setTitle("Effective Java")
                .setAuthor("Joshua Bloch")
                .setIsbn("9780134685991")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(45.33))
                .setDescription("Best practices for the Java platform.")
                .setCoverImage("https://example.com/images/effectivejava.jpg"));
        //When
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get book by id from database")
    void getBookById_ValidRequestDto_Success() throws Exception {
        //given
        BookDto expected = new BookDto().setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        //When
        MvcResult result = mockMvc.perform(
                        get("/books/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Search books by params: Author, Title or Isbn")
    void searchBooks_ValidRequestDto_Success() throws Exception {
        //given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg"));
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(37.55))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg"));
        //When
        MvcResult result = mockMvc.perform(
                        get("/books/search?authors=Andrew Hunt&isbns=9780132350884")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete book by id from database")
       void delete_ValidRequestDto_Success() throws Exception {
        //given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg"));
        expected.add(new BookDto()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(37.55))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg"));
        //When
        mockMvc.perform(
                        delete("/books/3")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new book and save it to database")
    void create_ValidRequestDto_Success() throws Exception {
        //given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("new title")
                .setAuthor("new author")
                .setIsbn("9780201611111")
                .setPrice(BigDecimal.valueOf(44.96))
                .setDescription("new description")
                .setCoverImage("new cover image");
        BookDto expected = new BookDto()
                .setId(4L)
                .setTitle("new title")
                .setAuthor("new author")
                .setIsbn("9780201611111")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(44.96))
                .setDescription("new description")
                .setCoverImage("new cover image");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("update book and save it to database")
    void update_ValidRequestDto_Success() throws Exception {
        //given
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("update title")
                .setAuthor("update author")
                .setIsbn("9780201616224")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("update title")
                .setAuthor("update author")
                .setIsbn("9780201616224")
                .setCategory("")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        put("/books/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
