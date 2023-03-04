package com.isikato.api.web;

import com.isikato.api.BaseWebTest;
import com.isikato.infrastructure.repositories.FileDataRepository;
import com.isikato.infrastructure.repositories.FileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.hamcrest.core.Is.is;

class FileControllerTest extends BaseWebTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileDataRepository dataRepository;


    String upload_url = "/files/uploadFile";


    @Test
    void imageUploadTest() throws IOException {
        //when
        var res = sendRq("image.png", MediaType.IMAGE_PNG);

        //then
        var files = fileRepository.findAll();
        var file = files.get(files.size() - 1);
        var id = file.getId();
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.mime").value(is(MediaType.IMAGE_PNG_VALUE))
                .jsonPath("$.extension").value(is(".png"))
                .jsonPath("$.original").value(is("api/files/" + id + "/" + file.getName()))
                .jsonPath("$.size").exists()
                .jsonPath("$.sizes").exists()
                .jsonPath("$.sizes.thumb").value(is("api/files/" + id + "/" + "thumb_" + file.getName()))
                .jsonPath("$.sizes.mini").value(is("api/files/" + id + "/" + "mini_" + file.getName()))
                .jsonPath("$.sizes.small").value(is("api/files/" + id + "/" + "small_" + file.getName()))
                .jsonPath("$.sizes.medium").value(is("api/files/" + id + "/" + "medium_" + file.getName()))
                .jsonPath("$.sizes.large").value(is("api/files/" + id + "/" + "large_" + file.getName()));
    }

    @Test
    void videoUploadTest() throws IOException {
        //when
        var res = sendRq("video.mp4", MediaType.parseMediaType("video/mp4"));

        //then
        var files = fileRepository.findAll();
        var file = files.get(files.size() - 1);
        var id = file.getId();

        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.mime").value(is("video/mp4"))
                .jsonPath("$.extension").value(is(".mp4"))
                .jsonPath("$.original").value(is("api/files/" + id + "/" + file.getName()))
                .jsonPath("$.size").exists()
                .jsonPath("$.sizes").exists()
                .jsonPath("$.duration").exists()
                .jsonPath("$.coverTime").exists()
                .jsonPath("$.cover").exists()
                .jsonPath("$.sizes.thumb").value(is("api/files/" + id + "/" + "thumb_" + file.getName()))
                .jsonPath("$.sizes.mini").value(is("api/files/" + id + "/" + "mini_" + file.getName()))
                .jsonPath("$.sizes.small").value(is("api/files/" + id + "/" + "small_" + file.getName()))
                .jsonPath("$.sizes.medium").value(is("api/files/" + id + "/" + "medium_" + file.getName()))
                .jsonPath("$.sizes.large").value(is("api/files/" + id + "/" + "large_" + file.getName()))
                .jsonPath("$.sizes.huge").value(is("api/files/" + id + "/" + "huge_" + file.getName()));

    }

    @Test
    void audioUploadTest() throws IOException {
        //when
        var res = sendRq("audio.mp3", MediaType.parseMediaType("audio/mpeg"));

        //then
        var files = fileRepository.findAll();
        var file = files.get(files.size() - 1);
        res
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.mime").value(is("audio/mpeg"))
                .jsonPath("$.extension").value(is(".mp3"))
                .jsonPath("$.original").value(is("api/files/" + file.getId() + "/" + file.getName()))
                .jsonPath("$.size").exists()
                .jsonPath("$.sizes").doesNotExist()
                .jsonPath("$.duration").exists();
    }


    private WebTestClient.ResponseSpec sendRq(String filename, MediaType type) throws IOException {
        var file = this.getClass().getClassLoader().getResourceAsStream(filename);
        var partFile = new ByteArrayResource(file.readAllBytes());
        var token = createTokenForTest();
        var builder = new MultipartBodyBuilder();
        builder.part("file", partFile,type).filename(filename);

        return client
                .post()
                .uri(upload_url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", token)
                .bodyValue(builder.build())
                .exchange();
    }

}