package az.codeworld.springboot.utilities.configurations;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private Presence presence = new Presence();
    private Login login = new Login();
    private Otp otp = new Otp();
    private RememberMe rememberMe = new RememberMe();
    private Time time = new Time();
    private Payriff payriff = new Payriff();

    public Payriff getPayriff() { return payriff; }
    public void setPayriff(Payriff payriff) { this.payriff = payriff; }

    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }

    public Presence getPresence() { return presence; }
    public void setPresence(Presence presence) { this.presence = presence; }

    public Login getLogin() { return login; }
    public void setLogin(Login login) { this.login = login; }

    public Otp getOtp() { return otp; }
    public void setOtp(Otp otp) { this.otp = otp; }

    public RememberMe getRememberMe() { return rememberMe; }
    public void setRememberMe(RememberMe rememberMe) { this.rememberMe = rememberMe; }

    public static class Presence {
        private long touchThrottleSeconds;
        private long onlineWindowMinutes;

        public long getTouchThrottle() { return touchThrottleSeconds; }
        public void setTouchThrottle(long touchThrottleSeconds) { this.touchThrottleSeconds = touchThrottleSeconds; }

        public long getOnlineWindow() { return onlineWindowMinutes; }
        public void setOnlineWindow(long onlineWindowMinutes) { this.onlineWindowMinutes = onlineWindowMinutes; }
    }

    public static class Login {
        private Attempts attempts = new Attempts();
        private Block block = new Block();
        private Password password = new Password();

        public Attempts getAttempts() { return attempts; }
        public void setAttempts(Attempts attempts) { this.attempts = attempts; }

        public Block getBlock() { return block; }
        public void setBlock(Block block) { this.block = block; }

        public Password getPassword() { return password; }
        public void setPassword(Password password) { this.password = password; }
    }

    public static class Attempts {
        private int threshold = 3;

        public int getThreshold() { return threshold; }
        public void setThreshold(int threshold) { this.threshold = threshold; }
    }

    public static class Block {
        private long expiryMinutes;

        public long getExpiryMinutes() { return expiryMinutes; }
        public void setExpiryMinutes(long expiryMinutes) { this.expiryMinutes = expiryMinutes; }
    }

    public static class Password {
        private String placeholder;

        public String getPlaceholder() { return placeholder; }
        public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    }

    public static class Otp {
        private long expiryMinutes;

        public long getExpiryMinutes() { return expiryMinutes; }
        public void setExpiryMinutes(long expiryMinutes) { this.expiryMinutes = expiryMinutes; }
    }

    public static class RememberMe {
        private int expirySeconds;

        public int getExpirySeconds() { return expirySeconds; }

        public void setExpirySeconds(int expirySeconds) { this.expirySeconds = expirySeconds; }
    }

    public static class Time {
        private String zone;
        private String dateTimeFormat;

        public String getZone() { return zone; }

        public void setZone(String zone) { this.zone = zone; }

        public String getDateTimeFormat() { return dateTimeFormat; }

        public void setDateTimeFormat(String dateTimeFormat) { this.dateTimeFormat = dateTimeFormat; }
        
    }

    public static class Payriff {
        private String secretKey;

        public String getSecretKey() { return secretKey; }
        
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        
        private String baseUrl;
        
        public String getBaseUrl() { return baseUrl; }
        
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    }


}
