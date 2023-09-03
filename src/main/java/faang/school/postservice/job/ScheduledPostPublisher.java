package faang.school.postservice.job;

import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ScheduledPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final ScheduledPostService scheduledPostService;

    @Scheduled(fixedRateString = "${schedule.time_interval}")
    @Async("scheduledTaskExecutor")
    public void publishScheduledPosts() {
        scheduledPostService.completeScheduledPosts();
    }
}
