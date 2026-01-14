package az.codeworld.springboot.utilities.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloudflare.images")
public class CloudFlareProperties {
    private String accountId;
    private String apiToken;
    private String accountHash;
    private String defaultVariant;

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getApiToken() { return apiToken; }
    public void setApiToken(String apiToken) { this.apiToken = apiToken; }

    public String getAccountHash() { return accountHash; }
    public void setAccountHash(String accountHash) { this.accountHash = accountHash; }

    public String getDefaultVariant() { return defaultVariant; }
    public void setDefaultVariant(String defaultVariant) { this.defaultVariant = defaultVariant; }
}
