package com.epam.esm.dto;

import com.epam.esm.model.Tag;
import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TagDto implements Serializable {
    @NotBlank(message = "name of tag must not be empty")
    private String name;

    public Tag convertToTag() {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}
