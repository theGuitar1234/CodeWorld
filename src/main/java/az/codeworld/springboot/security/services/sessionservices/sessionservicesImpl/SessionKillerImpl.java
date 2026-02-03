package az.codeworld.springboot.security.services.sessionservices.sessionservicesImpl;

import java.security.Principal;
import java.util.List;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
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
    public void invalidateUserSessions(String userName) {

        for (Object principal : sessionRegistry.getAllPrincipals()) {

            String principalName = (principal instanceof UserDetails ud ? ud.getUsername() : principal.toString());
                
            if (!userName.equals(principalName)) continue;

            sessionRegistry.getAllSessions(principal, false)
            .forEach(sessionInformation -> sessionInformation.expireNow());
        }
    }
    
}
