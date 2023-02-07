package hu.ponte.hr.controller.upload;

import hu.ponte.hr.config.FileUploadExceptionAdvice;
import hu.ponte.hr.services.ImageStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private ImageStore imageStoreMock;
    private MockMvc mockMvc;
    private UploadController uploadController;

    @BeforeEach
    void setUp() {
        uploadController = new UploadController(imageStoreMock);
        mockMvc = MockMvcBuilders
                .standaloneSetup(uploadController)
                .setControllerAdvice(new FileUploadExceptionAdvice())
                .build();
    }

    @Test
    void handleFormUpload_testSuccessful() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "cat.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/cat.jpg")));

        this.mockMvc
                .perform(
                        multipart("/api/file/post")
                                .file(testFile)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Successful file upload: cat.jpg")));

        verify(imageStoreMock, times(1)).storeFile(any(MultipartFile.class));
        verifyNoMoreInteractions(imageStoreMock);
    }

    @Test
    void handleFormUpload_testBadRequest() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "wrongName",
                "cat.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/cat.jpg")));

        this.mockMvc
                .perform(
                        multipart("/api/file/post")
                                .file(testFile)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(imageStoreMock);
    }
}
