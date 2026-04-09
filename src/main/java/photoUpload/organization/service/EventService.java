package photoUpload.organization.service;

import org.springframework.web.multipart.MultipartFile;
import photoUpload.organization.model.Event;
import org.springframework.stereotype.Service;
import photoUpload.organization.model.User;
import photoUpload.organization.repository.EventRepository;
import photoUpload.organization.repository.UserRepository;

import java.io.File;
import java.util.UUID;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final FileService fileService;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, FileService fileService, UserRepository userRepository){
        this.eventRepository=eventRepository;
        this.fileService=fileService;
        this.userRepository=userRepository;
    }

    public Event createEvent(String name, String subTitle, Long userId, MultipartFile file){
        String fileName=fileService.saveFile(file);
        Event event=new Event();
        event.setName(name);
        event.setSubTitle(subTitle);
        event.setCoverUri("uploads/events/"+fileName);

        User user= userRepository.findById(userId).orElseThrow();
        event.setUser(user);

        return eventRepository.save(event);
    }

    public Event getEventByUuid(String uuid){
        return eventRepository.findByUuid(uuid)
                .orElseThrow(()-> new RuntimeException("Geçersiz QR Kod!"));
    }

    public void deleteMultiple(List<Long> ids) {
    eventRepository.deleteAllById(ids); 
    }

}
