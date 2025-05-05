package org.example.educonnectjavaproject.security;

import java.security.SecureRandom;

public class PasswordGenerationService {

    private static final SecureRandom random = new SecureRandom();
    private static final int PASSWORD_LENGTH = 10;

    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder();

        while (password.length() < PASSWORD_LENGTH) {
            char c = (char) (random.nextInt(94) + 33);
            if (isValidChar(c)) {
                password.append(c);
            }
        }

        return password.toString();
    }

    private boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || "!@#$%&*?".indexOf(c) >= 0;
    }
}
