package com.epam.esm.service;

import com.epam.esm.model.Tag;

import java.util.List;

public interface TagService {
    void delete(long id);
    List<Tag> findAll(int page, int size);
    Tag findById(long id);
    Tag create(Tag tag);
}
