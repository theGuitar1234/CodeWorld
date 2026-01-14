package az.codeworld.springboot.admin.services.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.repositories.RequestRepository;
import az.codeworld.springboot.admin.services.RequestCleanUpService;

@Service
public class RequestCleanUpServiceImplDev implements RequestCleanUpService {

    private Logger log = LoggerFactory.getLogger(RequestCleanUpServiceImplDev.class);

    private final RequestRepository requestRepository;

    public RequestCleanUpServiceImplDev(
        RequestRepository requestRepository
    ) {
        this.requestRepository = requestRepository;
    }

    @Override
    public void deleteExpiredRequests() {}
}
