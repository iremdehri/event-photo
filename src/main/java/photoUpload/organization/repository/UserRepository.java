package photoUpload.organization.repository;

import photoUpload.organization.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>findByEmail(String email);

    Optional<User> findByPhoneNumber(String phone);

    Optional<User> findByEmailAndActiveTrue(String email);

    Optional<User> findByPhoneNumberAndActiveTrue(String phoneNumber);

    //tüm emaillerin kontrolü için
    boolean existsByEmail(String email);
}
