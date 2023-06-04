package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "tag")
public class Tag extends RepresentationModel<Tag> implements Serializable {
    @JsonView(Views.FullView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.ShortView.class)
    @NotBlank(message = "name of tag must not be empty")
    @Column(unique = true, nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
