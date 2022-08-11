package org.example.requestservice.services;

import org.example.requestservice.config.TestConfig;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Tag;
import org.example.requestservice.repositories.TagRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
@TestPropertySource("/application-test.properties")
@SpringBootTest
class TagServiceTest {

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private TagServiceImpl tagService;

    @Value("${tag.length.limit}")
    private int tagLengthLimit;

    @Test
    void addTag_noRequest() {
        assertThrows(BadRequestException.class, () -> tagService.addTag(null));
    }

    @Test
    void addTag_emptyRequest() {
        TagRequest tagRequest = new TagRequest();

        assertThrows(BadRequestException.class, () -> tagService.addTag(tagRequest));
    }

    @Test
    void addTag_blankName() {
        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("   ");

        assertThrows(BadRequestException.class, () -> tagService.addTag(tagRequest));
    }

    @Test
    void addTag_nameLengthOverLimit() {
        TagRequest tagRequest = new TagRequest();
        tagRequest.setName("a".repeat(tagLengthLimit + 1));

        assertThrows(BadRequestException.class, () -> tagService.addTag(tagRequest));
    }

    @Test
    void addTag_existsName() {
        String testName = "TestName";
        when(tagRepo.existsByName(testName)).thenReturn(true);

        TagRequest tagRequest = new TagRequest();
        tagRequest.setName(testName);

        assertThrows(BadRequestException.class, () -> tagService.addTag(tagRequest));
    }

    @Test
    void addTag_expectedTag() {
        String testName = "TestName";
        Tag testTag = new Tag();
        testTag.setId(99L);
        testTag.setName(testName);
        when(tagRepo.saveAndFlush(any())).thenReturn(testTag);
        when(tagRepo.existsByName(testName)).thenReturn(false);

        TagRequest tagRequest = new TagRequest();
        tagRequest.setName(testName);

        TagDto tagDto = tagService.addTag(tagRequest);

        assertEquals(tagRequest.getName(), tagDto.getName());
    }

    @Test
    void getTag_bagId() {
        when(tagRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagService.getTag(99L));
    }

    @Test
    void getTag_expectedTag() {
        Tag testTag = new Tag();
        testTag.setId(99L);
        testTag.setName("TestTag");
        when(tagRepo.findById(99L)).thenReturn(Optional.of(testTag));

        TagDto tagDto = tagService.getTag(99L);

        assertEquals(testTag.getName(), tagDto.getName());
    }

    @Test
    void removeTag_badId() {
        when(tagRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tagService.removeTag(99L));
    }

    @Test
    void removeTag_hasLinkedResponse() {
        Tag testTag = new Tag();
        testTag.setId(99L);
        testTag.setName("TestTag");
        when(tagRepo.findById(any())).thenReturn(Optional.of(testTag));
        when(tagRepo.hasRequestToTag(any())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> tagService.removeTag(99L));
    }

    @Test
    void removeTag_expectedResponse() {
        Tag testTag = new Tag();
        testTag.setId(99L);
        testTag.setName("TestTag");
        when(tagRepo.findById(99L)).thenReturn(Optional.of(testTag));
        when(tagRepo.hasRequestToTag(any())).thenReturn(false);
        String message = "Тег id=99 удален успешно";

        DeleteResponse deleteResponse = tagService.removeTag(99L);

        assertEquals(deleteResponse.getMessage(), message);
    }

    @Test
    void getList_empty() {
        List<Tag> tags = Collections.emptyList();
        when(tagRepo.findAll()).thenReturn(tags);
        List<TagDto> expected = Collections.emptyList();

        assertEquals(expected, tagService.getList());

    }

    @Test
    void getList_expectedList() {
        Tag t1 = new Tag();
        Tag t2 = new Tag();
        Tag t3 = new Tag();
        t1.setId(1L);
        t2.setId(2L);
        t3.setId(3L);
        t1.setName("Tag1");
        t2.setName("Tag2");
        t3.setName("Tag3");
        List<Tag> tags = List.of(t1, t2, t3);
        when(tagRepo.findAll()).thenReturn(tags);
        List<TagDto> expected = tags.stream().map(TagDto::new).collect(Collectors.toList());

        assertEquals(expected, tagService.getList());

    }

    @Test
    void getTagById_badId() {
        when(tagRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tagService.getTagById(99L));
    }

    @Test
    void getTagById_expectedTag() {
        Tag testTag = new Tag();
        testTag.setId(99L);
        testTag.setName("TestTag");
        when(tagRepo.findById(99L)).thenReturn(Optional.of(testTag));

        Tag tag = tagService.getTagById(99L);

        assertEquals(testTag.getName(), tag.getName());
    }

}