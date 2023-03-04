package com.isikato.infrastructure.entities;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Table(name = "isikato_data")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE isikato_data SET deleted = 1 WHERE id=?")
@Where(clause = "deleted=0")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String pathToData;

    @Builder.Default
    private boolean deleted = false;

    private Type type;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private IsikatoFile file;

    public enum Type{
        ORIGINAL, THUMB, MINI, SMALL, MEDIUM, LARGE, HUGE, COVER
    }
}
