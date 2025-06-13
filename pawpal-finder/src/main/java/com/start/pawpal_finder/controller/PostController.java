package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.PostDto;
import com.start.pawpal_finder.representation.SearchOwnerPostRepresentation;
import com.start.pawpal_finder.representation.SearchPostRepresentation;
import com.start.pawpal_finder.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        PostDto createdPost = postService.createPost(postDto);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/active/user/{petOwnerId}")
    public ResponseEntity<List<PostDto>> getActivePostsByUser(@PathVariable Integer petOwnerId) {
        List<PostDto> posts = postService.getActivePostsByUser(petOwnerId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/active/user/{petOwnerId}/count")
    public ResponseEntity<Long> getActivePostCountByUser(@PathVariable Integer petOwnerId) {
        Long count = postService.getActivePostCountByUser(petOwnerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PostDto>> getAllActivePosts() {
        List<PostDto> posts = postService.getAllActivePosts();
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Integer postId, @RequestBody PostDto postDto) {
        PostDto updatedPost = postService.updatePost(postId, postDto);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/city")
    public ResponseEntity<List<PostDto>> getPostsByCity(@RequestParam String city, @RequestParam String county) {
        List<PostDto> posts = postService.getPostsByCityAndCounty(city, county);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/search")
    public ResponseEntity<List<PostDto>> searchOwnerPosts(@RequestBody SearchOwnerPostRepresentation searchPostRepresentation) {
        List<PostDto> posts = postService.searchOwnerPosts(searchPostRepresentation);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{petOwnerId}/status")
    public ResponseEntity<Page<PostDto>> getPostsByUserAndStatus(
            @PathVariable Integer petOwnerId,
            @RequestParam String status,
            @PageableDefault(sort = "startDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<PostDto> page = postService.getPostsByUserAndStatus(petOwnerId, status, pageable);
        return ResponseEntity.ok(page);
    }

}
