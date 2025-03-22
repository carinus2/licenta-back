package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostSitterEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class PostSitterCustomImpl implements PostSitterRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PostSitterEntity> searchPosts(String keyword, String status, String tasks, String dayOfWeek) {
        // Build a JPQL query that fetches the relationships you need
        // - petSitter (ManyToOne)
        // - availabilities (OneToMany)
        // - tasks (ElementCollection)
        // "SELECT DISTINCT p" helps avoid duplicate PostSitterEntity rows
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT p FROM PostSitterEntity p " +
                        "LEFT JOIN FETCH p.petSitter ps " +
                        "LEFT JOIN FETCH p.availabilities a " +
                        // remove the JOIN FETCH for tasks
                        "WHERE 1=1 "
        );

        Map<String, Object> params = new HashMap<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND p.description LIKE :keyword");
            params.put("keyword", "%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            jpql.append(" AND p.status = :status");
            params.put("status", status);
        }
        if (tasks != null && !tasks.trim().isEmpty()) {
            // Because tasks is a collection of strings, you can do partial matching with "LIKE"
            // or do an exact match with "= :tasks". Here we do partial matching:
            jpql.append(" AND t LIKE :tasks");
            params.put("tasks", "%" + tasks + "%");
        }
        if (dayOfWeek != null && !dayOfWeek.trim().isEmpty()) {
            jpql.append(" AND a.dayOfWeek = :dayOfWeek");
            params.put("dayOfWeek", java.time.DayOfWeek.valueOf(dayOfWeek));
        }



        // Create a TypedQuery from the JPQL string
        TypedQuery<PostSitterEntity> query = em.createQuery(jpql.toString(), PostSitterEntity.class);

        // Bind each parameter
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // Execute and return results
        return query.getResultList();
    }
}
