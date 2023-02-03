package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.domain.ImageEntity;
import hu.ponte.hr.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ImageStoreTest {

    @Mock
    private ImageRepository imageRepositoryMock;
    private ImageStore imageStore;

    @BeforeEach
    void setup() {
        imageStore = new ImageStore(imageRepositoryMock);
    }

    @Test
    void storeFile_testSuccessful() throws IOException {

        MultipartFile testFile = new MockMultipartFile(
                "testFile",
                "testFile.jpeg",
                "image/jpeg",
                "testFileData".getBytes());

        imageStore.storeFile(testFile);

        verify(imageRepositoryMock, times(1)).save(any(ImageEntity.class));
        verifyNoMoreInteractions(imageRepositoryMock);
    }

    @Test
    void storeFile_testFailureWithNullPointerException() {

        try {
            imageStore.storeFile(null);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }

        verifyNoInteractions(imageRepositoryMock);
    }

    @Test
    void getAllImage_testWithTwoFile() throws IOException {

        MultipartFile firstTestFile = new MockMultipartFile(
                "firstTestFile",
                "firstTestFile.jpeg",
                "image/jpeg",
                "firstTestFileData".getBytes());

        ImageEntity firstTestImage = new ImageEntity(
                firstTestFile.getName(),
                firstTestFile.getContentType(),
                firstTestFile.getBytes(),
                "firstFileSignature");

        MultipartFile secondTestFile = new MockMultipartFile(
                "secondTestFile",
                "secondTestFile.png",
                "image/png",
                "secondTestFileData".getBytes());

        ImageEntity secondTestImage = new ImageEntity(
                secondTestFile.getName(),
                secondTestFile.getContentType(),
                secondTestFile.getBytes(),
                "secondFileSignature");

        List<ImageEntity> imageEntityList = List.of(firstTestImage, secondTestImage);

        when(imageRepositoryMock.findAll()).thenReturn(imageEntityList);

        List<ImageMeta> imageMetaList = imageStore.getAllImage();

        assertEquals(2, imageMetaList.size());
        assertEquals("firstTestFile", imageMetaList.get(0).getName());
        assertEquals("image/jpeg", imageMetaList.get(0).getMimeType());
        assertEquals("secondTestFile", imageMetaList.get(1).getName());
        assertEquals("image/png", imageMetaList.get(1).getMimeType());

        verify(imageRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(imageRepositoryMock);
    }

    @Test
    void getAllImage_testWithEmptyList() {

        List<ImageMeta> imageMetaList = imageStore.getAllImage();

        assertTrue(imageMetaList.isEmpty());

        verify(imageRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(imageRepositoryMock);
    }

    @Test
    void getImageData_testSuccessful() throws IOException {

        MultipartFile testFile = new MockMultipartFile(
                "testFile",
                "testFile.jpeg",
                "image/jpeg",
                "testFileData".getBytes());

        ImageEntity testImage = new ImageEntity(
                testFile.getName(),
                testFile.getContentType(),
                testFile.getBytes(),
                "fileSignature");

        when(imageRepositoryMock.findById(anyString())).thenReturn(Optional.of(testImage));

        byte[] result = imageStore.getImageData("testId");

        assertNotNull(result);
        assertEquals(testImage.getData().length, result.length);

        verify(imageRepositoryMock, times(1)).findById(anyString());
        verifyNoMoreInteractions(imageRepositoryMock);
    }

    @Test
    void getImageData_testWithEntityNotFoundException() {

        when(imageRepositoryMock.findById(anyString())).thenThrow(new EntityNotFoundException());

        try {
            imageStore.getImageData("testId");
        } catch (Exception e) {
            assertTrue(e instanceof EntityNotFoundException);
        }

        verify(imageRepositoryMock, times(1)).findById(anyString());
        verifyNoMoreInteractions(imageRepositoryMock);
    }
}
