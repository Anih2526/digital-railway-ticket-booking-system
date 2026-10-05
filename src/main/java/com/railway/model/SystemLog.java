package com.railway.model;

import java.time.LocalDateTime;

public class SystemLog {

    private String action;
    private String message;
    private String email;
    private LocalDateTime logTime;

    public SystemLog(String action,
                     String message,
                     String email,
                     LocalDateTime logTime) {
        this.action = action;
        this.message = message;
        this.email = email;
        this.logTime = logTime;
    }

    public String getAction() { return action; }
    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public LocalDateTime getLogTime() { return logTime; }
}
