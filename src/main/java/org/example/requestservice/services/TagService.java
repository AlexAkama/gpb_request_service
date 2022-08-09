package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TagService {

    ResponseEntity<TagDto> addTag(TagRequest tagRequest) throws BadRequestException;

    ResponseEntity<TagDto> getTag(Long tagId) throws NotFoundException;

    ResponseEntity<DeleteResponse> removeTag(Long tagId) throws NotFoundException, BadRequestException;

    ResponseEntity<List<TagDto>> getList();

    Tag getTagById(Long tagId) throws NotFoundException;

}
