package org.example.model;

import java.sql.Timestamp;

public record Activity_Logs(
        int logId,
        int userId,
        String action,
        Timestamp timestamp
) {}
