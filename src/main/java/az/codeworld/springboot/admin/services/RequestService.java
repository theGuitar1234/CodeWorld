package az.codeworld.springboot.admin.services;

import java.util.List;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.records.RequestRecord;

public interface RequestService {

    void defaultMethod();

    void saveRequest(Request request);
    void createNewRequest(RequestRecord requestRecord);

    List<RequestDTO> getAllRequests();
    List<RequestDTO> getRecentRequests();

    RequestDTO getRequestByRequestToken(String token);
    RequestDTO getRequestById(Long requestId);

    void deleteRequestByRequestId(Long requestId);

    RequestDTO validateRequest(String token);
}