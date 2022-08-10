package org.example.requestservice.services;

import org.example.requestservice.dto.AppResponse;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.RequestDto;
import org.example.requestservice.dto.RequestRequest;
import org.example.requestservice.model.Request;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(RequestRequest request);

    RequestDto getRequest(Long requestId);

    DeleteResponse removeRequest(Long requestId);

    List<RequestDto> getRequestList();

    AppResponse addTagToRequest(Long tagId, Long requestId);

    AppResponse removeTagFromRequest(Long tagId, Long requestId);

    List<RequestDto> getRequestListByTag(Long tagId);

    AppResponse removeRequestFromFolder(Long requestId, Long folderId);

    AppResponse addRequestToFolder(Long requestId, Long folderId);

    List<RequestDto> getRequestListByFolder(Long folderId);

    Request getRequestById(Long requestId);

}
