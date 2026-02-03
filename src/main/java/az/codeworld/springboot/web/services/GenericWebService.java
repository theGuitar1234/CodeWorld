package az.codeworld.springboot.web.services;

public interface GenericWebService {
    
    void defaultMethod();

    <T> void saveType(Class<T> type, T t);

    <T> T getById(Class<T> type, Long id);
}
