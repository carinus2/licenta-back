package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.dto.PostDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PostEntity;
import com.start.pawpal_finder.entity.TaskEntity;
import com.start.pawpal_finder.repository.*;
import com.start.pawpal_finder.representation.SearchOwnerPostRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PetOwnerRepository petOwnerRepository;
    private final AnimalRepository animalRepository;
    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final InterestReservationRepository interestReservationRepository;
    @Autowired
    public PostService(PetOwnerRepository petOwnerRepository, AnimalRepository animalRepository, PostRepository postRepository, PostRepositoryCustom postRepositoryCustom, InterestReservationRepository interestReservationRepository) {
        this.petOwnerRepository = petOwnerRepository;
        this.animalRepository = animalRepository;
        this.postRepository = postRepository;
        this.postRepositoryCustom = postRepositoryCustom;
        this.interestReservationRepository = interestReservationRepository;
    }

    public PostDto createPost(PostDto postDto) {
        PetOwnerEntity petOwner = petOwnerRepository.findById(postDto.getPetOwnerId())
                .orElseThrow(() -> new RuntimeException("Pet Owner not found"));

        Set<AnimalEntity> animals = new HashSet<>(
                animalRepository.findAllById(
                        postDto.getAnimals().stream()
                                .map(AnimalDto::getId)
                                .collect(Collectors.toList())
                )
        );

        PostEntity newPost = Transformer.fromDto(postDto, petOwner, animals);
        newPost.setAnimals(animals);
        PostEntity savedPost = postRepository.save(newPost);

        return Transformer.toDto(savedPost);
    }

    public List<PostDto> getAllActivePosts() {
        List<PostEntity> activePosts = postRepository.findByStatus("ACTIVE");
        return activePosts.stream().map(Transformer::toDto).collect(Collectors.toList());
    }

    public PostDto getPostById(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return Transformer.toDto(post);
    }

    public PostDto updatePost(Integer postId, PostDto postDto) {

        PostEntity existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PetOwnerEntity petOwner = petOwnerRepository.findById(postDto.getPetOwnerId())
                .orElseThrow(() -> new RuntimeException("Pet Owner not found"));

        Set<AnimalEntity> animals = new HashSet<>(
                animalRepository.findAllById(
                        postDto.getAnimals().stream()
                                .map(AnimalDto::getId)
                                .collect(Collectors.toList())
                )
        );

        existingPost.setTitle(postDto.getTitle());
        existingPost.setDescription(postDto.getDescription());
        existingPost.setStartDate(postDto.getStartDate());
        existingPost.setEndDate(postDto.getEndDate());
        existingPost.setNumber(postDto.getNumber());
        existingPost.setStreet(postDto.getStreet());

        existingPost.getTasks().clear();
        List<TaskEntity> newTasks = postDto.getTasks().stream()
                .map(Transformer::fromDto)
                .toList();
        newTasks.forEach(task -> {
            task.setPost(existingPost);
            existingPost.getTasks().add(task);
        });

        existingPost.setStatus(postDto.getStatus());
        existingPost.setPetOwner(petOwner);
        existingPost.setNotes(postDto.getNotes());
        existingPost.setAnimals(animals);

        PostEntity updatedPost = postRepository.save(existingPost);
        return Transformer.toDto(updatedPost);
    }


    public void deletePost(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        interestReservationRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    public List<PostDto> getActivePostsByUser(Integer petOwnerId) {
        List<PostEntity> activePosts = postRepository.findByPetOwnerIdAndStatus(petOwnerId, "ACTIVE");
        return activePosts.stream().map(Transformer::toDto).toList();
    }

    public Long getActivePostCountByUser(Integer petOwnerId) {
        return postRepository.countByPetOwnerIdAndStatus(petOwnerId, "ACTIVE");
    }

    public List<PostDto> getPostsByCityAndCounty(String city, String county) {
        return postRepository.findByPetOwner_CityAndPetOwner_CountyAndStatus(city, county, "ACTIVE").stream().map(Transformer::toDto).toList();
    }

    public List<PostDto> searchOwnerPosts(SearchOwnerPostRepresentation searchPostRepresentation) {
        List<PostEntity> posts = postRepositoryCustom.searchOwnerPosts(searchPostRepresentation);

        return posts.stream()
                .map(Transformer::toDto)
                .collect(Collectors.toList());
    }

    public Page<PostDto> getPostsByUserAndStatus(
            Integer petOwnerId,
            String status,
            Pageable pageable
    ) {
        return postRepository
                .findByPetOwnerIdAndStatus(petOwnerId, status, pageable)
                .map(Transformer::toDto);
    }

}
