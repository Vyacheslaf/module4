package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@DynamicUpdate
@Table(name = "gift_certificate")
public class GiftCertificate extends RepresentationModel<GiftCertificate> implements Serializable {
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @JsonView(Views.ShortView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.ShortView.class)
    @NotBlank(message = "name of gift certificate must not be empty")
    private String name;

    @NotBlank(message = "description must not be empty")
    @JsonView(Views.FullView.class)
    private String description;

    @NotNull(message = "price must not be null")
    @JsonView(Views.FullView.class)
    private Integer price;

    @NotNull(message = "duration must not be null")
    @JsonView(Views.FullView.class)
    private Integer duration;

    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonView(Views.FullView.class)
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @JsonFormat(pattern = ISO_8601_PATTERN)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonView(Views.FullView.class)
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Valid
    @JsonView(Views.IgnoredView.class)
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "gift_certificate_tag",
            joinColumns = { @JoinColumn(name = "gift_certificate_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    private Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "giftCertificate")
    private List<Order> orders;

    public GiftCertificate(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GiftCertificate that = (GiftCertificate) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(price, that.price) && Objects.equals(duration, that.duration)
                && Objects.equals(createDate, that.createDate) && Objects.equals(lastUpdateDate, that.lastUpdateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, description, price, duration, createDate, lastUpdateDate);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getGiftCertificates().remove(this);
    }
}
