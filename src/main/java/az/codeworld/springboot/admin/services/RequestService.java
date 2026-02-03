package az.codeworld.springboot.admin.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.records.RequestRecord;
import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;

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

    Page<RequestDTO> getPaginatedRequests(int pageIndex, int pageSize,
            String sortBy, Direction direction);
}