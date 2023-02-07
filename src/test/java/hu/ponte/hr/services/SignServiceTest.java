package hu.ponte.hr.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SignServiceTest {

    Map<String, String> files = new LinkedHashMap<>() {{
        put("cat.jpg", "XYZ+wXKNd3Hpnjxy4vIbBQVD7q7i0t0r9tzpmf1KmyZAEUvpfV8AKQlL7us66rvd6eBzFlSaq5HGVZX2DYTxX1C5fJlh3T3QkVn2zKOfPHDWWItdXkrccCHVR5HFrpGuLGk7j7XKORIIM+DwZKqymHYzehRvDpqCGgZ2L1Q6C6wjuV4drdOTHps63XW6RHNsU18wHydqetJT6ovh0a8Zul9yvAyZeE4HW7cPOkFCgll5EZYZz2iH5Sw1NBNhDNwN2KOxrM4BXNUkz9TMeekjqdOyyWvCqVmr5EgssJe7FAwcYEzznZV96LDkiYQdnBTO8jjN25wlnINvPrgx9dN/Xg==");
        put("enhanced-buzz.jpg", "tsLUqkUtzqgeDMuXJMt1iRCgbiVw13FlsBm2LdX2PftvnlWorqxuVcmT0QRKenFMh90kelxXnTuTVOStU8eHRLS3P1qOLH6VYpzCGEJFQ3S2683gCmxq3qc0zr5kZV2VcgKWm+wKeMENyprr8HNZhLPygtmzXeN9u6BpwUO9sKj7ImBvvv/qZ/Tht3hPbm5SrDK4XG7G0LVK9B8zpweXT/lT8pqqpYx4/h7DyE+L5bNHbtkvcu2DojgJ/pNg9OG+vTt/DfK7LFgCjody4SvZhSbLqp98IAaxS9BT6n0Ozjk4rR1l75QP5lbJbpQ9ThAebXQo+Be4QEYV/YXf07WXTQ==");
        put("rnd.jpg", "lM6498PalvcrnZkw4RI+dWceIoDXuczi/3nckACYa8k+KGjYlwQCi1bqA8h7wgtlP3HFY37cA81ST9I0X7ik86jyAqhhc7twnMUzwE/+y8RC9Xsz/caktmdA/8h+MlPNTjejomiqGDjTGvLxN9gu4qnYniZ5t270ZbLD2XZbuTvUAgna8Cz4MvdGTmE3MNIA5iavI1p+1cAN+O10hKwxoVcdZ2M3f7/m9LYlqEJgMnaKyI/X3m9mW0En/ac9fqfGWrxAhbhQDUB0GVEl7WBF/5ODvpYKujHmBAA0ProIlqA3FjLTLJ0LGHXyDgrgDfIG/EDHVUQSdLWsM107Cg6hQg==");
    }};

    private SignService signService;

    @BeforeEach
    void setUp() throws Exception {
        signService = new SignService();
    }

    @Test
    void signFile_testSuccessful01() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "cat.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/cat.jpg")));

        Map<String, Object> signedFileWithSignature = signService.signFile(testFile);

        assertEquals(files.get("cat.jpg"), signedFileWithSignature.get("sign"));

    }

    @Test
    void signFile_testSuccessful02() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "enhanced-buzz.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/enhanced-buzz.jpg")));

        Map<String, Object> signedFileWithSignature = signService.signFile(testFile);

        assertEquals(files.get("enhanced-buzz.jpg"), signedFileWithSignature.get("sign"));

    }

    @Test
    void signFile_testSuccessful03() throws Exception {

        MockMultipartFile testFile = new MockMultipartFile(
                "file",
                "rnd.jpg",
                "image/jpeg",
                Files.readAllBytes(Path.of("src/test/resources/images/rnd.jpg")));

        Map<String, Object> signedFileWithSignature = signService.signFile(testFile);

        assertEquals(files.get("rnd.jpg"), signedFileWithSignature.get("sign"));

    }
}
