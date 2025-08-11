package dev.library.core.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Построение запроса в виде Specification для Spring Data
 * @param <T> - тип данных
 */
@Component
public class SpecificationBuilder<T> {
    public Specification<T> equal(String column, Object value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(column), value);
    }

    public Specification<T> like(String column, String value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(column), "%".concat(value).concat("%"));
    }

    public Specification<T> greaterThanOrEqualToDate(String column, OffsetDateTime value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(column), value);
    }

    public Specification<T> lessThanOrEqualToDate(String column, OffsetDateTime value) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get(column), value);
    }

    public Specification<T> between(String column, OffsetDateTime from, OffsetDateTime to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(column), from, to);
    }

    public Specification<T> orderByAsc(String column, Specification<T> specification) {
        return (root, query, criteriaBuilder) -> {
            Objects.requireNonNull(query).orderBy(criteriaBuilder.asc(root.get(column)));
            return specification.toPredicate(root, query, criteriaBuilder);
        };
    }

    public Specification<T> orderByDesc(String column, Specification<T> specification) {
        return (root, query, criteriaBuilder) -> {
            Objects.requireNonNull(query).orderBy(criteriaBuilder.desc(root.get(column)));
            return specification.toPredicate(root, query, criteriaBuilder);
        };
    }

    public Specification<T> emptySpecification() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();
    }
}