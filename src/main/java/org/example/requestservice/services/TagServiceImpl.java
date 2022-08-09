package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Tag;
import org.example.requestservice.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.requestservice.utils.AppUtils.*;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;

    @Value("${tag.length.limit}")
    private int tagLengthLimit;

    @Override
    public ResponseEntity<TagDto> addTag(TagRequest tagRequest) throws BadRequestException {
        if (tagRequest == null || tagRequest.getName() == null || tagRequest.getName().isBlank())
            throw new BadRequestException("Не указано имя тега");
        if (tagRequest.getName().length() > tagLengthLimit)
            throw new BadRequestException("Длинна тега превышает ограничение = " + tagLengthLimit);
        if (tagRepo.existsByName(tagRequest.getName()))
            throw new BadRequestException("Тег с таким именем уже существует");
        Tag tag = new Tag();
        tag.setName(tagRequest.getName());
        tagRepo.saveAndFlush(tag);
        TagDto tagDto = new TagDto(tag);
        return getOkResponse(tagDto);
    }

    @Override
    public ResponseEntity<TagDto> getTag(Long tagId) throws NotFoundException {
        Tag tag = getTagById(tagId);
        TagDto tagDto = new TagDto(tag);
        return getOkResponse(tagDto);
    }

    @Override
    public ResponseEntity<DeleteResponse> removeTag(Long tagId) throws NotFoundException, BadRequestException {
        Tag tag = getTagById(tagId);
        if (tagRepo.hasRequestToTag(tagId))
            throw new BadRequestException(String.format("Тег id=%d удалить нельзя - прикреплены запросы", tagId));
        tagRepo.delete(tag);
        return getOkResponse(new DeleteResponse("Тег", tagId));
    }

    @Override
    public ResponseEntity<List<TagDto>> getList() {
        List<TagDto> list = tagRepo.findAll().stream().map(TagDto::new).collect(Collectors.toList());
        return getOkResponse(list);
    }

    @Override
    public Tag getTagById(Long tagId) throws NotFoundException {
        return tagRepo.findById(tagId).orElseThrow(() -> new NotFoundException("Тег", tagId));
    }

}
