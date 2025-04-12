package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostEntity;
import com.start.pawpal_finder.representation.SearchOwnerPostRepresentation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PostEntity> searchOwnerPosts(SearchOwnerPostRepresentation search) {
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT p FROM PostEntity p " +
                        "LEFT JOIN FETCH p.petOwner po " +
                        "LEFT JOIN FETCH p.tasks t " +
                        "WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
            jpql.append(" AND LOWER(p.description) LIKE :keyword");
            params.put("keyword", "%" + search.getKeyword().toLowerCase() + "%");
        }

        if (search.getStatus() != null && !search.getStatus().isEmpty()) {
            jpql.append(" AND LOWER(p.status) = LOWER(:status)");
            params.put("status", search.getStatus());
        }

        if (search.getTask() != null && !search.getTask().isEmpty()) {
            jpql.append(" AND EXISTS (SELECT t1 FROM p.tasks t1 WHERE LOWER(t1.description) LIKE :task)");
            params.put("task", "%" + search.getTask().toLowerCase() + "%");
        }

        if (search.getCity() != null && !search.getCity().isEmpty()) {
            jpql.append(" AND LOWER(po.city) = LOWER(:city)");
            params.put("city", search.getCity());
        }

        if (search.getCounty() != null && !search.getCounty().isEmpty()) {
            jpql.append(" AND LOWER(po.county) = LOWER(:county)");
            params.put("county", search.getCounty());
        }

        TypedQuery<PostEntity> query = em.createQuery(jpql.toString(), PostEntity.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }
}
