package com.isikato.infrastructure.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Table(name = "isikato_content")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE isikato_content SET deleted = 1 WHERE id=?")
@Where(clause = "deleted=0")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Builder.Default
    private boolean deleted = false;

    private boolean featured;

    @OneToMany
    @JoinTable(name = "isikato_content_image",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<IsikatoFile> images;


    @OneToMany
    @JoinTable(name = "isikato_content_image2",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<IsikatoFile> images2;

    private String title;

    @Lob
    private String body;

    private String description;
    private String page;

    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;
    private String extra5;

    private boolean published;

    @Transient
    private long visitCounter;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "isikato_content_category",
                joinColumns = @JoinColumn(name = "content_id"),
                inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private List<IsikatoFile> files;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedTime;

    @ElementCollection
    @CollectionTable(name = "isikato_tag", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "name")
    private List<String> tags;

    @OneToMany(mappedBy = "content")
    private List<ContentVisit> visits;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Employee writer;

    private long downloadCounter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return id == content.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
