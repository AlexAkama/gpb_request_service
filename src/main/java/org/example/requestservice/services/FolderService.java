package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Folder;

import java.util.List;

public interface FolderService {

    FolderDto addFolder(FolderRequest request) throws BadRequestException;

    FolderDto getFolder(Long folderId) throws NotFoundException;

    DeleteResponse removeFolder(Long folderId) throws NotFoundException, BadRequestException;

    List<FolderDto> getFolderList();

    Folder getFolderById(Long folderId) throws NotFoundException;

    void save(Folder folder);

}
