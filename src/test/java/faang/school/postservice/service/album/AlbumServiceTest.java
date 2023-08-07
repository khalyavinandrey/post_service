package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.AlbumDataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private AlbumService albumService;

    @Test
    public void testCreateAlbum() {
        UserDto mockUserDto = getMockUserDto();
        when(userServiceClient.getUser(1L)).thenReturn(mockUserDto);

        AlbumDto albumDto = getValidAlbumDto();
        Album mockAlbum = Album.builder()
                .id(1L)
                .title(albumDto.getTitle())
                .description(albumDto.getDescription())
                .authorId(albumDto.getAuthorId())
                .build();
        when(albumMapper.toEntity(albumDto)).thenReturn(mockAlbum);
        when(albumRepository.save(any(Album.class))).thenReturn(mockAlbum);
        when(albumMapper.toDto(mockAlbum)).thenReturn(albumDto);

        AlbumDto createdAlbum = albumService.createAlbum(albumDto);

        verify(userServiceClient, times(1)).getUser(1L);
        verify(albumRepository, times(1)).save(any(Album.class));
        verify(albumMapper, times(1)).toDto(mockAlbum);

        assertNotNull(createdAlbum);
        assertEquals(albumDto.getTitle(), createdAlbum.getTitle());
        assertEquals(albumDto.getDescription(), createdAlbum.getDescription());
        assertEquals(albumDto.getAuthorId(), createdAlbum.getAuthorId());
    }

    @Test
    public void testCreateAlbum_InvalidAuthor() {
        when(userServiceClient.getUser(1L)).thenReturn(null);

        AlbumDataValidationException exception = assertThrows(AlbumDataValidationException.class,
                () -> albumService.createAlbum(getValidAlbumDto()));

        assertEquals("There is no user with id 1", exception.getMessage());
    }

    @Test
    public void testCreateAlbum_NonUniqueTitle() {
        UserDto mockUserDto = getMockUserDto();
        when(userServiceClient.getUser(1L)).thenReturn(mockUserDto);

        AlbumDto albumDto = getValidAlbumDto();
        Album existingAlbum = Album.builder()
                .id(2L)
                .title(albumDto.getTitle())
                .description("Another album with the same title")
                .authorId(albumDto.getAuthorId())
                .build();
        when(albumRepository.findByAuthorId(1L)).thenReturn(Stream.of(existingAlbum));

        AlbumDataValidationException exception = assertThrows(AlbumDataValidationException.class,
                () -> albumService.createAlbum(albumDto));

        assertEquals("Title of the album should be unique", exception.getMessage());
    }

    private UserDto getMockUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();
    }

    private AlbumDto getValidAlbumDto() {
        return AlbumDto.builder()
                .title("Test Album")
                .description("Album description")
                .authorId(1L)
                .build();
    }
}