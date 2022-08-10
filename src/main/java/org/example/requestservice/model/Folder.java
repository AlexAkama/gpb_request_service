package org.example.requestservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter

public class Folder extends AbstractEntity {

    private String name;

    @OneToMany
    @JoinTable(name = "request_to_folder",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private Set<Request> requests;

    public String getInfo() {
        return "id=" + getId() + ", name='" + name + "'";
    }

}
