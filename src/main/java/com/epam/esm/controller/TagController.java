package com.epam.esm.controller;

import com.epam.esm.exception.NoContentException;
import com.epam.esm.model.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa/tag")
@Validated
public class TagController {
    private static final String PARENT_RELATION_NAME = "parent";
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/hal+json")
    public CollectionModel<Tag> findAll(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "5") Integer size) {
        List<Tag> tags = tagService.findAll(page, size);
        if (tags.isEmpty()) {
            throw new NoContentException();
        }
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(TagController.class).findById(tag.getId()))
                    .withSelfRel();
            tag.add(link);
        }
        Link link = linkTo(methodOn(TagController.class).findAll(page, size))
                .withSelfRel();
        return CollectionModel.of(tags, link);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = "application/hal+json")
    public Tag findById(@PathVariable long id) {
        return addLinksToTag(tagService.findById(id));
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/hal+json")
    public Tag create(@Valid @RequestBody Tag tag, BindingResult bindingResult) {
        return addLinksToTag(tagService.create(tag));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = "application/hal+json")
    public void delete(@PathVariable long id) {
        tagService.delete(id);
    }

    private Tag addLinksToTag(Tag tag) {
        tag.add(linkTo(TagController.class).slash(tag.getId()).withSelfRel());
        tag.add(linkTo(TagController.class)
                .withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        return tag;
    }
}
