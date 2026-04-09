package photoUpload.organization.repository;

import org.jspecify.annotations.Nullable;
import photoUpload.organization.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByUuid(String uuid);
    Optional<Event> findByUuidAndIsDeletedFalse(String uuid);
    List<Event> findByUserIdAndIsDeletedFalse(Long userId);
    List<Event> findAllByUuidIn(List<String> uuids);
}
