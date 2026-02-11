package com.autolot.autolotbackend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SiteConfig extends TenantScoped {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String layoutJson;

    @Column(nullable = false)
    private String theme = "default";

    @Column(nullable = false)
    private String primaryColor;

    private String secondaryColor;

    private String fontFamily;

    @Column(columnDefinition = "text")
    private String customCss;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
