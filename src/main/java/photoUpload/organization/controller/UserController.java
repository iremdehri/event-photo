package photoUpload.organization.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photoUpload.organization.model.User;
import photoUpload.organization.model.LoginRequest;
import photoUpload.organization.model.AuthResponse;
import photoUpload.organization.service.UserService;
import photoUpload.organization.service.JwtService;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }
    
    @PostMapping("/register")
    public User register(@RequestBody User newUser){
        return userService.registerUser(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        
        String generatedToken = jwtService.generateToken(user); 

        AuthResponse response = new AuthResponse(
            user.getId(), 
            user.getFullName(), 
            user.getEmail(), 
            generatedToken 
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User updatedUser){
        try{
            User updated = userService.updateProfile(id, updatedUser);
            return ResponseEntity.ok(updated);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords){
        try{
            String oldPw = passwords.get("oldPassword");
            String newPw = passwords.get("newPassword");

            userService.changePassword(id, oldPw, newPw);
            return ResponseEntity.ok("Sifre basariyla guncellendi.");
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        try {
            userService.processForgotPassword(email);
            return ResponseEntity.ok("Kod basariyla gonderildi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        if (userService.verifyOtp(email, code)) {
            return ResponseEntity.ok("Kod dogrulandi.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hatali kod!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        userService.updatePassword(email, newPassword);
        return ResponseEntity.ok("Sifre basariyla guncellendi.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Hesabiniz basariyla kaldirildi.");
    }
}