package az.codeworld.springboot.security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import az.codeworld.springboot.security.listeners.UserRevisionListener;

@Getter
@Setter
@Entity
@Table(name = "REVISION_INFO")
@RevisionEntity(UserRevisionListener.class)
/*
    With the latest versions of Hibernate Envers, 
    extends DefaultRevisionEntity is not possible anymore 
    as the class is now final.
*/
@NoArgsConstructor
@AllArgsConstructor
public class UserRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long userRevisionEntityId;

    @RevisionTimestamp
    private long timestamp;
    
    @Column(name = "username")
    private String userName;
}
