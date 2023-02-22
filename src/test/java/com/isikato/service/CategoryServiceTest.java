//package com.isikato.service;
//
//
//import com.isikato.infrastructure.entities.Category;
//import com.isikato.infrastructure.repositories.CategoryRepository;
//import com.isikato.infrastructure.repositories.FileRepository;
//import com.isikato.service.exceptions.CategoryNamingException;
//import com.isikato.service.exceptions.CategoryNotFoundException;
//
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import java.util.Optional;
//
///**
// *
// * @author omid
// */
//@ExtendWith(MockitoExtension.class)
//public class CategoryServiceTest {
//
//    @Mock
//    CategoryRepository categoryRepository;
//    @Mock
//    FileRepository imageRepository;
//    @InjectMocks
//    CategoryService service;
//    @Captor
//    ArgumentCaptor<Category> captor;
//    String name = "my-cat";
//    String type = "my-type";
//    String des = "description for my-cat";
//
//
//    @Test
//    void whenCategoryHasNoIdITMeansANewOneShouldGetsCreated() {
//        //given
//        var imgDto = ImageDto.builder().build();
//        var catDto = CategoryDto.builder().name(name).image(imgDto).type(type).description(des).build();
//        var catEntity = Category.builder().id(5L).name(name).type(type).description(des).build();
//        when(categoryRepository.save(any())).thenReturn(catEntity);
//        when(imageRepository.findById(0L)).thenReturn(Optional.empty());
//
//        //when
//        var persisted = service.createOrUpdate(catDto);
//
//        //then
//        verify(categoryRepository, times(0)).existsById(anyLong());
//        verify(categoryRepository, times(1)).save(captor.capture());
//        assertEquals(0L, captor.getValue().getId());
//        assertEquals(name, captor.getValue().getName());
//        assertNotEquals(0L, persisted.getId());
//        assertEquals(name, persisted.getName());
//        assertEquals(type, persisted.getType());
//        assertEquals(des, persisted.getDescription());
//    }
//
//    @Test
//    void whenCategoryWithGivenIdDoesNotExists() {
//        //given
//        var cat = CategoryDto.builder().id(5L).name(name).type(type).description(des).build();
//        when(categoryRepository.existsById(5L)).thenReturn(false);
//        //when
//        //then
//        assertThrows(CategoryNotFoundException.class, () -> service.createOrUpdate(cat));
//    }
//
//    @Test
//    void whenCreatingCategory_ProvidedNameShouldNotAlreadyExist() {
//        //given
//        var cat = CategoryDto.builder().name(name).type(type).description(des).build();
//        when(categoryRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
//
//        //when
//        //then
//        assertThrows(CategoryNamingException.class, () -> service.createOrUpdate(cat));
//
//    }
//
////    @Test
////    void nameShouldNotBeNullForCategoryCreation() {
////        //given
////        var cat = CategoryDto.builder().type(type).description(des).build();
////
////        //when
////        //then
////        assertThrows(CategoryNotFoundException.class, () -> service.createOrUpdate(cat));
////    }
////
////
////
////    @Test
////    void nameShouldNotBeEmptyForCategoryCreation() {
////        //given
////        var cat = CategoryDto.builder().name("  ").type(type).description(des).build();
////
////        //when
////        //then
////        assertThrows(CategoryNotFoundException.class, () -> service.createOrUpdate(cat));
////    }
//
//
//    @Test
//    void whenCategoryWithGivenIdExistsItShouldGetsUpdated() {
//        //given
//        var img = ImageDto.builder().build();
//        var newCat = CategoryDto.builder().id(5L).image(img).name(name).type(type).description(des).build();
//        var cat = Category.builder().id(5L).name(name).type(type).description(des).build();
//        when(categoryRepository.save(any())).thenReturn(cat);
//        when(categoryRepository.existsById(5L)).thenReturn(true);
//
//        //when
//        service.createOrUpdate(newCat);
//
//        //then
//        verify(categoryRepository, times(1)).save(any());
//    }
//
//    @Test
//    void whenDeleteWasSuccessfulItShouldReturnTrueToIndicateThat(){
//        //given
//        when(categoryRepository.removeById(anyLong())).thenReturn(1L);
//
//        //when
//        boolean deleted = service.remove(5L);
//
//        //then
//        verify(categoryRepository, times(1)).deleteById(5L);
//        assertTrue(deleted);
//    }
//
//    @Test
//    void whenDeleteWasUnSuccessfulItShouldReturnFalseToIndicateThat(){
//        //given
//        when(categoryRepository.removeById(anyLong())).thenReturn(0L);
//
//        //when
//        boolean deleted = service.remove(5L);
//
//        //then
//        verify(categoryRepository, times(1)).deleteById(5L);
//        assertFalse(deleted);
//    }
//
//}
