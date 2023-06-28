package com.epam.esm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.esm.model.Tag;
import com.epam.esm.repository.TagRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagServiceTest {

    private static final int TAG_LIST_SIZE = 3;
    private static TagRepository tagRepository;
    private static List<Tag> tagList;

    @BeforeAll
    public static void prepareMockDao() {
        tagRepository = Mockito.mock(TagRepository.class);
        tagList = new ArrayList<>();
        for (int i = 1; i < TAG_LIST_SIZE + 1; i++) {
            Tag tag = new Tag();
            tag.setName("tag" + i);
            tag.setId(i);
            tagList.add(tag);
        }
    }

    @Test
    void findByIdTest() {
        long id = 2;
        when(tagRepository.findById(id)).thenReturn(Optional.ofNullable(tagList.get((int) id - 1)));

        TagService tagService = new TagServiceImpl(tagRepository);
        assertEquals(tagList.get((int) id - 1), tagService.findById(id));
    }
}
