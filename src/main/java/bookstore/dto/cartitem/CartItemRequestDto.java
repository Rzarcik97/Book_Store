package bookstore.dto.cartitem;

public record CartItemRequestDto(
        Long bookId,
        int quantity
){
}
