package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Folder;
import org.example.requestservice.repositories.FolderRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.requestservice.utils.AppUtils.getOkResponse;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepo folderRepo;

    @Value("${folder.name.limit}")
    private int folderNameLimit;

    @Override
    public ResponseEntity<FolderDto> addFolder(FolderRequest request) throws BadRequestException {
        if (request == null || request.getName() == null || request.getName().isBlank())
            throw new BadRequestException("Не указано имя папки");
        if (request.getName().length() > folderNameLimit)
            throw new BadRequestException("Длинна тега превышает ограничение = " + folderNameLimit);
        if (folderRepo.existsByName(request.getName()))
            throw new BadRequestException("Папка с таким именем уже существует");
        Folder folder = new Folder();
        folder.setName(request.getName());
        folderRepo.saveAndFlush(folder);
        FolderDto folderDto = new FolderDto(folder);
        return getOkResponse(folderDto);
    }

    @Override
    public ResponseEntity<FolderDto> getFolder(Long folderId) throws NotFoundException {
        Folder folder = getFolderById(folderId);
        FolderDto folderDto = new FolderDto(folder);
        return getOkResponse(folderDto);
    }

    @Override
    public ResponseEntity<DeleteResponse> removeFolder(Long folderId) throws NotFoundException, BadRequestException {
        Folder folder = getFolderById(folderId);
        if (!folder.getRequests().isEmpty())
            throw new BadRequestException("Папку id=%d удалить нельзя - прикреплены запросы");
        folderRepo.delete(folder);
        return getOkResponse(new DeleteResponse("Папка", folderId));
    }

    @Override
    public ResponseEntity<List<FolderDto>> getFolderList() {
        List<Folder> folderList = folderRepo.findAll();
        List<FolderDto> list = folderList.stream().map(FolderDto::new).collect(Collectors.toList());
        return getOkResponse(list);
    }

    @Override
    public Folder getFolderById(Long folderId) throws NotFoundException {
        return folderRepo.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Папка", folderId));
    }

    @Override
    public void save(Folder folder) {
        folderRepo.save(folder);
    }

}
