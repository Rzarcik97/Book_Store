package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.order.StatusDto;
import bookstore.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface StatusMapper {

    StatusDto toDto(Order order);
}
