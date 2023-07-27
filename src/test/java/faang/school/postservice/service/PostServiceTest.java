package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.validator.PostServiceValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Spy
    private PostServiceValidator validator;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
    }

    @Test
    void addPost_BothProjectAndAuthorExist_ShouldThrowException() {
        postDto.setProjectId(1L);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            postService.addPost(postDto);
        });
        Assertions.assertEquals("There is should be only one author", e.getMessage());
    }

    @Test
    void addPost_BothProjectAndAuthorAreNull_ShouldThrowException() {
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            postService.addPost(postDto);
        });
        Assertions.assertEquals("There is should be only one author", e.getMessage());
    }

    @Test
    void addPost_ShouldMapCorrectlyToEntity() {
        PostDto dto = buildPostDto();

        Post actual = postMapper.toEntity(dto);

        Assertions.assertEquals(buildPost(), actual);
    }

    @Test
    void addPost_ShouldMapCorrectlyToDto() {
        Post post = buildPost();

        PostDto actual = postMapper.toDto(post);

        Assertions.assertEquals(buildExpectedPostDto(), actual);
    }

    @Test
    void addPost_ByAuthor_ShouldSave() {
        postService.addPost(postDto);

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void addPost_ByProject_ShouldSave() {
        postDto.setAuthorId(null);
        postDto.setProjectId(1L);

        postService.addPost(postDto);

        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void addPost_ShouldSave() {
        PostDto postDto = buildPostDto();

        postService.addPost(postDto);

        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void publishPost_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post not found", e.getMessage());
    }

    @Test
    void publishPost_PostIsPublished_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().published(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post is already published", e.getMessage());
    }

    @Test
    void publishPost_PostIsDeleted_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().deleted(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void publishPost_PostIsNotPublishedOrDeleted_ShouldNotThrowException() {
        Post post = Post.builder().published(false).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.publishPost(1L));
    }

    @Test
    void publishPost_FieldsShouldBeSet() {
        Post post = buildPost();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.publishPost(1L);

        Assertions.assertTrue(post.isPublished());
        Assertions.assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                post.getPublishedAt());
    }

    @Test
    void publishPost_ShouldPublish() {
        Post post = buildPost();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.publishPost(1L);

        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }


    private PostDto buildPostDto() {
        return PostDto.builder()
                .content("content")
                .authorId(1L)
                .adId(1L)
                .build();
    }

    private Post buildPost() {
        return Post.builder()
                .id(0)
                .content("content")
                .authorId(1L)
                .ad(Ad.builder().id(1L).build())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    private PostDto buildExpectedPostDto() {
        return PostDto.builder()
                .id(0L)
                .content("content")
                .authorId(1L)
                .adId(1L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}