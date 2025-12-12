package az.codeworld.springboot.web.services.serviceImpl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.web.repositories.GenericWebRepository;
import az.codeworld.springboot.web.services.GenericWebService;

@Service
@Profile("dev")
public class GenericWebServiceImplDev implements GenericWebService {

    private final GenericWebRepository genericWebRepository;
    
    public GenericWebServiceImplDev(
        GenericWebRepository genericWebRepository
    ) {
        this.genericWebRepository = genericWebRepository;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public <T> void saveType(Class<T> type, T t) {
        genericWebRepository.save(t);
    }
    
}
