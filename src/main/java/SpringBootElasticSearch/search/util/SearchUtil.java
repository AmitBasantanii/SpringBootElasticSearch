package SpringBootElasticSearch.search.util;

//import SpringBootElasticSearch.search.SearchRequestDTO;
//import SpringBootElasticSearch.search.SortOrder;
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import org.apache.commons.collections4.CollectionUtils;
////import org.elasticsearch.index.query.*;
////import org.elasticsearch.search.builder.SearchSourceBuilder;
////import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.data.elasticsearch.core.query.Criteria;
//
//import java.util.Date;
//import java.util.List;
//
//public final class SearchUtil {
//
//    private SearchUtil() {}
//
//    public static SearchRequest buildSearchRequest(final String indexName, final SearchRequestDTO dto) {
//        try {
//            final int page = dto.getPage();
//            final int size = dto.getSize();
//            final int from = page <= 0 ? 0 : page * size;
//
//            SearchSourceBuilder builder = new SearchSourceBuilder()
//                    .from(from)
//                    .size(size)
//                    .query(getQueryBuilder(dto));
//
//            if (dto.getSortBy() != null) {
//                if (dto.getOrder() != null) builder = builder.sort(
//                        dto.getSortBy(),
//                        dto.getOrder()
//                );
//                else builder = builder.sort(
//                        dto.getSortBy(),
//                        SortOrder.ASCENDING
//                );
//            }
//
//            final SearchRequest request = new SearchRequest(indexName);
//            request.source(builder);
//
//            return request;
//        } catch (final Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static SearchRequest buildSearchRequest(final String indexName,
//                                                   final String field,
//                                                   final Date date) {
//        try {
//            final SearchSourceBuilder builder = new SearchSourceBuilder()
//                    .postFilter(getQueryBuilder(field, date));
//
//            final SearchRequest request = new SearchRequest(indexName);
//            request.source(builder);
//
//            return request;
//        } catch (final Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static SearchRequest buildSearchRequest(final String indexName,
//                                                   final SearchRequestDTO dto,
//                                                   final Date date) {
//        try {
//            final QueryBuilder searchQuery = getQueryBuilder(dto);
//            final QueryBuilder dateQuery = getQueryBuilder("created", date);
//
//            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
//                    .mustNot(searchQuery)
//                    .must(dateQuery);
//
//            SearchSourceBuilder builder = new SearchSourceBuilder()
//                    .postFilter(boolQuery);
//
//            if (dto.getSortBy() != null) {
//                builder = builder.sort(
//                        dto.getSortBy(),
//                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASCENDING
//                );
//            }
//
//            final SearchRequest request = new SearchRequest(indexName);
//            request.source();
//
//            return request;
//        } catch (final Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static MultiMatchQueryBuilder getQueryBuilder(final SearchRequestDTO dto) {
//        if (dto == null) {
//            return QueryBuilders.matchAllQuery();
//        }
//
//        final List<String> fields = dto.getFields();
//        if (CollectionUtils.isEmpty(fields)) {
//            return QueryBuilders.matchAllQuery();
//        }
//
//        if (fields.size() > 1) {
//            final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
//                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
//                    .operator(Criteria.Operator.AND);
//
//            fields.forEach(queryBuilder::field);
//
//            return queryBuilder;
//        }
//
//        return fields.stream()
//                .findFirst()
//                .map(field ->
//                        QueryBuilders.matchQuery(field, dto.getSearchTerm())
//                                .operator(Criteria.Operator.AND))
//                .orElse(QueryBuilders.matchAllQuery());
//    }
//
//    private static QueryBuilder getQueryBuilder(final String field, final Date date) {
//        return QueryBuilders.rangeQuery(field).gte(date);
//    }
//}

import SpringBootElasticSearch.search.SearchRequestDTO;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.json.JsonData;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

public final class SearchUtil {

    private final ElasticsearchClient client;

    public SearchUtil(ElasticsearchClient client) {
        this.client = client;
    }

    public static SearchRequest buildSearchRequest(final String indexName, final SearchRequestDTO dto) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;

            Query query = getQueryBuilder(dto);

            SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
                    .index(indexName)
                    .from(from)
                    .size(size)
                    .query(query);

            if (dto.getSortBy() != null) {
                SortOrder sortOrder = dto.getOrder() != null ? (SortOrder) dto.getOrder() : SortOrder.Asc;
                SortOptions sortOptions = SortOptions.of(so -> so.field(f -> f.field(dto.getSortBy()).order(sortOrder)));
                requestBuilder.sort(sortOptions);
            }

            return requestBuilder.build();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName, final String field, final Date date) {
        try {
            Query query = getQueryBuilder(field, date);

            return new SearchRequest.Builder()
                    .index(indexName)
                    .postFilter(query)
                    .build();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SearchRequest buildSearchRequest(final String indexName, final SearchRequestDTO dto, final Date date) {
        try {
            Query searchQuery = getQueryBuilder(dto);
            Query dateQuery = getQueryBuilder("created", date);

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .must(searchQuery)
                    .must(dateQuery)
            );

            SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
                    .index(indexName)
                    .postFilter(boolQuery._toQuery());

            if (dto.getSortBy() != null) {
                SortOrder sortOrder = dto.getOrder() != null ? (SortOrder) dto.getOrder() : SortOrder.Asc;
                SortOptions sortOptions = SortOptions.of(so -> so.field(f -> f.field(dto.getSortBy()).order(sortOrder)));
                requestBuilder.sort(sortOptions);
            }

            return requestBuilder.build();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Query getQueryBuilder(final SearchRequestDTO dto) {
        if (dto == null) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        final List<String> fields = dto.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        if (fields.size() > 1) {
            return Query.of(q -> q.multiMatch(m -> m
                    .query(dto.getSearchTerm())
                    .fields(fields)
//                    .type(t -> t.crossFields())
            ));
        }

        return fields.stream()
                .findFirst()
                .map(field -> Query.of(q -> q.match(m -> m
                        .field(field)
                        .query(dto.getSearchTerm())
                )))
                .orElse(Query.of(q -> q.matchAll(m -> m)));
    }

    private static Query getQueryBuilder(final String field, final Date date) {
        return Query.of(q -> q.range(r -> r
                .field(field)
                .gte(JsonData.of(date))
        ));
    }
}


