package com.start.pawpal_finder.service;

import com.start.pawpal_finder.dto.AnimalDto;
import com.start.pawpal_finder.entity.AnimalEntity;
import com.start.pawpal_finder.entity.AnimalProjection;
import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.repository.AnimalRepository;
import com.start.pawpal_finder.repository.PetOwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTest {
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private PetOwnerRepository petOwnerRepository;
    @InjectMocks
    private AnimalService animalService;
    private PetOwnerEntity ownerEntity;

    @BeforeEach
    void setUp() {
        ownerEntity = new PetOwnerEntity();
        ownerEntity.setId(1);
    }

    @Test
    void testSaveAnimalSuccess() throws IOException {
        AnimalDto dto = new AnimalDto(null, "Buddy", "Street", "Cute dog", 3, "Labrador", null, 1);
        AnimalEntity entity = new AnimalEntity();
        entity.setPetOwner(ownerEntity);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn("imageBytes".getBytes());

        when(petOwnerRepository.findById(1)).thenReturn(Optional.of(ownerEntity));
        when(animalRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        try (MockedStatic<com.start.pawpal_finder.auth.SecurityConfig> mockedSecurity =
                     mockStatic(com.start.pawpal_finder.auth.SecurityConfig.class)) {
            mockedSecurity.when(com.start.pawpal_finder.auth.SecurityConfig::getAuthenticatedUserId).thenReturn(1);

            AnimalDto result = animalService.saveAnimal(1, dto, file);

            assertEquals("Buddy", result.getName());
            verify(animalRepository, times(1)).save(any());
        }
    }

    @Test
    void testGetAnimalsByOwner() {
        AnimalProjection projection = new AnimalProjection() {
            public Integer getId() { return 1; }
            public String getName() { return "Buddy"; }
            public String getStreet() { return "Street"; }
            public String getDescription() { return "Cute dog"; }
            public Integer getAge() { return 3; }
            public String getBreed() { return "Labrador"; }
            public byte[] getProfilePicture() { return "bytes".getBytes(); }
            public Integer getPetOwnerId() { return 1; }
        };

        when(animalRepository.findByPetOwnerId(1)).thenReturn(Collections.singletonList(projection));

        List<AnimalDto> result = animalService.getAnimalsByOwner(1);

        assertEquals(1, result.size());
        assertEquals("Buddy", result.get(0).getName());
        assertTrue(result.get(0).getProfilePicture().startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void testDeleteAnimal() {
        AnimalEntity animal = new AnimalEntity();
        animal.setId(1);
        PetOwnerEntity owner = new PetOwnerEntity();
        owner.setId(1);
        animal.setPetOwner(owner);

        when(animalRepository.findById(1)).thenReturn(Optional.of(animal));

        try (MockedStatic<com.start.pawpal_finder.auth.SecurityConfig> mockedSecurity =
                     mockStatic(com.start.pawpal_finder.auth.SecurityConfig.class)) {
            mockedSecurity.when(com.start.pawpal_finder.auth.SecurityConfig::getAuthenticatedUserId).thenReturn(1);

            animalService.deleteAnimal(1);

            verify(animalRepository, times(1)).delete(animal);
        }
    }

    @Test
    void testUpdateAnimalSuccess() throws IOException {
        AnimalDto dto = new AnimalDto(null, "Max", "New Street", "Friendly dog", 5, "Beagle", null, 1);
        AnimalEntity existing = new AnimalEntity();
        existing.setId(1);
        existing.setName("Old Name");
        existing.setStreet("Old Street");
        existing.setDescription("Old desc");
        existing.setAge(3);
        existing.setBreed("Old breed");

        PetOwnerEntity owner = new PetOwnerEntity();
        owner.setId(1);
        existing.setPetOwner(owner);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn("newImage".getBytes());

        when(animalRepository.findById(1)).thenReturn(Optional.of(existing));
        when(animalRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        try (MockedStatic<com.start.pawpal_finder.auth.SecurityConfig> mockedSecurity =
                     mockStatic(com.start.pawpal_finder.auth.SecurityConfig.class)) {
            mockedSecurity.when(com.start.pawpal_finder.auth.SecurityConfig::getAuthenticatedUserId).thenReturn(1);

            AnimalDto result = animalService.updateAnimal(1, dto, file);

            assertEquals("Max", result.getName());
            assertEquals("New Street", result.getStreet());
        }
    }

    @Test
    void testCountAnimalsByOwnerId() {
        when(animalRepository.countByPetOwnerId(1)).thenReturn(5L);
        Long count = animalService.countAnimalsByOwnerId(1);
        assertEquals(5L, count);
    }

    @Test
    void testCountAnimalsByOwnerEmail() {
        when(animalRepository.countByOwnerEmail("test@example.com")).thenReturn(3);
        int count = animalService.countAnimalsByOwnerEmail("test@example.com");
        assertEquals(3, count);
    }

}
