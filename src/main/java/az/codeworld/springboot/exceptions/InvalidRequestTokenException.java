package az.codeworld.springboot.exceptions;

import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;

public class InvalidRequestTokenException extends InvalidOneTimeTokenException {

    public InvalidRequestTokenException(String msg) {
        super(msg);
    }
}
