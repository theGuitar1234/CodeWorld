// package az.codeworld.springboot.security.repositories;

// import java.util.List;

// import org.hibernate.envers.AuditReader;
// import org.hibernate.envers.AuditReaderFactory;
// import org.hibernate.envers.query.AuditEntity;
// import org.springframework.stereotype.Repository;

// import az.codeworld.springboot.admin.entities.User;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;

// @Repository
// public class UserRevisionRepository {
    
//     @PersistenceContext
//     private EntityManager entityManager;

//     private final AuditReader auditReader = AuditReaderFactory.get(entityManager);

//     public User findRevision(Long userId, int revisionNumber) {
//         return auditReader.find(User.class, userId, revisionNumber);
//     }

//     public List<Number> findRevision(Long userId) {
//         return auditReader.getRevisions(User.class, userId);
//     }

//     public List<?> findRevisionRows(Long userId) {
//         return auditReader.createQuery()
//             .forRevisionsOfEntityWithChanges(User.class, true)
//             .add(AuditEntity.id().eq(userId))
//             .getResultList();
//     }
// }
