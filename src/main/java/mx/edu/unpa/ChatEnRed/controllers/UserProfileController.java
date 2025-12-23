package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request.UserProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response.UserProfileResponse;
import mx.edu.unpa.ChatEnRed.services.UserProfileService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/user-profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

	@GetMapping(path = "/app")
	ResponseEntity<List<UserProfileResponse>> findAll() {
		return Optional
                .of(this.userProfileService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}

    @GetMapping("/fnd")
    public ResponseEntity<UserProfileResponse> findById(@RequestParam("userId") Integer userId) {
        return userProfileService.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<UserProfileResponse> save(@RequestBody UserProfileRequest request) {
        return userProfileService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Integer userId) {
        return userProfileService.deleteById(userId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserProfileResponse> update(
            @PathVariable("userId") Integer userId,
            @RequestBody UserProfileRequest request) {
        return userProfileService.update(userId, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}