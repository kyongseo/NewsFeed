package hello.blog.feature.controller.api;

import hello.blog.feature.domain.User;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ImageApiController {

    private final UserService userService;

    @PostMapping("/api/s3/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {

        User user = userService.updateProfileImage(userId, file);
        log.info("Uploaded file URL: " + user.getFilepath());
        return ResponseEntity.ok(user.getFilepath());
    }
}