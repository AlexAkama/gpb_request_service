package org.example.requestservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag extends AbstractEntity {

    private String name;

    public String getInfo() {
        return "id=" + getId() + ", name='" + name + "'";
    }

}
