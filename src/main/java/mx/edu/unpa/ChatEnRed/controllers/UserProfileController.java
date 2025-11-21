package mx.edu.unpa.ChatEnRed.controllers;

import java.util.LinkedList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.services.UserProfileService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/user-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService profileService;

    @GetMapping(path = "/app")
    public Iterable<UserProfile> index() {
        return this.profileService.findAll();
    }

    @GetMapping("/fnd")
    public ResponseEntity<?> read(@RequestParam("id") Integer userId) {
        Optional<UserProfile> o = this.profileService.findById(userId);
        if (o.isPresent()) {
            LinkedList<UserProfile> list = new LinkedList<>();
            list.add(o.get());
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Perfil no encontrado");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<UserProfile> create(@RequestBody UserProfile profile) {
        UserProfile saved = this.profileService.save(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/upd/{id}")
    public ResponseEntity<UserProfile> upd(@PathVariable(value = "id") Integer userId) {
        Optional<UserProfile> o = this.profileService.findById(userId);
        if (o.isPresent()) {
            return ResponseEntity.ok(o.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/save")
    public ResponseEntity<UserProfile> save(@RequestBody UserProfile profile) {
        if (profile != null) {
            UserProfile saved = this.profileService.save(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer userId) {
        Optional<UserProfile> o = this.profileService.findById(userId);
        if (o.isPresent()) {
            this.profileService.deleteById(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
