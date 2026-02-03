package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.mappers.RequestMapper;
import az.codeworld.springboot.admin.repositories.RequestRepository;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import az.codeworld.springboot.utilities.generators.TokenGenerator;
import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;
import az.codeworld.springboot.web.mappers.CourseEnrollmentMapper;
import az.codeworld.springboot.admin.records.RequestRecord;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    public RequestServiceImpl(
            RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public void defaultMethod() {
    }

    @Override
    public void saveRequest(Request request) {
        if (request.getExpiresAt() == 0L) {
            request.setExpiresAt(Instant.now().plus(Duration.ofDays(1L)).toEpochMilli());
        }
        requestRepository.save(request);
        requestRepository.flush();
    }

    @Override
    public void createNewRequest(RequestRecord requestRecord) {
        Request request = new Request();
        request.setFirstName(requestRecord.firstName());
        request.setLastName(requestRecord.lastName());
        request.setEmail(requestRecord.email());
        request.setRole(requestRecord.role());
        request.setRequestToken(TokenGenerator.generateToken());

        saveRequest(request);
    }

    @Override
    public RequestDTO getRequestById(Long requestId) {
        Optional<Request> requestOptional = requestRepository.findById(requestId);
        Request request = requestOptional.orElseThrow(() -> new RuntimeException("Request Not Found"));

        return RequestMapper.toRequestDTO(
                request.getId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getRole(),
                request.getRequestToken(),
                request.getExpiresAt());
    }

    @Override
    public List<RequestDTO> getAllRequests() {
        return requestRepository
                .findAllRequests()
                .stream()
                .map(requestOptional -> {
                    if (requestOptional.isPresent()) {
                        Request request = requestOptional.get();
                        return RequestMapper.toRequestDTO(
                            request.getId(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getRole(),
                            request.getRequestToken(),
                            request.getExpiresAt());
                    } else {
                        return null;
                    }
                    
                })
                .toList();
    }

    @Override
    public List<RequestDTO> getRecentRequests() {
        return requestRepository
                .findRecentRequests()
                .stream()
                .map(requestOptional -> {
                    if (requestOptional.isPresent()) {
                        Request request = requestOptional.get();
                        return RequestMapper.toRequestDTO(
                                request.getId(),
                                request.getFirstName(),
                                request.getLastName(),
                                request.getEmail(),
                                request.getRole(),
                                request.getRequestToken(),
                                request.getExpiresAt());
                    } else {
                        return null;
                    }
                })
                .toList();
    }

    @Override
    public void deleteRequestByRequestId(Long requestId) {
        requestRepository.deleteRequestById(requestId);
        requestRepository.flush();
    }

    @Override
    public RequestDTO getRequestByRequestToken(String token) {
        Request request = requestRepository.findByRequestToken(token).orElseThrow(() -> new RuntimeException("Request Not Found"));
        return RequestMapper.toRequestDTO(
            request.getId(), 
            request.getFirstName(), 
            request.getLastName(), 
            request.getEmail(), 
            request.getRole(), 
            request.getRequestToken(),
            request.getExpiresAt()
        ); 
    }

    @Override
    public RequestDTO validateRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findByRequestToken(token);   
        if (!(requestOptional.isPresent() && requestOptional.get().getExpiresAt() > System.currentTimeMillis()))
            throw new InvalidRequestTokenException("token: " + token);
        Request request = requestOptional.get();
        RequestDTO requestDTO = RequestMapper.toRequestDTO(
            request.getId(), 
            request.getFirstName(), 
            request.getLastName(), 
            request.getEmail(), 
            request.getRole(), 
            request.getRequestToken(),
            request.getExpiresAt()
        );
        return requestDTO; 
    }

    @Override
    public Page<RequestDTO> getPaginatedRequests(int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        return requestRepository
                .findAll(PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy))
                .map(r -> RequestMapper.toRequestDTO(r.getId(), r.getFirstName(), r.getLastName(), r.getEmail(), r.getRole(), r.getRequestToken(), r.getExpiresAt()));
    }

}
