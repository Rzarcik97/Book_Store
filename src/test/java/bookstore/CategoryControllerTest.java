package bookstore;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.book.BookDto;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CategoryRequestDto;
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
public class CategoryControllerTest {

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
                    new ClassPathResource("database/categorytest/delete-tables.sql")
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get all categories from database")
    void getAll_ValidRequestDto_Success() throws Exception {
        //given
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto(1L,"fantasy","fantasy description"));
        expected.add(new CategoryDto(2L,"horror","horror description"));
        expected.add(new CategoryDto(3L,"romans","romans description"));
        //When
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto[].class);
        Assertions.assertEquals(3, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Get categories by id from database")
    void getCategoryById_ValidRequestDto_Success() throws Exception {
        //given
        CategoryDto expected = new CategoryDto(1L,"fantasy","fantasy description");
        //When
        MvcResult result = mockMvc.perform(
                        get("/categories/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("get books by given category id")
    void getBooksByCategoryId_ValidRequestDto_Success() throws Exception {
        //given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L)
                .setTitle("The Pragmatic Programmer")
                .setAuthor("Andrew Hunt")
                .setIsbn("9780201616224")
                .setCategory("fantasy")
                .setPrice(BigDecimal.valueOf(42.99))
                .setDescription("Classic book on software craftsmanship.")
                .setCoverImage("https://example.com/images/pragmatic.jpg"));
        //When
        MvcResult result = mockMvc.perform(
                        get("/categories/1/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete Category by id from database")
    void delete_ValidRequestDto_Success() throws Exception {
        //given
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto(1L,"fantasy","fantasy description"));
        expected.add(new CategoryDto(2L,"horror","horror description"));
        //When
        mockMvc.perform(
                delete("/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create new book and save to database")
    void create_ValidRequestDto_Success() throws Exception {
        //given
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "science","science description");
        CategoryDto expected = new CategoryDto(
                4L,"science","science description");
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("update book and save to database")
    void update_ValidRequestDto_Success() throws Exception {
        //given
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "updated category","updated description");
        CategoryDto expected = new CategoryDto(1L,
                "updated category","updated description");

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        put("/categories/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
