package mx.edu.unpa.ChatEnRed.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.repositories.UserProfileRepository;
import mx.edu.unpa.ChatEnRed.services.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository repo;

    @Override
    public Iterable<UserProfile> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<UserProfile> findById(Integer userId) {
        return repo.findById(userId);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        return repo.save(profile);
    }

    @Override
    public void deleteById(Integer userId) {
        repo.deleteById(userId);
    }
}
