package photoUpload.organization.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import photoUpload.organization.model.Event;
import photoUpload.organization.model.Photo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import photoUpload.organization.repository.EventRepository;
import photoUpload.organization.repository.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PhotoService {

    private final Cloudinary cloudinary;
    @Autowired
    private final PhotoRepository photoRepository;
    @Autowired
    private final EventRepository eventRepository;

    public PhotoService(Cloudinary cloudinary, PhotoRepository photoRepository, EventRepository eventRepository){
        this.cloudinary = cloudinary;
        this.photoRepository = photoRepository;
        this.eventRepository = eventRepository;
    }

    private final String uploadPath = "uploads/";
    public Photo savePhotoToEvent(MultipartFile file, Long eventId) throws IOException{
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()-> new RuntimeException("Etkinlik bulunamadı!"));

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl=(String) uploadResult.get("url");

        Photo photo = new Photo();
        photo.setImageUrl(imageUrl);
        photo.setEvent(event);

        return photoRepository.save(photo);
    }

    public Photo uploadPhoto(String imageUrl, Event event){
        Photo newPhoto =new Photo();
        newPhoto.setImageUrl(imageUrl);
        newPhoto.setEvent(event);
        return photoRepository.save(newPhoto);
    }

    public List<Photo> getPhotosByEvent(Long eventId) {
        return photoRepository.findByEventId(eventId);
    }

    public void deleteMultiplePhotos(List<Long> photoIds){
        photoRepository.deleteAllById(photoIds);
    }

    @Transactional
    public void savePhotosForEvent(String uuid, MultipartFile[] files) throws IOException {
        // 1. UUID ile ilgili Event'i (Albümü) bul
        System.out.println("DEBUG: UUID ile arama basladi: " + uuid);
        Event event = eventRepository.findByUuid(uuid)
            .orElseThrow(() -> {
                System.out.println("DEBUG: Veritabaninda bu UUID bulunamadi!");
                return new RuntimeException("Albüm bulunamadı!");
            });

        System.out.println("DEBUG: Event bulundu: " + event.getName());
        for (MultipartFile file : files) {
        if (file.isEmpty()) {
            System.out.println("DEBUG: Bos dosya atlandi.");
            continue;
        }

        try {
            System.out.println("DEBUG: Cloudinary'ye yukleme basliyor...");
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "event_photos/test_folder")); // Klasor ismini basitlestirdik

            String secureUrl = (String) uploadResult.get("secure_url");
            System.out.println("DEBUG: Yukleme basarili. URL: " + secureUrl);

            Photo photo = new Photo();
            photo.setImageUrl(secureUrl);
            photo.setEvent(event);
            photoRepository.save(photo);
            System.out.println("DEBUG: Fotoğraf DB'ye kaydedildi.");
        } catch (Exception e) {
            System.out.println("DEBUG: Cloudinary yukleme hatasi: " + e.getMessage());
            throw e;
        }
    }
}
