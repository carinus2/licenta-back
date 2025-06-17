package com.start.pawpal_finder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.AnimalRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnimalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer testOwnerId;

    @BeforeEach
    void setup() {
        animalRepository.deleteAll();
        petOwnerRepository.deleteAll();

        PetOwnerEntity owner = new PetOwnerEntity();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@example.com");
        owner.setPassword("password");
        owner.setPhoneNumber("1234567890");
        owner.setCity("Cluj");
        owner.setCounty("Cluj");

        petOwnerRepository.save(owner);

        testOwnerId = owner.getId();
    }

    @Test
    void testSaveAnimal() throws Exception {
        AnimalDto animalDto = new AnimalDto(null, "Max", "Main Street", "Friendly", 5, "Beagle", null, testOwnerId);
        MockMultipartFile jsonPart = new MockMultipartFile("animalDto", "", "application/json",
                objectMapper.writeValueAsBytes(animalDto));
        MockMultipartFile imgPart = new MockMultipartFile("profilePicture", "image.jpg", "image/jpeg", "dummy".getBytes());

        mockMvc.perform(multipart("/api/animals/save/" + testOwnerId)
                        .file(jsonPart)
                        .file(imgPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.breed").value("Beagle"));
    }

    @Test
    void testGetAnimalsByOwnerId() throws Exception {
        testSaveAnimal();

        mockMvc.perform(get("/api/animals/owner/" + testOwnerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    void testGetAnimalCount() throws Exception {
        testSaveAnimal();

        mockMvc.perform(get("/api/animals/count/" + testOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testUpdateAnimal() throws Exception {
        AnimalDto animalDto = new AnimalDto(null, "Max", "Street", "Desc", 5, "Breed", null, testOwnerId);
        MockMultipartFile jsonPart = new MockMultipartFile("animalDto", "", "application/json",
                objectMapper.writeValueAsBytes(animalDto));
        MockMultipartFile imgPart = new MockMultipartFile("profilePicture", "image.jpg", "image/jpeg", "bytes".getBytes());

        // Save animal
        String response = mockMvc.perform(multipart("/api/animals/save/" + testOwnerId)
                        .file(jsonPart)
                        .file(imgPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse().getContentAsString();

        AnimalDto savedAnimal = objectMapper.readValue(response, AnimalDto.class);

        AnimalDto updatedAnimal = new AnimalDto(null, "Rex", "Other Street", "Updated", 6, "Husky", null, testOwnerId);
        MockMultipartFile jsonPartUpdate = new MockMultipartFile("animalDto", "", "application/json",
                objectMapper.writeValueAsBytes(updatedAnimal));

        mockMvc.perform(multipart("/api/animals/" + savedAnimal.getId())
                        .file(jsonPartUpdate)
                        .file(imgPart)
                        .with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rex"))
                .andExpect(jsonPath("$.breed").value("Husky"));
    }

    @Test
    void testDeleteAnimal() throws Exception {
        testSaveAnimal();

        Integer animalId = animalRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/animals/" + animalId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/animals/count/" + testOwnerId))
                .andExpect(content().string("0"));
    }
}
