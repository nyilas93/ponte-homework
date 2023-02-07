package hu.ponte.hr.controller;

import hu.ponte.hr.domain.ImageEntity;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ImagesControllerTest {

    @Mock
    private ImageStore imageStoreMock;
    private MockMvc mockMvc;
    private ImagesController imagesController;

    @BeforeEach
    void setUp() {
        imagesController = new ImagesController(imageStoreMock);
        mockMvc = MockMvcBuilders
                .standaloneSetup(imagesController)
                .build();
    }

    @Test
    void listImages_testWithEmptyList() throws Exception {

        when(imageStoreMock.getAllImage()).thenReturn(Collections.emptyList());

        this.mockMvc
                .perform(
                        get("/api/images/meta")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));

        verify(imageStoreMock, times(1)).getAllImage();
        verifyNoMoreInteractions(imageStoreMock);
    }

    @Test
    void listImages_testWithAListContainingTwoItems() throws Exception {

        ImageMeta firstTestMeta = ImageMeta.builder()
                .id("firstTestId")
                .name("firstTestFile")
                .mimeType("image/jpeg")
                .size(11)
                .digitalSign("firstTestSign").build();

        ImageMeta secondTestMeta = ImageMeta.builder()
                .id("secondTestId")
                .name("secondTestFile")
                .mimeType("image/png")
                .size(22)
                .digitalSign("secondTestSign").build();

        List<ImageMeta> result = List.of(firstTestMeta, secondTestMeta);

        when(imageStoreMock.getAllImage()).thenReturn(result);

        this.mockMvc
                .perform(
                        get("/api/images/meta")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(firstTestMeta.getId())))
                .andExpect(jsonPath("$[0].name", is(firstTestMeta.getName())))
                .andExpect(jsonPath("$[0].mimeType", is(firstTestMeta.getMimeType())))
                .andExpect(jsonPath("$[0].size", is(11)))
                .andExpect(jsonPath("$[0].digitalSign", is(firstTestMeta.getDigitalSign())))
                .andExpect(jsonPath("$[1].id", is(secondTestMeta.getId())))
                .andExpect(jsonPath("$[1].name", is(secondTestMeta.getName())))
                .andExpect(jsonPath("$[1].mimeType", is(secondTestMeta.getMimeType())))
                .andExpect(jsonPath("$[1].size", is(22)))
                .andExpect(jsonPath("$[1].digitalSign", is(secondTestMeta.getDigitalSign())));

        verify(imageStoreMock, times(1)).getAllImage();
        verifyNoMoreInteractions(imageStoreMock);
    }

    @Test
    void getImage_testSuccessful() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "cat.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/cat.jpg")));

        ImageEntity testImage = new ImageEntity(
                testFile.getName(),
                testFile.getContentType(),
                testFile.getBytes(),
                "fileSignature");

        when(imageStoreMock.getImageData(anyString())).thenReturn(testImage.getData());

        MvcResult mvcResult = this.mockMvc
                .perform(
                        get("/api/images/preview/testId")
                )
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(testImage.getData().length, mvcResult.getResponse().getContentAsByteArray().length);

        verify(imageStoreMock, times(1)).getImageData(anyString());
        verifyNoMoreInteractions(imageStoreMock);
    }
}
