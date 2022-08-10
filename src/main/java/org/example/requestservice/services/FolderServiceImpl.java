package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.FolderDto;
import org.example.requestservice.dto.FolderRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Folder;
import org.example.requestservice.repositories.FolderRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private static final Logger LOGGER = Logger.getLogger(FolderServiceImpl.class);

    private final FolderRepo folderRepo;

    @Value("${folder.name.limit}")
    private int folderNameLimit;

    @Override
    public FolderDto addFolder(FolderRequest folderRequest) {
        validateRequest(folderRequest);
        Folder folder = new Folder();
        folder.setName(folderRequest.getName());
        folderRepo.saveAndFlush(folder);
        LOGGER.info("Добавлена папка: " + folder.getInfo());
        return new FolderDto(folder);
    }

    @Override
    public FolderDto getFolder(Long folderId) {
        Folder folder = getFolderById(folderId);
        LOGGER.info("Получена папка: " + folder.getInfo());
        return new FolderDto(folder);
    }

    @Override
    public DeleteResponse removeFolder(Long folderId) {
        Folder folder = getFolderById(folderId);
        if (!folder.getRequests().isEmpty()) {
            String error = String.format("Папку id=%d удалить нельзя - прикреплены запросы", folderId);
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        folderRepo.delete(folder);
        LOGGER.info("Удалена папка: id=" + folderId);
        return new DeleteResponse("Папка", folderId);
    }

    @Override
    public List<FolderDto> getFolderList() {
        List<FolderDto> list = folderRepo.findAll()
                .stream()
                .map(FolderDto::new)
                .collect(Collectors.toList());
        LOGGER.info("Получен список папок: size=" + list.size());
        return list;
    }

    @Override
    public Folder getFolderById(Long folderId) {
        Optional<Folder> optionalFolder = folderRepo.findById(folderId);
        if (optionalFolder.isEmpty()) {
            LOGGER.error(String.format("Папка id=%d не найдена", folderId));
            throw new NotFoundException("Папка", folderId);
        }
        return optionalFolder.get();
    }

    @Override
    public void save(Folder folder) {
        folderRepo.save(folder);
    }

    private void validateRequest(FolderRequest folderRequest) {
        String error = null;
        if (folderRequest == null || folderRequest.getName() == null || folderRequest.getName().isBlank()) {
            error = "Не указано имя папки";
        } else if (folderRequest.getName().length() > folderNameLimit) {
            error = "Длинна тега превышает ограничение = " + folderNameLimit;
        } else if (folderRepo.existsByName(folderRequest.getName())) {
            error = "Папка с таким именем уже существует";
        }
        if (error != null) {
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
    }

}
