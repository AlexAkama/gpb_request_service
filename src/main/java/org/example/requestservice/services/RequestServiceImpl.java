package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.requestservice.dto.AppResponse;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.RequestDto;
import org.example.requestservice.dto.RequestRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Folder;
import org.example.requestservice.model.Request;
import org.example.requestservice.model.Tag;
import org.example.requestservice.repositories.RequestRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final Logger LOGGER = Logger.getLogger(RequestServiceImpl.class);

    private static final String REQUEST = "Запрос";

    private final RequestRepo requestRepo;
    private final TagService tagService;
    private final FolderService folderService;

    @Value("${request.text.limit}")
    private int textLengthLimit;

    @Value("${request.tag.limit}")
    private int tagLimit;

    @Override
    public RequestDto addRequest(RequestRequest requestRequest) throws BadRequestException {
        validateRequest(requestRequest);
        Request requestEntity = new Request(requestRequest.getText());
        requestRepo.saveAndFlush(requestEntity);
        LOGGER.info("Добавлен запрос: " + requestEntity.getInfo());
        return new RequestDto(requestEntity);
    }

    @Override
    public RequestDto getRequest(Long requestId) throws NotFoundException {
        Request request = getRequestById(requestId);
        LOGGER.info("Получен запрос: " + request.getInfo());
        return new RequestDto(request);
    }

    @Transactional
    @Override
    public DeleteResponse removeRequest(Long requestId) throws NotFoundException {
        Request request = getRequestById(requestId);
        if (request.getFolder() != null) {
            Folder folder = request.getFolder();
            folder.getRequests().remove(request);
            folderService.save(folder);
        }
        requestRepo.delete(request);
        LOGGER.info("Удален запрос: id=" + requestId);
        return new DeleteResponse(REQUEST, requestId);
    }

    @Override
    public List<RequestDto> getRequestList() {
        List<RequestDto> list = requestRepo.findAll().stream().map(RequestDto::new).collect(Collectors.toList());
        LOGGER.info("Получен список запросов: size=" + list.size());
        return list;
    }

    @Override
    public AppResponse addTagToRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException {
        Request request = getRequestById(requestId);
        Set<Tag> tags = request.getTags();
        String error;
        if (tags.size() == tagLimit) {
            error = "Достигнуто максимальное количество прикрепленных тегов. Ограничение = " + tagLimit;
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        Tag tag = tagService.getTagById(tagId);
        if (tags.contains(tag)) {
            error = String.format("Тег id=%d уже прикреплен к запросу", tagId);
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        tags.add(tag);
        requestRepo.save(request);
        String message = String.format("Тег id=%d добавлен к запросу id=%d", tagId, requestId);
        LOGGER.info(message);
        return new AppResponse(message);
    }

    @Transactional
    @Override
    public AppResponse removeTagFromRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException {
        Request request = getRequestById(requestId);
        Set<Tag> tags = request.getTags();
        Tag tag = tagService.getTagById(tagId);
        if (!tags.contains(tag)) {
            String error = String.format("Тег id=%d не прикреплен к запросу", tagId);
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        tags.remove(tag);
        requestRepo.save(request);
        String message = String.format("Тег id=%d удален из запроса id=%d", tagId, requestId);
        LOGGER.info(message);
        return new AppResponse(message);
    }

    @Override
    public List<RequestDto> getRequestListByTag(Long tagId) throws NotFoundException {
        tagService.getTagById(tagId);
        List<RequestDto> list = requestRepo.getRequestListByTag(tagId).stream().map(RequestDto::new).collect(Collectors.toList());
        LOGGER.info(String.format("Получен список запросов по тегу(id=%d): size=%d", tagId, list.size()));
        return list;
    }

    @Transactional
    @Override
    public AppResponse addRequestToFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException {
        Folder folder = folderService.getFolderById(folderId);
        Request request = getRequestById(requestId);
        updateRequestFolder(request, folder);
        requestRepo.save(request);
        folderService.save(folder);
        String message = String.format("Запрос id=%d прикреплен к папке id=%d", requestId, folderId);
        LOGGER.info(message);
        return new AppResponse(message);
    }

    @Transactional
    @Override
    public AppResponse removeRequestFromFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException {
        Folder folder = folderService.getFolderById(folderId);
        Request request = getRequestById(requestId);
        Set<Request> requests = folder.getRequests();
        if (request.getFolder().getId().longValue() != folderId || !requests.contains(request)) {
            String error = String.format("Запрос id=%d не прикреплен к папке id=%d", requestId, folderId);
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        requests.remove(request);
        folderService.save(folder);
        request.setFolder(null);
        requestRepo.save(request);
        String message = String.format("Запрос id=%d откреплен от папки id=%d", requestId, folderId);
        LOGGER.info(message);
        return new AppResponse(message);
    }

    @Override
    public List<RequestDto> getRequestListByFolder(Long folderId) throws NotFoundException {
        Folder folder = folderService.getFolderById(folderId);
        List<RequestDto> list = folder.getRequests().stream().map(RequestDto::new).collect(Collectors.toList());
        LOGGER.info(String.format("Получен список запросов по папке(id=%d): size=%d", folderId, list.size()));
        return list;
    }

    @Override
    public Request getRequestById(Long requestId) throws NotFoundException {
        Optional<Request> optionalRequest = requestRepo.findById(requestId);
        if (optionalRequest.isEmpty()) {
            LOGGER.error(String.format("Запрос id=%d не найден", requestId));
            throw new NotFoundException(REQUEST, requestId);
        }
        return optionalRequest.get();
    }

    private void updateRequestFolder(Request request, Folder newFolder) throws BadRequestException {
        if (request.getFolder() != null) {
            Folder oldFolder = request.getFolder();
            if (oldFolder.getId().longValue() == newFolder.getId().longValue()) {
                String error = String.format("Запрос id=%d уже прикреплен к папке id=%d", request.getId(), newFolder.getId());
                LOGGER.error(error);
                throw new BadRequestException(error);
            }
            Set<Request> oldFolderRequests = oldFolder.getRequests();
            oldFolderRequests.remove(request);
        }
        Set<Request> newFolderRequests = newFolder.getRequests();
        newFolderRequests.add(request);
        request.setFolder(newFolder);
    }

    private void validateRequest(RequestRequest requestRequest) throws BadRequestException {
        String error = null;
        if (requestRequest == null || requestRequest.getText() == null || requestRequest.getText().isBlank()) {
            error = "Отсутствует текст запроса";
        } else if (requestRequest.getText().length() > textLengthLimit) {
            error = "Длина текста превышает ограничение = " + textLengthLimit;
        } else if (requestRepo.existsByText(requestRequest.getText())) {
            error = "Запись с таким текстом уже существует";
        }
        if (error != null) {
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
    }

}
