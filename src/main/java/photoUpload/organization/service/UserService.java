package photoUpload.organization.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder; // Import eklendi
import org.springframework.stereotype.Service;
import photoUpload.organization.model.User;
import photoUpload.organization.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserRepository userRepository;

    // Profesyonel yaklaşım: Constructor Injection (En güvenli yol)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    private final Map<String, String> otpStorage = new HashMap<>();

    public User registerUser(User newUser) {
        String phone = newUser.getPhoneNumber();
        if (phone == null || !phone.matches("^\\+905[0-9]{9}$")) {
            throw new RuntimeException("Lütfen geçerli bir telefon numarası giriniz!");
        }
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda! Lütfen başka bir tane deneyin. ❌");
        }
        if (newUser.getPassword() == null || newUser.getPassword().length() < 6) {
            throw new RuntimeException("Şifre en az 6 karakter olmalıdır!");
        }
        if (userRepository.findByPhoneNumber(phone).isPresent()) {
            throw new RuntimeException("Bu telefon numarası zaten bir hesaba kayıtlı! 📱");
        }

        // Şifre hashleniyor
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return userRepository.save(newUser);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Hatalı e-posta veya şifre!"));

        // Hash kontrolü
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Hatalı e-posta veya şifre!");
        }
        return user;
    }

    public User updateProfile(Long id, User updatedInfo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));
        userRepository.findByEmailAndActiveTrue(updatedInfo.getEmail()).ifPresent(user->{
            if(!user.getId().equals(id)){
                throw new RuntimeException("Bu e-posta adresi başka bir kullanıcı tarafından kullanılıyor!");
            }
        });
        userRepository.findByPhoneNumberAndActiveTrue(updatedInfo.getPhoneNumber()).ifPresent(user->{
            if(!user.getId().equals(id)){
                throw new RuntimeException("Bu telefon numarası başka bir hesapta tanımlı! 📞");
            }
        });

        existingUser.setFullName(updatedInfo.getFullName());
        existingUser.setEmail(updatedInfo.getEmail());
        existingUser.setPhoneNumber(updatedInfo.getPhoneNumber());

        return userRepository.save(existingUser);
    }

    // Kullanıcı giriş yapmışken şifre değiştirme alanı
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        // Hashli şifre kontrolü düzeltildi
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifreniz hatalı!");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Yeni şifre en az 6 karakter olmalıdır.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void processForgotPassword(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Bu e-posta adresiyle kayıtlı kullanıcı bulunamadı."));

        String code = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, code);
        mailService.sendResetCode(email, code);
    }

    public boolean verifyOtp(String email, String code) {
        return otpStorage.containsKey(email) && otpStorage.get(email).equals(code);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStorage.remove(email);
    }

    @Transactional
    public void deleteUser(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Kullanıcı Bulunamadı!"));
        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User authenticate(String email, String password) {
  
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi."));

    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Sifre hatali.");
    }

    return user;
    }
}