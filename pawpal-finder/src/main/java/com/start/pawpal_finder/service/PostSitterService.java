package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.PostSitterAvailabilityDto;
import com.start.pawpal_finder.dto.PostSitterDto;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.entity.PostSitterAvailabilityEntity;
import com.start.pawpal_finder.entity.PostSitterEntity;
import com.start.pawpal_finder.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostSitterService {

    private final PostSitterRepository postSitterRepository;

    private final PetSitterRepository petSitterRepository;

    private final PostSitterAvailabilityRepository availabilityRepository;
    private final PostSitterRepositoryCustom postSitterCustom;

    public PostSitterService(PostSitterRepository postSitterRepository, PetSitterRepository petSitterRepository, PostSitterAvailabilityRepository availabilityRepository, PostSitterRepositoryCustom postSitterCustom) {
        this.postSitterRepository = postSitterRepository;
        this.petSitterRepository = petSitterRepository;
        this.availabilityRepository = availabilityRepository;
        this.postSitterCustom = postSitterCustom;
    }

    public PostSitterDto createPostSitter(PostSitterDto postSitterDto) {
        PetSitterEntity petSitter = petSitterRepository.findById(postSitterDto.getPetSitterId())
                .orElseThrow(() -> new RuntimeException("Pet Sitter not found with ID: " + postSitterDto.getPetSitterId()));

        PostSitterEntity postSitterEntity = Transformer.toEntity(postSitterDto, petSitter);
        PostSitterEntity savedPost = postSitterRepository.save(postSitterEntity);

        List<PostSitterAvailabilityEntity> availabilityEntities = postSitterDto.getAvailability().stream()
                .map(dto -> {
                    PostSitterAvailabilityEntity entity = Transformer.toEntity(dto);
                    entity.setPostSitter(savedPost);
                    return entity;
                })
                .collect(Collectors.toList());

        availabilityRepository.saveAll(availabilityEntities);

        return Transformer.toDto(savedPost, availabilityEntities);
    }

    public long getPostCountBySitter(Integer sitterId) {
        return postSitterRepository.countByPetSitter_Id(sitterId);
    }
    public List<PostSitterDto> getActiveSitterPostsBySitterId(Integer sitterId) {
        List<PostSitterEntity> activePosts = postSitterRepository.findByPetSitter_IdAndStatus(sitterId, "Active");

        return activePosts.stream()
                .map(post -> {
                    List<PostSitterAvailabilityEntity> availability = availabilityRepository.findByPostSitter(post);
                    return Transformer.toDto(post, availability);
                })
                .collect(Collectors.toList());
    }

    public List<PostSitterDto> getAllSitterPosts() {
        return postSitterRepository.findAll().stream()
                .map(post -> {
                    List<PostSitterAvailabilityEntity> availability = availabilityRepository.findByPostSitter(post);
                    return Transformer.toDto(post, availability);
                })
                .collect(Collectors.toList());
    }

    public List<PostSitterDto> getSitterPostsByLocation(String city, String county) {
        return postSitterRepository.findByPetSitter_CityAndPetSitter_County(city, county)
                .stream()
                .map(post -> {
                    List<PostSitterAvailabilityEntity> availability = availabilityRepository.findByPostSitter(post);
                    return Transformer.toDto(post, availability);
                })
                .collect(Collectors.toList());
    }

    public PostSitterDto getSitterPostById(Integer postId) {
        PostSitterEntity post = postSitterRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        List<PostSitterAvailabilityEntity> availability = availabilityRepository.findByPostSitter(post);
        return Transformer.toDto(post, availability);
    }

    public PostSitterDto updatePostSitter(Integer postId, PostSitterDto updatedDto) {
        PostSitterEntity existingPost = postSitterRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        existingPost.setDescription(updatedDto.getDescription());
        existingPost.setStatus(updatedDto.getStatus());
        existingPost.setPostDate(updatedDto.getPostDate());
        existingPost.setTasks(updatedDto.getTasks());

        if (!updatedDto.getAvailability().isEmpty()) {
            existingPost.setAvailabilityStart(updatedDto.getAvailability().getFirst().getStartTime());
            existingPost.setAvailabilityEnd(updatedDto.getAvailability().getLast().getEndTime());
        }

        PostSitterEntity updatedPost = postSitterRepository.save(existingPost);

        availabilityRepository.deleteByPostSitter(existingPost);
        List<PostSitterAvailabilityEntity> updatedAvailability = updatedDto.getAvailability().stream()
                .map(dto -> {
                    PostSitterAvailabilityEntity entity = Transformer.toEntity(dto);
                    entity.setPostSitter(updatedPost);
                    return entity;
                })
                .collect(Collectors.toList());

        availabilityRepository.saveAll(updatedAvailability);

        return Transformer.toDto(updatedPost, updatedAvailability);
    }
    @Transactional
    public void deletePostSitter(Integer postId) {
        PostSitterEntity post = postSitterRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        post.getAvailabilities().clear();
        postSitterRepository.delete(post);
    }

    public List<PostSitterDto> searchPosts(String keyword, String status, String tasks, String dayOfWeek) {
        List<PostSitterEntity> posts = postSitterCustom.searchPosts(keyword, status, tasks, dayOfWeek);

        // Convert each PostSitterEntity to PostSitterDto
        return posts.stream()
                .map(post -> {

                    List<PostSitterAvailabilityDto> availabilityDtos =
                            post.getAvailabilities() == null
                                    ? Collections.emptyList()
                                    : post.getAvailabilities().stream()
                                    .map(av -> new PostSitterAvailabilityDto(
                                            av.getDayOfWeek(),
                                            av.getStartTime(),
                                            av.getEndTime()
                                    ))
                                    .collect(Collectors.toList());

                    return new PostSitterDto(
                            post.getId(),
                            post.getPetSitter() == null ? null : post.getPetSitter().getId(),
                            post.getDescription(),
                            post.getStatus(),
                            post.getPostDate(),
                            post.getTasks(),
                            availabilityDtos
                    );
                })
                .collect(Collectors.toList());
    }

}
