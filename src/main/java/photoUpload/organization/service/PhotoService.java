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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Map;

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

    public List<Photo> getPhotosByEvent(Long eventId) {
        return photoRepository.findByEventId(eventId);
    }

    public void deleteMultiplePhotos(List<Long> photoIds){
        photoRepository.deleteAllById(photoIds);
    }

    @Transactional
    public void savePhotosForEvent(String uuid, MultipartFile[] files) throws IOException {
        System.out.println("DEBUG: UUID ile arama basladi: " + uuid);
        Event event = eventRepository.findByUuid(uuid)
            .orElseThrow(() -> {
                System.out.println("DEBUG: Veritabaninda bu UUID bulunamadi!");
                return new RuntimeException("Albüm bulunamadı!");
            });
        
        String safeFolderName = event.getName()
            .toLowerCase()
            .replaceAll("ı", "i").replaceAll("ğ", "g").replaceAll("ü", "u")
            .replaceAll("ş", "s").replaceAll("ö", "o").replaceAll("ç", "c")
            .replaceAll("[^a-z0-9]", "_") 
            .replaceAll("_+", "_");   

        System.out.println("DEBUG: Event bulundu: " + event.getName());
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                System.out.println("DEBUG: Bos dosya atlandi.");
                continue;
            }

            try {
                System.out.println("DEBUG: Cloudinary'ye yukleme basliyor...");
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("folder", "event_photos/" + safeFolderName));
                
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
} 
