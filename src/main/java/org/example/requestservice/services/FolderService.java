package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Folder;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FolderService {

    ResponseEntity<FolderDto> addFolder(FolderRequest request) throws BadRequestException;

    ResponseEntity<FolderDto> getFolder(Long folderId) throws NotFoundException;

    ResponseEntity<DeleteResponse> removeFolder(Long folderId) throws NotFoundException, BadRequestException;

    ResponseEntity<List<FolderDto>> getFolderList();

    Folder getFolderById(Long folderId) throws NotFoundException;

    void save(Folder folder);

}
