package org.example.requestservice.services;

import org.example.requestservice.dto.AppResponse;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.RequestDto;
import org.example.requestservice.dto.RequestRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Request;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(RequestRequest request) throws BadRequestException;

    RequestDto getRequest(Long requestId) throws NotFoundException;

    DeleteResponse removeRequest(Long requestId) throws NotFoundException;

    List<RequestDto> getRequestList();

    AppResponse addTagToRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException;

    AppResponse removeTagFromRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException;

    List<RequestDto> getRequestListByTag(Long tagId) throws NotFoundException;

    AppResponse removeRequestFromFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException;

    AppResponse addRequestToFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException;

    List<RequestDto> getRequestListByFolder(Long folderId) throws NotFoundException;

    Request getRequestById(Long requestId) throws NotFoundException;

}
