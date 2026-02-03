package az.codeworld.springboot.web.services.serviceImpl;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.web.repositories.GenericWebRepository;
import az.codeworld.springboot.web.services.GenericWebService;

@Service
public class GenericWebServiceImpl implements GenericWebService {

    private final GenericWebRepository genericWebRepository;
    
    public GenericWebServiceImpl(
        GenericWebRepository genericWebRepository
    ) {
        this.genericWebRepository = genericWebRepository;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public <T> void saveType(Class<T> type, T t) {
        genericWebRepository.save(type, t);
    }

    @Override
    public <T> T getById(Class<T> type, Long id) {
        return genericWebRepository.findById(id, type);
    }
    
}
