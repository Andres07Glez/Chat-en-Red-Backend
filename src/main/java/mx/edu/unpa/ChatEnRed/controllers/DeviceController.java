package mx.edu.unpa.ChatEnRed.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mx.edu.unpa.ChatEnRed.domains.Device;
import mx.edu.unpa.ChatEnRed.services.DeviceService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping(path = "/app")
    public Iterable<Device> index() {
        return this.deviceService.findAll();
    }

    @GetMapping("/fnd")
    public ResponseEntity<?> read(@RequestParam("id") int deviceId) {
        Optional<Device> o = this.deviceService.findById(deviceId);
        if (o.isPresent()) {
            LinkedList<Device> list = new LinkedList<>();
            list.add(o.get());
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device no encontrado");
        }
    }

    // Listar dispositivos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listByUser(@PathVariable("userId") int userId) {
        List<Device> devices = this.deviceService.findByUserId(userId);
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/create")
    public ResponseEntity<Device> create(@RequestBody Device device) {
        Device saved = this.deviceService.save(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/save")
    public ResponseEntity<Device> save(@RequestBody Device device) {
        if (device != null) {
            Device saved = this.deviceService.save(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int deviceId) {
        Optional<Device> o = this.deviceService.findById(deviceId);
        if (o.isPresent()) {
            this.deviceService.deleteById(deviceId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
