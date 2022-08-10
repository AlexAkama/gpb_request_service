package org.example.requestservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Tag;
import org.example.requestservice.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private static final Logger LOGGER = Logger.getLogger(TagServiceImpl.class);
    private final TagRepo tagRepo;

    @Value("${tag.length.limit}")
    private int tagLengthLimit;

    @Override
    public TagDto addTag(TagRequest tagRequest) {
        validateRequest(tagRequest);
        Tag tag = new Tag();
        tag.setName(tagRequest.getName());
        tagRepo.saveAndFlush(tag);
        LOGGER.info("Добавлен тег: " + tag.getInfo());
        return new TagDto(tag);
    }

    @Override
    public TagDto getTag(Long tagId) {
        Tag tag = getTagById(tagId);
        LOGGER.info("Получен тег: " + tag.getInfo());
        return new TagDto(tag);
    }

    @Override
    public DeleteResponse removeTag(Long tagId) {
        Tag tag = getTagById(tagId);
        if (tagRepo.hasRequestToTag(tagId)) {
            String error = String.format("Тег id=%d удалить нельзя - прикреплены запросы", tagId);
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
        tagRepo.delete(tag);
        LOGGER.info("Удалена папка: id=" + tagId);
        return new DeleteResponse("Тег", tagId);
    }

    @Override
    public List<TagDto> getList() {
        List<TagDto> list = tagRepo.findAll()
                .stream()
                .map(TagDto::new)
                .collect(Collectors.toList());
        LOGGER.info("Получен список тегов: size=" + list.size());
        return list;
    }

    @Override
    public Tag getTagById(Long tagId) {
        Optional<Tag> optionalTag = tagRepo.findById(tagId);
        if (optionalTag.isEmpty()) {
            LOGGER.error(String.format("Тег id=%d не найден", tagId));
            throw new NotFoundException("Тег", tagId);
        }
        return optionalTag.get();
    }

    private void validateRequest(TagRequest tagRequest) {
        String error = null;
        if (tagRequest == null || tagRequest.getName() == null || tagRequest.getName().isBlank()) {
            error = "Не указано имя тега";
        } else if (tagRequest.getName().length() > tagLengthLimit) {
            error = "Длинна тега превышает ограничение = " + tagLengthLimit;
        } else if (tagRepo.existsByName(tagRequest.getName())) {
            error = "Тег с таким именем уже существует";
        }
        if (error != null) {
            LOGGER.error(error);
            throw new BadRequestException(error);
        }
    }

}
