package com.start.pawpal_finder.entity;

public interface AnimalProjection {
    Integer getId();
    String getName();
    String getStreet();
    String getBreed();
    Integer getAge();
    String getDescription();
    byte[] getProfilePicture();
    Integer getPetOwnerId();
}

