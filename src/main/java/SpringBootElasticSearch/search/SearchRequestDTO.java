package SpringBootElasticSearch.search;

import java.util.List;
import org.springframework.data.domain.Sort;
//import org.elasticsearch.search.sort.SortOrder;

public class SearchRequestDTO extends PagedRequestDTO {
    private List<String> fields;
    private String searchTerm;
    private String sortBy;
    private Sort.Direction order;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Enum<? extends Enum<?>> getOrder() {
        return order;
    }

    public void setOrder(Sort.Direction order) {
        this.order = order;
    }
}