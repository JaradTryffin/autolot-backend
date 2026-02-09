package com.autolot.autolotbackend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dealership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[a-z0-9-]+$")
    private String slug;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String address;

    private String logoUrl;

    @Column(columnDefinition = "text")
    private String about;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dealership", cascade = CascadeType.ALL)
    private List<AdminUser> adminUsers= new ArrayList<>();

    public void setSlug(String slug){
        this.slug = slug != null ? slug.toLowerCase() : null;
    }
}
