package com.example.javaandroidsmartca;

import java.io.Serializable;

public class CallbackResult implements Serializable {

    private final String credentialId;
    private final String accessToken;

    public CallbackResult(String credentialId, String accessToken) {
        this.credentialId = credentialId;
        this.accessToken = accessToken;
    }

    // Getters for credentialId and accessToken
    public String getCredentialId() {
        return credentialId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
