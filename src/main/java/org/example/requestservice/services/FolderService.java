package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.model.Folder;

import java.util.List;

public interface FolderService {

    FolderDto addFolder(FolderRequest request);

    FolderDto getFolder(Long folderId);

    DeleteResponse removeFolder(Long folderId);

    List<FolderDto> getFolderList();

    Folder getFolderById(Long folderId);

    void save(Folder folder);

}
