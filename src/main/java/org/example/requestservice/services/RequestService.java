package org.example.requestservice.services;

import org.example.requestservice.dto.AppResponse;
import org.example.requestservice.dto.DeleteResponse;
import org.example.requestservice.dto.RequestDto;
import org.example.requestservice.dto.RequestRequest;
import org.example.requestservice.exceptions.BadRequestException;
import org.example.requestservice.exceptions.NotFoundException;
import org.example.requestservice.model.Request;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequestService {

    ResponseEntity<RequestDto> addRequest(RequestRequest request) throws BadRequestException;

    ResponseEntity<RequestDto> getRequest(Long requestId) throws NotFoundException;

    ResponseEntity<DeleteResponse> removeRequest(Long requestId) throws NotFoundException;

    ResponseEntity<List<RequestDto>> getRequestList();

    ResponseEntity<AppResponse> addTagToRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException;

    ResponseEntity<AppResponse> removeTagFromRequest(Long tagId, Long requestId) throws NotFoundException, BadRequestException;

    ResponseEntity<List<RequestDto>> getRequestListByTag(Long tagId) throws NotFoundException;

    ResponseEntity<AppResponse> removeRequestFromFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException;

    ResponseEntity<AppResponse> addRequestToFolder(Long requestId, Long folderId) throws NotFoundException, BadRequestException;

    ResponseEntity<List<RequestDto>> getRequestListByFolder(Long folderId) throws NotFoundException;

    Request getRequestById(Long requestId) throws NotFoundException;

}
