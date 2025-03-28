package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostSitterEntity;
import com.start.pawpal_finder.representation.SearchPostRepresentation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class PostSitterCustomImpl implements PostSitterRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public List<PostSitterEntity> searchPosts(SearchPostRepresentation searchPostRepresentation) {
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT p FROM PostSitterEntity p " +
                        "LEFT JOIN FETCH p.petSitter ps " +
                        "LEFT JOIN FETCH p.availabilities a " +
                        "WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        if (searchPostRepresentation.getKeyword() != null && !searchPostRepresentation.getKeyword().trim().isEmpty()) {
            jpql.append(" AND p.description LIKE :keyword");
            params.put("keyword", "%" + searchPostRepresentation.getKeyword() + "%");
        }
        if (searchPostRepresentation.getStatus() != null && !searchPostRepresentation.getStatus().trim().isEmpty()) {
            jpql.append(" AND p.status = :status");
            params.put("status", searchPostRepresentation.getStatus());
        }
        if (searchPostRepresentation.getTasks() != null
                && !searchPostRepresentation.getTasks().trim().isEmpty()) {
            jpql.append(" AND EXISTS (SELECT t FROM p.tasks t WHERE LOWER(t) LIKE :tasks)");
            params.put("tasks", "%" + searchPostRepresentation.getTasks().toLowerCase() + "%");
        }
        if (searchPostRepresentation.getDayOfWeek() != null && !searchPostRepresentation.getDayOfWeek().isEmpty()) {
            List<java.time.DayOfWeek> days = searchPostRepresentation.getDayOfWeek().stream()
                    .map(java.time.DayOfWeek::valueOf)
                    .collect(Collectors.toList());
            jpql.append(" AND a.dayOfWeek IN :dayOfWeek");
            params.put("dayOfWeek", days);
        }
        if (searchPostRepresentation.getMaxPricePerHour() != null) {
            jpql.append(" AND (p.pricingModel = 'PER_HOUR' AND p.ratePerHour <= :maxPricePerHour)");
            params.put("maxPricePerHour", searchPostRepresentation.getMaxPricePerHour());
        }
        if (searchPostRepresentation.getMaxPricePerDay() != null) {
            jpql.append(" AND (p.pricingModel = 'PER_DAY' AND p.ratePerDay <= :maxPricePerDay)");
            params.put("maxPricePerDay", searchPostRepresentation.getMaxPricePerDay());
        }
        if (searchPostRepresentation.getMaxFlatRate() != null) {
            jpql.append(" AND (p.pricingModel = 'FLAT' AND p.flatRate <= :maxFlatRate)");
            params.put("maxFlatRate", searchPostRepresentation.getMaxFlatRate());
        }


        TypedQuery<PostSitterEntity> query = em.createQuery(jpql.toString(), PostSitterEntity.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

}
