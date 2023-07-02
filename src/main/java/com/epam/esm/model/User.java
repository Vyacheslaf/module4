package com.epam.esm.model;

import com.epam.esm.util.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Audited
@Table(name = "\"user\"")
public class User extends RepresentationModel<User> implements Serializable, UserDetails {
    @JsonView(Views.ShortView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(Views.ShortView.class)
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @JsonView(Views.FullView.class)
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @JsonView(Views.IgnoredView.class)
    @Column(nullable = false, length = 60)
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return id == user.id
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password)
                && role == user.role && Objects.equals(orders, user.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, username, email, password, role, orders);
    }
}
