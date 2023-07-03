package com.epam.esm.controller;

import com.epam.esm.dto.TagDto;
import com.epam.esm.model.Tag;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @InjectMocks
    TagController tagController;

    @Mock
    TagService tagService;

    @Test
    void createTest() {
        long tagId = 1;
        String tagName = "tagName";
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);

        Tag tag = tagDto.convertToTag();
        tag.setId(tagId);
        when(tagService.create(any(Tag.class))).thenReturn(tag);

        assertEquals(tagDto.getName(), tagController.create(tagDto).getName());
    }
}
