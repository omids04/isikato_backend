package com.isikato.infrastructure.entities;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "isikato_category")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE isikato_category SET deleted = 1 WHERE id=?")
@Where(clause = "deleted=0")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;
    private String type;

    private String description;

    @OneToOne
    @JoinColumn(name = "image_id")
    private IsikatoFile image;

    @Builder.Default
    private boolean deleted = false;

    @ManyToMany(mappedBy = "categories")
    private List<Content> contents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
