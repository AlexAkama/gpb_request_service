package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Tag;

import java.util.List;

public interface TagService {

    TagDto addTag(TagRequest tagRequest) throws BadRequestException;

    TagDto getTag(Long tagId) throws NotFoundException;

    DeleteResponse removeTag(Long tagId) throws NotFoundException, BadRequestException;

    List<TagDto> getList();

    Tag getTagById(Long tagId) throws NotFoundException;

}
