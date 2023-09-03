package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.ScheduledPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ScheduledTaskExecutorConfig {

    private final ScheduledPostService scheduledPostService;
    private final LikeService likeService;
    private final CommentService commentService;

    @Bean
    public Map<ScheduledEntityType, ScheduledTaskExecutor> scheduledTaskExecutors() {
        return Map.of(
                ScheduledEntityType.POST, new ScheduledPostExecutorImpl(scheduledPostService),
                ScheduledEntityType.LIKE, new ScheduledLikeExecutorImpl(likeService),
                ScheduledEntityType.COMMENT, new ScheduledCommentExecutorImpl(commentService)
        );
    }
}
