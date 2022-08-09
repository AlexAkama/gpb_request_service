package org.example.requestservice.repositories;

import org.example.requestservice.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepo extends JpaRepository<Folder, Long> {

    boolean existsByName(String name);

}
