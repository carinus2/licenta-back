package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostSitterEntity;
import com.start.pawpal_finder.representation.SearchPostRepresentation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSitterRepositoryCustom {
    List<PostSitterEntity> searchPosts(SearchPostRepresentation searchPostRepresentation);
}
