package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateDuration;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.exception.NoContentException;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.dto.SearchCriteria;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa/certificate")
@Validated
public class GiftCertificateController {
    private static final String PARENT_RELATION_NAME = "parent";
    private final GiftCertificateService giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(null);
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/hal+json")
    public CollectionModel<GiftCertificate> findAll(@RequestParam(required = false) List<String> tag,
                                                    @RequestParam(required = false) String search,
                                                    @RequestParam(required = false) List<String> sort,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "5") Integer size) {
        SearchCriteria rph = new SearchCriteria(tag, search, sort, page, size);
        List<GiftCertificate> giftCertificates = giftCertificateService.findAll(rph);
        if (giftCertificates.isEmpty()) {
            throw new NoContentException();
        }
        for (GiftCertificate giftCertificate : giftCertificates) {
            Link link = linkTo(methodOn(GiftCertificateController.class).findById(giftCertificate.getId()))
                    .withSelfRel();
            giftCertificate.add(link);
        }
        Link link = linkTo(methodOn(GiftCertificateController.class).findAll(tag, search, sort, page, size))
                .withSelfRel();
        return CollectionModel.of(giftCertificates, link);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = "application/hal+json")
    public GiftCertificate findById(@PathVariable long id) {
        return addLinksToGiftCertificate(giftCertificateService.findById(id));
    }

    @JsonView(Views.ShortView.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}/tag", produces = "application/hal+json")
    public CollectionModel<Tag> findGiftCertificateTags(@PathVariable long id,
                                                        @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "5") Integer size) {
        List<Tag> tags = giftCertificateService.findGiftCertificateTags(id, page, size);
        for (Tag tag : tags) {
            Link link = linkTo(methodOn(TagController.class).findById(tag.getId())).withSelfRel();
            tag.add(link);
        }
        Link selflink = linkTo(methodOn(GiftCertificateController.class).findGiftCertificateTags(id, page, size))
                .withSelfRel();
        Link userLink = linkTo(methodOn(GiftCertificateController.class).findById(id))
                .withRel(LinkRelation.of(PARENT_RELATION_NAME));
        return CollectionModel.of(tags, selflink, userLink);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/hal+json")
    public GiftCertificate create(@Valid @RequestBody GiftCertificate certificate) {
        return addLinksToGiftCertificate(giftCertificateService.create(certificate));
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/hal+json")
    public GiftCertificate update(@PathVariable long id, @RequestBody GiftCertificateDto certificate) {
        certificate.setId(id);
        return addLinksToGiftCertificate(giftCertificateService.update(certificate));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = "application/hal+json")
    public void delete(@PathVariable long id) {
        giftCertificateService.delete(id);
    }

    @JsonView(Views.FullView.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping(value = "/{id}/duration",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/hal+json")
    public GiftCertificate updateDuration(@PathVariable long id,
                                          @Valid @RequestBody GiftCertificateDuration dto) {
        return addLinksToGiftCertificate(giftCertificateService.update(new GiftCertificateDto(id, dto.getDuration())));
    }

    private GiftCertificate addLinksToGiftCertificate(GiftCertificate certificate) {
        certificate.add(linkTo(GiftCertificateController.class).slash(certificate.getId()).withSelfRel());
        certificate.add(linkTo(GiftCertificateController.class).withRel(LinkRelation.of(PARENT_RELATION_NAME)));
        certificate.add(linkTo(methodOn(GiftCertificateController.class)
                .findGiftCertificateTags(certificate.getId(), null, null))
                .withRel(IanaLinkRelations.COLLECTION));
        return certificate;
    }
}
