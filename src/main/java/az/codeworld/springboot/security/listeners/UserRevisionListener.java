package az.codeworld.springboot.security.listeners;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import az.codeworld.springboot.security.entities.UserRevisionEntity;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevisionEntity userRevisionEntity = (UserRevisionEntity) revisionEntity;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (authentication == null || !authentication.isAuthenticated() ? "SYSTEM" : authentication.getName());
        
        userRevisionEntity.setUserName(username);
    }
    
}
