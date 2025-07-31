package bookstore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import bookstore.dto.cartitem.CartItemDto;
import bookstore.dto.cartitem.CartItemRequestDto;
import bookstore.dto.shoppingcart.ShoppingCartDto;
import bookstore.exceptions.EntityNotFoundException;
import bookstore.mapper.ShoppingCartMapper;
import bookstore.model.Book;
import bookstore.model.CartItem;
import bookstore.model.Role;
import bookstore.model.ShoppingCart;
import bookstore.model.User;
import bookstore.repository.book.BookRepository;
import bookstore.repository.shoppingcart.ShoppingCartRepository;
import bookstore.repository.user.UserRepository;
import bookstore.service.CartItemService;
import bookstore.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTests {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("""
            Verify that the shoppingCart is found and loaded from database by given user.
            """)
    public void getShoppingCart_ValidEmail_Success() {
        //Given
        String email = "test@test.com";
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("The Pragmatic Programmer")
                        .setAuthor("Andrew Hunt")
                        .setIsbn("9780201616224")
                        .setPrice(BigDecimal.valueOf(42.99))
                        .setDescription("Classic book on software craftsmanship.")
                        .setCoverImage("https://example.com/images/pragmatic.jpg")
                        .setDeleted(false))
                .setQuantity(3);
        CartItem cartItem2 = new CartItem()
                .setId(2L)
                .setBook(new Book()
                        .setId(3L)
                        .setTitle("Clean Code")
                        .setAuthor("Robert C. Martin")
                        .setIsbn("9780132350884")
                        .setPrice(BigDecimal.valueOf(37.50))
                        .setDescription("A handbook of agile software craftsmanship.")
                        .setCoverImage("https://example.com/images/cleancode.jpg"))
                .setQuantity(2);
        Set<CartItem> setOfCartItem = Set.of(cartItem, cartItem2);
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setCartItems(setOfCartItem)
                .setUser(new User()
                        .setEmail(email)
                        .setId(1L)
                        .setPassword("password")
                        .setFirstName("Robert")
                        .setLastName("Martin")
                        .setRoles(Set.of(new Role()
                                .setId(1L)
                                .setName(Role.RoleName.USER)))
                        .setShippingAddress("adress")
                        .setDeleted(false));
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        CartItemDto cartItemDto2 = new CartItemDto(2L,3L,"Clean Code",2);
        Set<CartItemDto> setOfCartItemDto1 = Set.of(cartItemDto, cartItemDto2);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,setOfCartItemDto1);

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(email);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the empty shoppingCart is returned when User Don't have any ShoppingCart.
            """)
    public void getShoppingCart_UserHaveNoShoppingCart_ReturnEmptyShoppingCart() {
        //Given
        String email = "test@test.com";
        ShoppingCartDto expected = new ShoppingCartDto(null,null,new HashSet<>());

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(null);
        when(shoppingCartMapper.toDto(any())).thenReturn(expected);
        //When
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(email);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the method return new empty ShoppingCart
             when given user dont have shoppingCart.
            """)
    public void getShoppingCart_UserDontHaveShoppingCart_ReturnEmptyShoppingCart_Success() {
        String email = "test@test.com";
        ShoppingCartDto expected = new ShoppingCartDto(null,null,new HashSet<>());
        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(null);
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(expected);
        //when
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(email);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the given book is added to given users shoppingCart.
            """)
    public void addBooksToShoppingCart_ValidRequest_Success() {
        String email = "test@test.com";
        User user = new User()
                .setEmail(email)
                .setId(1L)
                .setPassword("password")
                .setFirstName("Robert")
                .setLastName("Martin")
                .setRoles(Set.of(new Role()
                        .setId(1L)
                        .setName(Role.RoleName.USER)))
                .setShippingAddress("adress")
                .setDeleted(false);
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("The Pragmatic Programmer")
                        .setAuthor("Andrew Hunt")
                        .setIsbn("9780201616224")
                        .setPrice(BigDecimal.valueOf(42.99))
                        .setDescription("Classic book on software craftsmanship.")
                        .setCoverImage("https://example.com/images/pragmatic.jpg")
                        .setDeleted(false))
                .setQuantity(3);
        Set<CartItem> setOfCartItem = new HashSet<>();
        setOfCartItem.add(cartItem);
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setCartItems(setOfCartItem)
                .setUser(user);
        Book book = new Book()
                .setId(2L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg");
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(2L,3);
        CartItemDto cartItemDto = new CartItemDto(1L,1L,"The Pragmatic Programmer",3);
        CartItemDto cartItemDto2 = new CartItemDto(2L,3L,"Clean Code",3);
        Set<CartItemDto> setOfCartItemDto1 = Set.of(cartItemDto, cartItemDto2);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,setOfCartItemDto1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(shoppingCart);
        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.of(book));
        doAnswer(c -> {
            CartItem newCartItem = new CartItem()
                    .setId(2L)
                    .setBook(c.getArgument(0))
                    .setQuantity(c.getArgument(1))
                    .setShoppingCart(c.getArgument(2));
            shoppingCart.getCartItems().add(newCartItem);
            return null;
        }).when(cartItemService).save(book,cartItemRequestDto.quantity(),shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartDto actual = shoppingCartService
                .addBooksToShoppingCart(email,cartItemRequestDto);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the method throws Exception When
             quantity of request is less or equals then 0
            """)
    public void addBooksToShoppingCart_quantityLessOrEquals0_Success() {
        String email = "test@test.com";
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(2L,-1);

        assertThatThrownBy(() -> shoppingCartService
                .addBooksToShoppingCart(email, cartItemRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("quantity is cannot be less or equal 0");
    }

    @Test
    @DisplayName("""
            Verify that the method throws exception while Book dont exist in DB
            """)
    public void addBooksToShoppingCart_AddingBookWhoDontExistInDB_ThrowsException() {
        String email = "test@test.com";
        User user = new User()
                .setEmail(email)
                .setId(1L)
                .setPassword("password")
                .setFirstName("Robert")
                .setLastName("Martin")
                .setRoles(Set.of(new Role()
                        .setId(1L)
                        .setName(Role.RoleName.USER)))
                .setShippingAddress("adress")
                .setDeleted(false);
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("The Pragmatic Programmer")
                        .setAuthor("Andrew Hunt")
                        .setIsbn("9780201616224")
                        .setPrice(BigDecimal.valueOf(42.99))
                        .setDescription("Classic book on software craftsmanship.")
                        .setCoverImage("https://example.com/images/pragmatic.jpg")
                        .setDeleted(false))
                .setQuantity(3);
        Set<CartItem> setOfCartItem = new HashSet<>();
        setOfCartItem.add(cartItem);
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setCartItems(setOfCartItem)
                .setUser(user);
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(2L,3);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(shoppingCart);
        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> shoppingCartService
                .addBooksToShoppingCart(email, cartItemRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cannot find Book with id:" + cartItemRequestDto.bookId());
    }

    @Test
    @DisplayName("""
            Verify that the method throws EntityNotFoundException when the given User is not found.
            """)
    public void addBooksToShoppingCart_UserNotExist_Success() {
        String email = "test@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(2L,3);

        assertThatThrownBy(() -> shoppingCartService
                .addBooksToShoppingCart(email, cartItemRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cannot find user with email: " + email);
    }

    @Test
    @DisplayName("""
            Verify that the given book is added to given users shoppingCart.
            """)
    public void addBooksToShoppingCart_ValidRequest_CreateNewShoppingCart_Success() {
        String email = "test@test.com";
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(3L,3);
        User user = new User()
                .setEmail(email)
                .setId(1L)
                .setPassword("password")
                .setFirstName("Robert")
                .setLastName("Martin")
                .setRoles(Set.of(new Role()
                        .setId(1L)
                        .setName(Role.RoleName.USER)))
                .setShippingAddress("adress")
                .setDeleted(false);
        Book book = new Book()
                .setId(3L)
                .setTitle("Clean Code")
                .setAuthor("Robert C. Martin")
                .setIsbn("9780132350884")
                .setPrice(BigDecimal.valueOf(37.50))
                .setDescription("A handbook of agile software craftsmanship.")
                .setCoverImage("https://example.com/images/cleancode.jpg");
        CartItemDto cartItemDto = new CartItemDto(1L,3L,"Clean Code",3);
        ShoppingCartDto expected = new ShoppingCartDto(1L,1L,Set.of(cartItemDto));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(null);
        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.of(book));
        doAnswer(c -> {
            ShoppingCart shoppingCart = c.getArgument(2);
            CartItem newCartItem = new CartItem()
                    .setId(1L)
                    .setBook(c.getArgument(0))
                    .setQuantity(c.getArgument(1))
                    .setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(newCartItem);
            return null;
        }).when(cartItemService).save(any(Book.class),anyInt(),any(ShoppingCart.class));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(c -> c.getArgument(0));
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(expected);
        //when
        ShoppingCartDto actual = shoppingCartService
                .addBooksToShoppingCart(email,cartItemRequestDto);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the quantity of given book which
            user already have in shoppingCart is updated.
            """)
    public void addBooksToShoppingCart_AddQuantityToAlreadyExistedBookInShoppingCart_Success() {
        String email = "test@test.com";
        User user = new User()
                .setEmail(email)
                .setId(1L)
                .setPassword("password")
                .setFirstName("Robert")
                .setLastName("Martin")
                .setRoles(Set.of(new Role()
                        .setId(1L)
                        .setName(Role.RoleName.USER)))
                .setShippingAddress("adress")
                .setDeleted(false);
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("The Pragmatic Programmer")
                        .setAuthor("Andrew Hunt")
                        .setIsbn("9780201616224")
                        .setPrice(BigDecimal.valueOf(42.99))
                        .setDescription("Classic book on software craftsmanship.")
                        .setCoverImage("https://example.com/images/pragmatic.jpg")
                        .setDeleted(false))
                .setQuantity(3);
        Set<CartItem> setOfCartItem = new HashSet<>();
        setOfCartItem.add(cartItem);
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setCartItems(setOfCartItem)
                .setUser(user);
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(1L, 2);
        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "The Pragmatic Programmer", 5);
        Set<CartItemDto> setOfCartItemDto1 = Set.of(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(1L, 1L, setOfCartItemDto1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartDto actual = shoppingCartService
                .addBooksToShoppingCart(email, cartItemRequestDto);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the given book quantity is updated in shoppingCart.
            """)
    public void updateShoppingCart_ValidRequest_Success() {
        String email = "test@test.com";
        User user = new User()
                .setEmail(email)
                .setId(1L)
                .setPassword("password")
                .setFirstName("Robert")
                .setLastName("Martin")
                .setRoles(Set.of(new Role()
                        .setId(1L)
                        .setName(Role.RoleName.USER)))
                .setShippingAddress("adress")
                .setDeleted(false);
        CartItem cartItem = new CartItem()
                .setId(1L)
                .setBook(new Book()
                        .setId(1L)
                        .setTitle("The Pragmatic Programmer")
                        .setAuthor("Andrew Hunt")
                        .setIsbn("9780201616224")
                        .setPrice(BigDecimal.valueOf(42.99))
                        .setDescription("Classic book on software craftsmanship.")
                        .setCoverImage("https://example.com/images/pragmatic.jpg")
                        .setDeleted(false))
                .setQuantity(3);
        Set<CartItem> setOfCartItem = new HashSet<>();
        setOfCartItem.add(cartItem);
        ShoppingCart shoppingCart = new ShoppingCart()
                .setId(1L)
                .setCartItems(setOfCartItem)
                .setUser(user);
        CartItemDto cartItemDto = new CartItemDto(1L, 1L, "The Pragmatic Programmer", 5);
        Set<CartItemDto> setOfCartItemDto1 = Set.of(cartItemDto);
        ShoppingCartDto expected = new ShoppingCartDto(1L, 1L, setOfCartItemDto1);

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(shoppingCart);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartDto actual = shoppingCartService.updateShoppingCart(email, 1L,3);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that the method throws exception book is not found in shoppingCart.
            """)
    public void updateShoppingCart_BookNotFoundInShoppingCart_ThrowsException() {
        String email = "test@test.com";
        Long cartItemId = 1L;
        int quantity = 3;

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(new ShoppingCart());

        assertThatThrownBy(() -> shoppingCartService
                .updateShoppingCart(email, cartItemId, quantity))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cannot find Cart item with id: " + cartItemId
                        + " in Your shopping cart.");
    }

    @Test
    @DisplayName("""
            Verify that the method throws exception when quantity is less or equals 0.
            """)
    public void updateShoppingCart_quantityLessOrEquals0_ThrowsException() {
        String email = "test@test.com";
        Long cartItemId = 1L;
        int quantity = -1;

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(null);

        assertThatThrownBy(() -> shoppingCartService
                .updateShoppingCart(email, cartItemId, quantity))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("quantity is cannot be less or equal 0");
    }

    @Test
    @DisplayName("""
            Verify that the method throws exception book is not found in shoppingCart.
            """)
    public void updateShoppingCart_ShoppingCartIsNotFound_ThrowsException() {
        String email = "test@test.com";
        Long cartItemId = 1L;
        int quantity = 3;

        when(shoppingCartRepository.getShoppingCartsByUser(email)).thenReturn(null);

        assertThatThrownBy(() -> shoppingCartService
                .updateShoppingCart(email, cartItemId, quantity))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("cannot find Yours ShoppingCart");
    }
}
