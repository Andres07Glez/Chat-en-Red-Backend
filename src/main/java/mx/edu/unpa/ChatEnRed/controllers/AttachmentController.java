package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.AttachmentResponse;
=======
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response.AttachmentResponse;
>>>>>>> origin/JoseBranch
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.services.AttachmentService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @GetMapping(path = "/app")
	ResponseEntity<List<AttachmentResponse>> findAll() {
		return Optional
                .of(this.attachmentService.findAll())
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
		
	}

    @GetMapping("/fnd")
    public ResponseEntity<AttachmentResponse> findById(@RequestParam("id") Integer attachmentId) {
        return attachmentService.findById(attachmentId)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<AttachmentResponse> save(@RequestBody AttachmentRequest request) {
        return attachmentService.save(request)
                .map(att -> ResponseEntity.ok().body(att))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer attachmentId) {
        return attachmentService.deleteById(attachmentId)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AttachmentResponse> update(
            @PathVariable(value = "id") Integer attachmentId,
            @RequestBody AttachmentRequest request) {
        return attachmentService.update(attachmentId, request)
                .map(att -> ResponseEntity.ok().body(att))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}