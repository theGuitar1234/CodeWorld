package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.mappers.RequestMapper;
import az.codeworld.springboot.admin.repositories.RequestRepository;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.utilities.TokenGenerator;
import az.codeworld.springboot.admin.records.RequestRecord;

@Service
@Profile("dev")
public class RequestServiceImplDev implements RequestService {

    private final RequestRepository requestRepository;

    public RequestServiceImplDev(
            RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public void defaultMethod() {
    }

    @Override
    public void saveRequest(Request request) {
        if (request.getExpiresAt() == 0L) {
            request.setExpiresAt(Instant.now().plus(Duration.ofMillis(15000)).toEpochMilli());
        }
        requestRepository.save(request);
        requestRepository.flush();
    }

    @Override
    public void createNewRequest(RequestRecord requestRecord) {
        Request request = new Request();
        request.setFirstname(requestRecord.firstname());
        request.setLastname(requestRecord.lastname());
        request.setEmail(requestRecord.email());
        request.setRole(requestRecord.role());
        request.setRequestToken(TokenGenerator.generateToken());

        saveRequest(request);
    }

    @Override
    public RequestDTO getRequestById(Long requestId) {
        Optional<Request> requestOptional = requestRepository.findByRequestId(requestId);
        Request request = requestOptional.orElseThrow(() -> new RuntimeException("Request Not Found"));

        return RequestMapper.toRequestDTO(
                request.getRequestId(),
                request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                request.getRole(),
                request.getRequestToken());

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
                            request.getRequestId(),
                            request.getFirstname(),
                            request.getLastname(),
                            request.getEmail(),
                            request.getRole(),
                            request.getRequestToken());
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
                                request.getRequestId(),
                                request.getFirstname(),
                                request.getLastname(),
                                request.getEmail(),
                                request.getRole(),
                                request.getRequestToken());
                    } else {
                        return null;
                    }
                })
                .toList();
    }

    @Override
    public void deleteRequestByRequestId(Long requestId) {
        requestRepository.deleteRequestByRequestId(requestId);
        requestRepository.flush();
    }

    @Override
    public boolean validateRequest(String token) {
        Optional<Request> requestOptional = requestRepository.findByRequestToken(token);    
        System.out.println("\n\n\n\n\n\n\n\n" + requestOptional.get().getExpiresAt() + "\n\n\n\n\n\n\n\n" + System.currentTimeMillis());
        return requestOptional.isPresent() && requestOptional.get().getExpiresAt() > System.currentTimeMillis();
    }

}
