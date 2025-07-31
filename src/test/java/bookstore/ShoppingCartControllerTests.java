package bookstore;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bookstore.dto.cartitem.CartItemDto;
import bookstore.dto.cartitem.CartItemRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
public class ShoppingCartControllerTests {

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

    @WithMockUser(username = "User@email.com")
    @Test
    @DisplayName("Get User's ShoppingCart from database")
    void getShoppingCart_ValidRequestDto_Success() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        CartItemDto cartItemDto2 = new CartItemDto(2L,2L,"Clean Code",5);
        Set<CartItemDto> cartItems = Set.of(cartItemDto, cartItemDto2);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,cartItems);
        //When
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "User2@email.com")
    @Test
    @DisplayName("Get User's ShoppingCart from database")
    void getShoppingCart_UserWithoutShoppingCart_ReturnEmptyShoppingCart() throws Exception {
        //given
        ShoppingCartDto expected = new ShoppingCartDto(null,null,new HashSet<>());
        //When
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get User's ShoppingCart from database")
    void getShoppingCart_UserNotAuthorized_Return401Code() throws Exception {
        //When
        mockMvc.perform(
                get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "User@email.com")
    @Test
    @DisplayName("Add book to User's ShoppingCart to database")
    void addBooksToShoppingCart_ValidRequestDto_Success() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        CartItemDto cartItemDto2 = new CartItemDto(2L,2L,"Clean Code",5);
        CartItemDto cartItemDto3 = new CartItemDto(3L,3L,"Effective Java",8);
        Set<CartItemDto> cartItems = Set.of(cartItemDto, cartItemDto2, cartItemDto3);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,cartItems);
        CartItemRequestDto requestDto = new CartItemRequestDto(3L,8);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "User@email.com")
    @Test
    @DisplayName("Update book to User's ShoppingCart to database")
    void updateShoppingCart_ValidRequestDto_Success() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        CartItemDto cartItemDto2 = new CartItemDto(2L,2L,"Clean Code",8);
        Set<CartItemDto> cartItems = Set.of(cartItemDto, cartItemDto2);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,cartItems);
        Map<String,Integer> requestDto = Map.of("quantity",8);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        //When
        MvcResult result = mockMvc.perform(
                        put("/cart/cart-items/2")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "User@email.com")
    @Test
    @DisplayName("Delete CartItem from User's ShoppingCart from database")
    void removeBooksFromShoppingCart_ValidRequestDto_Success() throws Exception {
        //given
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        Set<CartItemDto> cartItems = Set.of(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,cartItems);
        //When
        mockMvc.perform(
                delete("/cart/cart-items/2")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
