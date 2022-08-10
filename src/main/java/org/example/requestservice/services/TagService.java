package org.example.requestservice.services;

import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.TagDto;
import org.example.requestservice.dto.TagRequest;
import org.example.requestservice.model.Tag;

import java.util.List;

public interface TagService {

    TagDto addTag(TagRequest tagRequest);

    TagDto getTag(Long tagId);

    DeleteResponse removeTag(Long tagId);

    List<TagDto> getList();

    Tag getTagById(Long tagId);

}
