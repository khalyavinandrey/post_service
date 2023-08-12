package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto createPost(PostDto post) {
        ProjectDto project = null;
        UserDto user = null;

        if (post.getProjectId() != null) {
            project = projectServiceClient.getProject(post.getProjectId());
        } else if (post.getAuthorId() != null) {
            user = userServiceClient.getUser(post.getAuthorId());
        }

        postValidator.validatePostCreator(post, project, user);
        postValidator.validatePostContent(post);

        Post postEntity = postMapper.toPost(post);

        return postMapper.toDto(postRepository.save(postEntity));
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validatePublishPost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postUpdateDto) {
        Post post = getPostById(postUpdateDto.getId());
        postValidator.validationOfPostUpdate(postUpdateDto, post);

        Post updatedPost = postMapper.toPost(postUpdateDto);

        return postMapper.toDto(postRepository.save(updatedPost));
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        return postMapper.toDto(getPostById(postId));
    }

    @Transactional
    public boolean softDeletePost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validationOfPostDelete(post);

        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}
