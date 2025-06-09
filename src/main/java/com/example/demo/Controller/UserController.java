package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

     @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

     @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    try {
        // Gọi sang course-service để xóa khóa học của user này
        restTemplate.delete("http://localhost:8082/api/courses/user/" + id);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Lỗi khi gọi Course Service: " + e.getMessage());
    }

    userService.deleteUser(id);
    return ResponseEntity.ok("Đã xóa user và toàn bộ khóa học liên quan.");
}

@PostMapping
public User createUser(@RequestBody User user) {
    return userRepository.save(user);
}

@PutMapping("/{id}")
public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User newUser) {
    return userRepository.findById(id)
            .map(user -> {
                user.setFullName(newUser.getFullName());
                user.setEmail(newUser.getEmail());
                return ResponseEntity.ok(userRepository.save(user));
            })
            .orElse(ResponseEntity.notFound().build());
}


}
