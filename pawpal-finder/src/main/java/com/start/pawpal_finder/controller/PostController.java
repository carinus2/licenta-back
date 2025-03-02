package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.PostDto;
import com.start.pawpal_finder.service.PostService;
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
}
