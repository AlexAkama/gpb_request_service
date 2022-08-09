package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.requestservice.utils.AppUtils.getOkResponse;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String REQUEST = "Запрос";

    private final RequestRepo requestRepo;
    private final TagService tagService;
    private final FolderService folderService;

    @Value("${request.text.limit}")
    private int textLengthLimit;

    @Value("${request.tag.limit}")
    private int tagLimit;

    @Override
    public ResponseEntity<RequestDto> addRequest(RequestRequest request) throws BadRequestException {
        if (request == null || request.getText() == null || request.getText().isBlank())
            throw new BadRequestException("Отсутствует текст запроса");
        if (request.getText().length() > textLengthLimit)
            throw new BadRequestException("Длина текста превышает ограничение = " + textLengthLimit);
        if (requestRepo.existsByText(request.getText()))
            throw new BadRequestException("Запись с таким текстом уже существует");
        Request requestEntity = new Request(request.getText());
        requestRepo.saveAndFlush(requestEntity);
        RequestDto requestDto = new RequestDto(requestEntity);
        return getOkResponse(requestDto);
    }

    @Override
    public ResponseEntity<RequestDto> getRequest(Long requestId) throws NotFoundException {
        Request request = getRequestById(requestId);
        RequestDto requestDto = new RequestDto(request);
        return getOkResponse(requestDto);
    }

    @Override
    public ResponseEntity<DeleteResponse> removeRequest(Long requestId) throws NotFoundException {
        Request request = getRequestById(requestId);
        if (request.getFolder() != null) {
            Folder folder = request.getFolder();
            folder.getRequests().remove(request);
            folderService.save(folder);
        }
        requestRepo.delete(request);
        return getOkResponse(new DeleteResponse(REQUEST, requestId));
    }

    @Override
    public ResponseEntity<List<RequestDto>> getRequestList() {
        return getOkResponseFromCollection(requestRepo.findAll());
    }

    @Override
    public ResponseEntity<AppResponse> addTagToRequest(Long tagId, Long requestId)
            throws NotFoundException, BadRequestException {
        Request request = getRequestById(requestId);
        Set<Tag> tags = request.getTags();
        if (tags.size() == tagLimit)
            throw new BadRequestException(
                    "Достигнуто максимальное количество прикрепленных тегов. Ограничение = " + tagLimit
            );
        Tag tag = tagService.getTagById(tagId);
        if (tags.contains(tag))
            throw new BadRequestException(String.format("Тег id=%d уже прикреплен к запросу", tagId));
        tags.add(tag);
        requestRepo.save(request);
        return getOkResponse(
                new AppResponse(String.format("Тег id=%d добавлен к запросу id=%d", tagId, requestId)));
    }

    @Override
    public ResponseEntity<AppResponse> removeTagFromRequest(Long tagId, Long requestId)
            throws NotFoundException, BadRequestException {
        Request request = getRequestById(requestId);
        Set<Tag> tags = request.getTags();
        Tag tag = tagService.getTagById(tagId);
        if (!tags.contains(tag))
            throw new BadRequestException(String.format("Тег id=%d не прикреплен к запросу", tagId));
        tags.remove(tag);
        requestRepo.save(request);
        return getOkResponse(
                new AppResponse(String.format("Тег id=%d удален из запроса id=%d", tagId, requestId)));
    }

    @Override
    public ResponseEntity<List<RequestDto>> getRequestListByTag(Long tagId) throws NotFoundException {
        tagService.getTagById(tagId);
        return getOkResponseFromCollection(requestRepo.getRequestListByTag(tagId));
    }

    @Override
    public ResponseEntity<AppResponse> addRequestToFolder(Long requestId, Long folderId)
            throws NotFoundException, BadRequestException {
        Folder folder = folderService.getFolderById(folderId);
        Request request = getRequestById(requestId);
        Set<Request> requests = folder.getRequests();
        if (request.getFolder() != null && request.getFolder().getId().longValue() == folderId) {
            if (requests.contains(request))
                throw new BadRequestException(
                        String.format("Запрос id=%d уже прикреплен к папке id=%d", requestId, folderId)
                );
        } else {
            if (request.getFolder() != null) {
                Long oldFolderId = request.getFolder().getId();
                Folder oldFolder = folderService.getFolderById(oldFolderId);
                oldFolder.getRequests().remove(request);
                folderService.save(oldFolder);
            }
            request.setFolder(folder);
        }
        requests.add(request);
        requestRepo.save(request);
        folderService.save(folder);
        return getOkResponse(
                new AppResponse(String.format("Запрос id=%d прикреплен к папке id=%d", requestId, folderId))
        );
    }

    @Override
    public ResponseEntity<AppResponse> removeRequestFromFolder(Long requestId, Long folderId)
            throws NotFoundException, BadRequestException {
        Folder folder = folderService.getFolderById(folderId);
        Request request = getRequestById(requestId);
        Set<Request> requests = folder.getRequests();
        if (request.getFolder().getId().longValue() != folderId || !requests.contains(request))
            throw new BadRequestException(
                    String.format("Запрос id=%d не прикреплен к папке id=%d", requestId, folderId)
            );
        requests.remove(request);
        folderService.save(folder);
        request.setFolder(null);
        requestRepo.save(request);
        return getOkResponse(
                new AppResponse(String.format("Запрос id=%d откреплен от папки id=%d", requestId, folderId))
        );

    }

    @Override
    public ResponseEntity<List<RequestDto>> getRequestListByFolder(Long folderId) throws NotFoundException {
        Folder folder = folderService.getFolderById(folderId);
        return getOkResponseFromCollection(folder.getRequests());
    }

    @Override
    public Request getRequestById(Long requestId) throws NotFoundException {
        return requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST, requestId));
    }

    private ResponseEntity<List<RequestDto>> getOkResponseFromCollection(Collection<Request> collection) {
        return ResponseEntity.ok(collection.stream().map(RequestDto::new).collect(Collectors.toList()));
    }

}
