package bookstore.dto;

import java.util.ArrayList;
import java.util.List;

public record BookSearchParametersDto(
        List<String> titles,
        List<String> authors,
        List<String> isbns) {

    public BookSearchParametersDto {
        if (titles == null) {
            titles = new ArrayList<>();
        }
        if (authors == null) {
            authors = new ArrayList<>();
        }
        if (isbns == null) {
            isbns = new ArrayList<>();
        }
    }
}
