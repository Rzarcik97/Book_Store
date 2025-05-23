package bookstore.mapper;

import bookstore.config.MapperConfig;
import bookstore.dto.category.CategoryDto;
import bookstore.dto.category.CreateCategoryDto;
import bookstore.model.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryDto createCategoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(CreateCategoryDto dto, @MappingTarget Category entity);
}
