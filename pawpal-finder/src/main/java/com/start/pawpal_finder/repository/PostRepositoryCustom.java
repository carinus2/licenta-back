package com.start.pawpal_finder.repository;

import com.start.pawpal_finder.entity.PostEntity;
import com.start.pawpal_finder.representation.SearchOwnerPostRepresentation;

import java.util.List;

public interface PostRepositoryCustom {
    List<PostEntity> searchOwnerPosts(SearchOwnerPostRepresentation search);
}
