package com.isikato.api.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isikato.api.BaseWebTest;
import com.isikato.infrastructure.entities.Category;
import com.isikato.infrastructure.repositories.CategoryRepository;
import com.isikato.infrastructure.repositories.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

class CategoryControllerTest extends BaseWebTest {

    static String ADD_UPDATE_API_URL = "/categories/add";
    static String DELETE_API_URL = "/categories/remove";
    static String GET_INFO_API_URL = "/categories/getInfo";
    static String GET_ALL_API_URL = "/categories/getAll";

    @Autowired
    WebTestClient client;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FileRepository fileRepository;

    ObjectMapper mapper;


    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }


    @Test
    void callingCreateCatApiWithoutToken(){

        //when
        var res = sendRq("body", ADD_UPDATE_API_URL);

        //then
        res
                .expectStatus().isUnauthorized()
                .expectBody()
                    .jsonPath("$").exists()
                    .jsonPath("$.path").isEqualTo(ADD_UPDATE_API_URL)
                    .jsonPath("$.message").exists();

    }
    @Test
    void catNameCanNotBeNullOrEmpty(){
        //given
        var body = """
                {
                "name":""
                }
                """;
        var token = createTokenForTest();

        //when
        var res = sendRq(body, token, ADD_UPDATE_API_URL);

        //then
        res
                .expectStatus().isBadRequest()
                .expectBody()
                        .jsonPath("$").exists()
                        .jsonPath("$.path").isEqualTo(ADD_UPDATE_API_URL)
                        .jsonPath("$.message").exists();
    }
//
//    @Test
//    @DirtiesContext
//    void catNameAlreadyExists(){
//        //given
//        var cat = Category.builder().name("cat").build();
//        categoryRepository.save(cat);
//        var req = new CategoryCreationModel();
//        req.setName("cat");
//        var body = toJsonString(req);
//
//        //when
//        //then
//        sendRq(body, ADD_UPDATE_API_URL)
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.path").isEqualTo(ADD_UPDATE_API_URL)
//                .jsonPath("$.message").exists();
//    }
//
//    @Test
//    @DirtiesContext
//    void catWithAllNeededInfoShouldGetsPersisted(){
//        //given
//        var img = imageRepository.save(Image.builder().build());
//        var req = new CategoryCreationModel();
//        req.setName("cat");
//        req.setImage(new IdModel(img.getId()));
//        var body = toJsonString(req);
//
//        //when
//        var specs = sendRq(body, ADD_UPDATE_API_URL);
//        //then
//
//        var cat = categoryRepository.findById(1L).get();
//        specs
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.id").isEqualTo(cat.getId())
//                .jsonPath("$.name").isEqualTo(cat.getName())
//                .jsonPath("$.image.id").isEqualTo(cat.getImage().getId());
//
//    }
//
//    @Test
//    @DirtiesContext
//    void noImageIsProvided(){
//        //given
//        var req = new CategoryCreationModel();
//        req.setName("cat");
//        var body = toJsonString(req);
//
//        //when
//        //then
//        sendRq(body, ADD_UPDATE_API_URL)
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.image").doesNotExist()
//                .jsonPath("$.id").exists();
//    }
//
//
//    @Test
//    @DirtiesContext
//    void whenIdIsProvidedMeansCatInfoShouldGetsUpdated(){
//        //given
//        var newType = "new_type";
//        var cat = Category.builder().name("cat").type("type").build();
//        cat = categoryRepository.save(cat);
//        var req = new CategoryCreationModel();
//        req.setName("cat");
//        req.setId(cat.getId());
//        req.setType(newType);
//        var body = toJsonString(req);
//
//        //when
//        var specs = sendRq(body, ADD_UPDATE_API_URL);
//
//        //then
//        var updatedCat = categoryRepository.findById(req.getId()).get();
//        assertEquals(newType, updatedCat.getType());
//        specs
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.id").isEqualTo(updatedCat.getId())
//                .jsonPath("$.name").isEqualTo(updatedCat.getName())
//                .jsonPath("$.type").isEqualTo(updatedCat.getType());
//    }
//
//    @Test
//    @DirtiesContext
//    void whileUpdatingIfAPropertyIsNotProvidedMeansItShouldBecomeUnsetInDb(){
//        //given
//        var cat = Category.builder().name("cat").type("type").build();
//        cat = categoryRepository.save(cat);
//        var req = new CategoryCreationModel();
//        req.setName("cat");
//        req.setId(cat.getId());
//        var body = toJsonString(req);
//
//        //when
//        var specs = sendRq(body, ADD_UPDATE_API_URL);
//
//        //then
//        var updatedCat = categoryRepository.findById(req.getId()).get();
//        assertNull(updatedCat.getType());
//        specs
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.id").isEqualTo(updatedCat.getId())
//                .jsonPath("$.name").isEqualTo(updatedCat.getName())
//                .jsonPath("$.type").doesNotExist();
//    }
//
//    @Test
//    @DirtiesContext
//    void returnTrueAndOkStatusOnSuccessfulDelete(){
//        //given
//        var cat = Category.builder().name("cat").type("type").build();
//        cat = categoryRepository.save(cat);
//        var req = new IdModel();
//        req.setId(cat.getId());
//        var body = toJsonString(req);
//
//        //when
//        var specs = sendRq(body, DELETE_API_URL);
//
//        //then
//        specs
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.success").isEqualTo(true);
//        var exists = categoryRepository.existsById(req.getId());
//        assertFalse(exists);
//    }
//
//    @Test
//    @DirtiesContext
//    void returnFalseAndOkStatusOnUnsuccessfulDelete(){
//        //given
//        var req = new IdModel();
//        req.setId(5L);
//        var body = toJsonString(req);
//        assertFalse(categoryRepository.existsById(5L));
//
//        //when
//        var specs = sendRq(body, DELETE_API_URL);
//
//        //then
//        specs
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.success").isEqualTo(false);
//    }
//
//    @Test
//    @DirtiesContext
//    void returnOkStatusAndCatInfoWhenGettingACatThatExistInDb(){
//        //given
//        var name = "cat";
//        var type = "type";
//        var cat = Category.builder().name(name).type(type).build();
//        cat = categoryRepository.save(cat);
//        var req = new IdModel();
//        req.setId(cat.getId());
//        var body = toJsonString(req);
//
//        //when
//        var specs = sendRq(body, GET_INFO_API_URL);
//
//        //then
//        specs
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.id").isEqualTo(cat.getId())
//                .jsonPath("$.name").isEqualTo(cat.getName())
//                .jsonPath("$.type").isEqualTo(cat.getType());
//    }
//
//    @Test
//    @DirtiesContext
//    void returnBadRequestStatusWhenGettingACatThatDoesNotExistInDb(){
//        //given
//        var req = new IdModel();
//        req.setId(5L);
//        var body = toJsonString(req);
//        assertFalse(categoryRepository.existsById(5L));
//
//        //when
//        var specs = sendRq(body, GET_INFO_API_URL);
//
//        //then
//        specs
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$").exists()
//                .jsonPath("$.path").isEqualTo(GET_INFO_API_URL)
//                .jsonPath("$.message").exists()
//                .jsonPath("$.timestamp").exists();
//    }
}