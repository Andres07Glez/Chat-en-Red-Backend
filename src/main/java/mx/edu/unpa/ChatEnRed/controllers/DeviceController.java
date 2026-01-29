package mx.edu.unpa.ChatEnRed.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.DTOs.Device.Request.DeviceRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Device.Response.DeviceResponse;
import mx.edu.unpa.ChatEnRed.services.DeviceService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping(path = "/app")
    ResponseEntity<List<DeviceResponse>> findAll(){
    	return Optional.of(this.deviceService.findAll())
    			.map(ResponseEntity::ok)
    			.orElseGet(ResponseEntity.notFound()::build);
    }
    

    @GetMapping("/fnd")
    public ResponseEntity<DeviceResponse> findById(@RequestParam("id") Integer id) {
        return deviceService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/create")
    public ResponseEntity<DeviceResponse> save(@RequestBody DeviceRequest request) {
        return deviceService.save(request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Integer id) {
        return deviceService.deleteById(id)
                .map(deleted -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DeviceResponse> update(
            @PathVariable("id") Integer id,
            @RequestBody DeviceRequest request) {
        return deviceService.update(id, request)
                .map(resp -> ResponseEntity.ok().body(resp))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
