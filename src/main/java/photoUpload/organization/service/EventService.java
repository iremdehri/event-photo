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

        event.setUuid(UUID.randomUUID().toString());
        event.setDeleted(false);

        User user= userRepository.findById(userId).orElseThrow();
        event.setUser(user);

        return eventRepository.save(event);
    }

    public Event getEventByUuid(String uuid){
        return eventRepository.findByUuidAndIsDeletedFalse(uuid)
                .orElseThrow(() -> new RuntimeException("Gecersiz veya silinmis album!"));
    }

    public List<Event> getActiveEventsByUserId(Long userId) {
        return eventRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    public void deleteEventByUuid(String uuid) {
        Event event = eventRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Album bulunamadi"));
        
        event.setDeleted(true); 
        eventRepository.save(event);
    }

    public void deleteMultipleByUuids(List<String> uuids) {
        List<Event> events = eventRepository.findAllByUuidIn(uuids);
        for (Event event : events) {
            event.setDeleted(true);
        }
        eventRepository.saveAll(events);
    }

}
