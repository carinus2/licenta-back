package com.start.pawpal_finder.service;

import com.start.pawpal_finder.Transformer;
import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.dto.PostDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PostEntity;
import com.start.pawpal_finder.repository.AnimalRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import com.start.pawpal_finder.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PetOwnerRepository petOwnerRepository;
    private final AnimalRepository animalRepository;
    private final PostRepository postRepository;

    public PostService(PetOwnerRepository petOwnerRepository, AnimalRepository animalRepository, PostRepository postRepository) {
        this.petOwnerRepository = petOwnerRepository;
        this.animalRepository = animalRepository;
        this.postRepository = postRepository;
    }

    public PostDto createPost(PostDto postDto) {
        PetOwnerEntity petOwner = petOwnerRepository.findById(postDto.getPetOwnerId())
                .orElseThrow(() -> new RuntimeException("Pet Owner not found"));

        List<AnimalEntity> animals = animalRepository.findAllById(
                postDto.getAnimals().stream().map(AnimalDto::getId).collect(Collectors.toList())
        );

        PostEntity newPost = Transformer.fromDto(postDto, petOwner, animals);
        newPost.setAnimals(animals);
        PostEntity savedPost = postRepository.save(newPost);

        return Transformer.toDto(savedPost);
    }

    public List<PostDto> getAllActivePosts() {
        List<PostEntity> activePosts = postRepository.findByStatus("Active");
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

        List<AnimalEntity> animals = animalRepository.findAllById(
                postDto.getAnimals().stream().map(AnimalDto::getId).collect(Collectors.toList())
        );

        existingPost.setTitle(postDto.getTitle());
        existingPost.setDescription(postDto.getDescription());
        existingPost.setStartDate(postDto.getStartDate());
        existingPost.setEndDate(postDto.getEndDate());
        existingPost.setNumber(postDto.getNumber());
        existingPost.setStreet(postDto.getStreet());
        existingPost.setTasks(postDto.getTasks().stream().map(Transformer::fromDto).toList());
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

        postRepository.delete(post);
    }

    public List<PostDto> getActivePostsByUser(Integer petOwnerId) {
        List<PostEntity> activePosts = postRepository.findByPetOwnerIdAndStatus(petOwnerId, "Active");
        return activePosts.stream().map(Transformer::toDto).collect(Collectors.toList());
    }

    public Long getActivePostCountByUser(Integer petOwnerId) {
        return postRepository.countByPetOwnerIdAndStatus(petOwnerId, "Active");
    }


}
