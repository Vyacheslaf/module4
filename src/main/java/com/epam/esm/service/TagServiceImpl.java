package com.epam.esm.service;

import com.epam.esm.exception.InvalidIdException;
import com.epam.esm.exception.DuplicateTagNameException;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void delete(long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new InvalidIdException(id, Tag.class.getSimpleName()));
        tagRepository.delete(tag);
    }

    @Override
    public List<Tag> findAll(int page, int size) {
        return tagRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public Tag findById(long id) {
        return tagRepository.findById(id).orElseThrow(() -> new InvalidIdException(id, Tag.class.getSimpleName()));
    }

    @Override
    public Tag create(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateTagNameException();
        }
    }
}
