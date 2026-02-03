package az.codeworld.springboot.web.repositories;

import org.springframework.stereotype.Repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class GenericWebRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> T findById(Long id, Class<T> type) {
        return entityManager.find(type, id);
    }

    public <T> List<T> findAll(Class<T> type) {
        return entityManager
                            .createQuery("SELECT u FROM User u", type)
                            .getResultList();
    }

    public <T> boolean exists(T t) {
        return entityManager.contains(t);
    }

    @Transactional
    public <T> void save(Class<T> type,T t) {
        entityManager.persist(t);
        entityManager.flush();
    }

    @Transactional
    public <T> T update(T t) {
        return entityManager.merge(t);
    }

    @Transactional 
    public <T> void delete(T t) {
        entityManager.remove(entityManager.contains(t) ? t : entityManager.merge(t));
    }
}