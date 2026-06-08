package com.yasirakbal.secureloanapi.feature.audit.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;

public class RequestInfoUtils {

    public static RequestInfo extract(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        return RequestInfo.builder()
                .ipAddress(getClientIP(request))
                .userAgent(userAgent)
                .browser(parseBrowser(userAgent))
                .os(parseOS(userAgent))
                .device(parseDevice(userAgent))
                .build();
    }

    private static String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        String cfHeader = request.getHeader("CF-Connecting-IP");
        if (cfHeader != null && !cfHeader.isEmpty()) {
            return cfHeader;
        }

        String realIP = request.getHeader("X-Real-IP");
        if (realIP != null && !realIP.isEmpty()) {
            return realIP;
        }

        return request.getRemoteAddr();
    }

    private static String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Opera") || userAgent.contains("OPR")) return "Opera";
        return "Unknown";
    }

    private static String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Unknown";
    }

    private static String parseDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile") || userAgent.contains("Android")) return "Mobile";
        if (userAgent.contains("Tablet") || userAgent.contains("iPad")) return "Tablet";
        return "Desktop";
    }

    @Data
    @Builder
    public static class RequestInfo {
        private String ipAddress;
        private String userAgent;
        private String browser;
        private String os;
        private String device;
    }
}