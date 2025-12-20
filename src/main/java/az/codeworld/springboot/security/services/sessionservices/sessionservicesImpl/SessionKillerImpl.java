package az.codeworld.springboot.security.services.sessionservices.sessionservicesImpl;

import java.util.List;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.services.sessionservices.SessionKiller;

@Service
public class SessionKillerImpl implements SessionKiller {

    private final SessionRegistry sessionRegistry;

    public SessionKillerImpl(
        SessionRegistry sessionRegistry
    ) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void invalidateUserSessions(String username) {
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, false);
        sessions.forEach(sessionInformation -> sessionInformation.expireNow());
    }
    
}
