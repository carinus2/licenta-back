package com.start.pawpal_finder.controller;

import com.start.pawpal_finder.dto.PostSitterDto;
import com.start.pawpal_finder.representation.SearchPostRepresentation;
import com.start.pawpal_finder.service.PostSitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-sitter")
@CrossOrigin(origins = "*")
public class PostSitterController {

    private final PostSitterService postSitterService;

    public PostSitterController(PostSitterService postSitterService) {
        this.postSitterService = postSitterService;
    }

    @PostMapping("/create")
    public ResponseEntity<PostSitterDto> createPostSitter(@RequestBody PostSitterDto postSitterDto) {
        return ResponseEntity.ok(postSitterService.createPostSitter(postSitterDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostSitterDto>> getAllSitterPosts() {
        return ResponseEntity.ok(postSitterService.getAllSitterPosts());
    }

    @GetMapping("/location")
    public ResponseEntity<List<PostSitterDto>> getSitterPostsByLocation(
            @RequestParam String city, @RequestParam String county) {
        return ResponseEntity.ok(postSitterService.getSitterPostsByLocation(city, county));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostSitterDto> getSitterPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(postSitterService.getSitterPostById(postId));
    }

    @GetMapping("/active-posts/{sitterId}")
    public ResponseEntity<List<PostSitterDto>> getSitterActivePostById(@PathVariable Integer sitterId) {
        return ResponseEntity.ok(postSitterService.getActiveSitterPostsBySitterId(sitterId));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostSitterDto> updatePostSitter(
            @PathVariable Integer postId, @RequestBody PostSitterDto postSitterDto) {
        return ResponseEntity.ok(postSitterService.updatePostSitter(postId, postSitterDto));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePostSitter(@PathVariable Integer postId) {
        postSitterService.deletePostSitter(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPostCountBySitter(@RequestParam Integer sitterId) {
        long count = postSitterService.getPostCountBySitter(sitterId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/search")
    public ResponseEntity<List<PostSitterDto>> searchPosts(@RequestBody SearchPostRepresentation searchPostRepresentation) {

        List<PostSitterDto> posts = postSitterService.searchPosts(searchPostRepresentation);
        return ResponseEntity.ok(posts);
    }
}
