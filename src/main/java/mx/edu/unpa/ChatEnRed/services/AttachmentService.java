package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.domains.Attachment;
<<<<<<< HEAD
import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.AttachmentResponse;
=======
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response.AttachmentResponse;
>>>>>>> origin/JoseBranch

public interface AttachmentService {
    List<AttachmentResponse> findAll();
    Optional<AttachmentResponse> findById(Integer id);
    Optional<AttachmentResponse> save(AttachmentRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<AttachmentResponse> update(Integer id, AttachmentRequest request);
}


