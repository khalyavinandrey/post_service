package faang.school.postservice.mapper;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LikeMapperTest {
    @Autowired
    private LikeMapper likeMapper;
    LikeDto likeDto;
    Like like;

    @BeforeEach
    void setUp(){
        Comment comment = Comment.builder().id(1L).build();
        Post post = Post.builder().id(1L).build();
        likeDto = new LikeDto(1L,1L,1L,1L);
        like = Like.builder().id(1L).userId(1L).comment(comment).post(post).build();
    }

    @Test
    void toDto() {
        Assertions.assertEquals(likeDto,likeMapper.toDto(like));
    }

    @Test
    void toModel() {
        like = Like.builder().id(1L).userId(1L).build();
        Assertions.assertEquals(like,likeMapper.toModel(likeDto));
    }
}