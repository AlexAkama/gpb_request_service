package org.example.requestservice.repositories;

import org.example.requestservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepo extends JpaRepository<Request, Long> {

    boolean existsByText(String text);

    @Query(nativeQuery = true,
            value = "SELECT * FROM request r " +
                    "WHERE r.id IN (SELECT ttr.request_id FROM tag_to_request ttr WHERE ttr.tag_id = ?1)")
    List<Request> getRequestListByTag(Long tagId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM request r " +
                    "WHERE r.id IN (SELECT rtf.request_id FROM request_to_folder rtf WHERE rtf.folder_id = ?1)")
    List<Request> getRequestListByFolder(Long tagFolder);

}
