package photoUpload.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import photoUpload.organization.model.Event;
import photoUpload.organization.model.Photo;
import photoUpload.organization.repository.EventRepository; // Import ekledik
import photoUpload.organization.repository.PhotoRepository;
import photoUpload.organization.service.FileService;
import photoUpload.organization.service.PhotoService;

import java.io.IOException;
import java.util.List;

@CrossOrigin((origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    @Autowired
    private final PhotoService photoService;
    private final FileService fileService;
    private final EventRepository eventRepository;
    private final PhotoRepository photoRepository;

    @Value("${server.url}") // application.properties'den IP çekiyoruz
    private String serverUrl;

    public PhotoController(PhotoService photoService, FileService fileService, EventRepository eventRepository, PhotoRepository photoRepository) {
        this.photoService = photoService;
        this.fileService = fileService;
        this.eventRepository = eventRepository;
        this.photoRepository=photoRepository;
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Photo>> getPhotosByEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(photoRepository.findByEventId(eventId));
    }

    @PostMapping("/upload")
    public ResponseEntity<Photo> uploadPhoto(@RequestParam("file") MultipartFile file,
                                             @RequestParam("eventId") Long eventId) throws IOException {
        return ResponseEntity.ok(photoService.savePhotoToEvent(file, eventId));
    }

    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEvent(
            @RequestParam("name") String name,
            @RequestParam("subTitle") String subTitle, // Frontend'den gelen alt başlık
            @RequestParam("userId") Long userId,
            @RequestParam("image") MultipartFile file) {

        String fileName = fileService.saveFile(file);

        Event event = new Event();
        event.setName(name);
        event.setSubTitle(subTitle);

        event.setCoverUri(serverUrl + "/uploads/events/" + fileName);

        return ResponseEntity.ok(eventRepository.save(event));
    }

    @DeleteMapping("/delete-multiple")
    public ResponseEntity<?> deletePhotos(@RequestBody List<Long> photoIds){
        photoService.deleteMultiplePhotos(photoIds);
        return ResponseEntity.ok("Seçilen fotoğraflar silindi.");
    }

    @PostMapping("/public/upload/{uuid}")
    public ResponseEntity<?> uploadGuestPhotos(
            @PathVariable String uuid,
            @RequestParam("files") MultipartFile[] files) {

        try {
            // Service katmanında UUID ile Event'i bulup fotoğrafları kaydediyoruz
            photoService.savePhotosForEvent(uuid, files);
            return ResponseEntity.ok("Fotoğraflar başarıyla yüklendi! 📸");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Yükleme sırasında hata oluştu: " + e.getMessage());
        }
    }
}
