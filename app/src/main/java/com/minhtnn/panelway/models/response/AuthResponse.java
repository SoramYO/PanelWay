package com.minhtnn.panelway.models.response;

import com.google.gson.annotations.SerializedName;
import com.minhtnn.panelway.models.User;

public class AuthResponse {
    @SerializedName("accountResponse")
    private User accountResponse;
    
    @SerializedName("jwtToken")
    private String jwtToken;

    public User getAccountResponse() {
        return accountResponse;
    }

    public void setAccountResponse(User accountResponse) {
        this.accountResponse = accountResponse;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    
    // Legacy support for existing code
    public String getToken() {
        return jwtToken;
    }
    
    public User getUser() {
        return accountResponse;
    }
    
    public boolean isSuccess() {
        return jwtToken != null && !jwtToken.isEmpty();
    }
    
    public String getMessage() {
        return isSuccess() ? "Login successful" : "Login failed";
    }
}