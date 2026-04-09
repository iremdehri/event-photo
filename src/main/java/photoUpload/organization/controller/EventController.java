package photoUpload.organization.controller;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import photoUpload.organization.model.Event;
import photoUpload.organization.model.User;
import photoUpload.organization.repository.EventRepository;
import photoUpload.organization.repository.UserRepository;
import photoUpload.organization.service.EventService;
import photoUpload.organization.service.FileService;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final FileService fileService;
    private final UserRepository userRepository;

    @Value("${server.url}")
    private String serverUrl;

    public EventController(EventService eventService, EventRepository eventRepository, FileService fileService, UserRepository userRepository){
        this.eventService=eventService;
        this.eventRepository=eventRepository;
        this.fileService=fileService;
        this.userRepository=userRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Event>> getUserEvents(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getActiveEventsByUserId(userId));
    }

    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEvent(
            @RequestParam("name") String name,
            @RequestParam("userId") Long userId,
            @RequestParam("subTitle") String subTitle, // Alt başlığı unutma
            @RequestParam("image") MultipartFile file) {

        String fileName = fileService.saveFile(file);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Event event = new Event();
        event.setName(name);
        event.setSubTitle(subTitle);
        event.setUser(user);
        event.setUuid(UUID.randomUUID().toString());
        event.setCoverUri(serverUrl + "/uploads/events/" + fileName);
        return ResponseEntity.ok(eventRepository.save(event));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Event> getEventByUuid(@PathVariable String uuid) {
        try {
            return ResponseEntity.ok(eventService.getEventByUuid(uuid));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<?> deleteMultiple(@RequestBody List<String> uuids) {
        try {
            eventService.deleteMultipleByUuids(uuids);
            return ResponseEntity.ok("Albumler basariyla gorunmez yapildi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<?> deleteEvent(@PathVariable String uuid) {
        try {
            eventService.deleteEventByUuid(uuid);
            return ResponseEntity.ok("Album basariyla gorunmez yapildi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
