package com.isikato.infrastructure.entities;


import com.isikato.fileutil.model.IsikatoFileType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import javax.persistence.*;
import java.util.List;

@Table(name = "isikato_file")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE isikato_file SET deleted = 1 WHERE id=?")
@Where(clause = "deleted=0")
public class IsikatoFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Builder.Default
    private boolean deleted = false;
    private String mime;
    private String extension;
    @Column(name = "file_size")
    private long size;
    private String name;
    private IsikatoFileType type;
    @ManyToOne
    private Content content;
    private double duration;
    private int coverTime;
    @OneToMany(mappedBy = "file", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FileData> fileData;
}
