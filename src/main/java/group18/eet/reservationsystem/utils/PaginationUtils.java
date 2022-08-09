package group18.eet.reservationsystem.utils;



import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public class PaginationUtils {

    /**
     *
     * @param page the page we want to query
     * @param size the size of the page
     * @param sort a sorting list which should like like this - [ "sortField;sortDirection" ] - [ "from;ASC", "status;DESC" ]
     * @return
     */
    public static PageRequest createPaginationAndSorting(Integer page, Integer size, List<String> sort) {
        if (page == null || size == null) return null;

        Sort pageRequestSort = Sort.by(sort.stream().map(item -> {
            String[] sortBy = item.split(";");
            return sortBy[1].equals("ASC") ? Sort.Order.asc(sortBy[0]) : Sort.Order.desc(sortBy[0]);
        }).collect(Collectors.toList()));

       return PageRequest.of(page - 1, size, pageRequestSort);
    }

}
