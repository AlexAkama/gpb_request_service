package org.example.requestservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Request extends AbstractEntity {

    private String text;
    private Integer length;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @ManyToMany
    @JoinTable(name = "tag_to_request",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "request_id")
    )
    private Set<Tag> tags;

    public Request(String text) {
        this.text = text;
        length = text.length();
    }

}
