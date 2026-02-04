package az.codeworld.springboot.utilities.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.stereotype.Component;

import jakarta.persistence.PrePersist;

@Component
public class UsernameGenerator {

    // private static long counter = 0;

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final char[] PREFIX = {'S', 'T', 'A'};

    private static final char[] ALNUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    @PrePersist
    public static String generateUsername(String role) {
        StringBuilder stringBuilder = new StringBuilder(14);

        switch (role.toUpperCase(Locale.ROOT)) {
            case "TEACHER":
                stringBuilder.append(PREFIX[1]);
                break;
            case "ADMIN":
                stringBuilder.append(PREFIX[2]);
                break;
            case "STUDENT":
                stringBuilder.append(PREFIX[0]);
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
        
        stringBuilder.append('-');
        appendRandom(stringBuilder, 4);
        stringBuilder.append('-');
        appendRandom(stringBuilder, 4);
        stringBuilder.append('-');
        appendRandom(stringBuilder, 1);

        // stringBuilder.append(counter);
        // counter++;
        //appendRandom(stringBuilder, 1);

        return stringBuilder.toString();
    }

    private static void appendRandom(StringBuilder stringBuilder, int count) {
        for (int i = 0; i<count; i++) {
            stringBuilder.append(ALNUM[secureRandom.nextInt(ALNUM.length)]);
        }
    }
}


