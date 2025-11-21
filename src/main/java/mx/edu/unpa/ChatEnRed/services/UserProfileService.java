package mx.edu.unpa.ChatEnRed.services;

import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.UserProfile;

public interface UserProfileService {
    public Iterable<UserProfile> findAll();
    public Optional<UserProfile> findById(Integer userId);
    public UserProfile save(UserProfile profile);
    public void deleteById(Integer userId);
}
