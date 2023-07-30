package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/like")
public class LikeController {
    private final LikeService service;

    @GetMapping("/post/{id}")
    public List<UserDto> getPostLikes(@PathVariable long id) {
        return service.getPostLikes(id);
    }

    @GetMapping("/comment/{id}")
    public List<UserDto> getCommentLikes(@PathVariable long id) {
        return service.getCommentLikes(id);
    }
}