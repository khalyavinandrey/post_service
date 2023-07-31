package faang.school.postservice.service.album;

import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
}
