package org.example.requestservice.repositories;

import org.example.requestservice.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepo extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    @Query(nativeQuery = true,
            value = "SELECT EXISTS (SELECT * FROM tag_to_request WHERE tag_id = ?1)")
    boolean hasRequestToTag(Long tagId);

}
