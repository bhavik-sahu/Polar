package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotNull(message = "Role is required")
        private UserRole role;

        private StationName station;

        public RegisterRequest() {}
        public RegisterRequest(String username, String password, UserRole role, StationName station) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.station = station;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        public StationName getStation() { return station; }
        public void setStation(StationName station) { this.station = station; }
    }

    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private UserDto user;

        public AuthResponse() {}
        public AuthResponse(String token, String type, UserDto user) {
            this.token = token;
            this.type = type != null ? type : "Bearer";
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String token;
            private String type = "Bearer";
            private UserDto user;

            public Builder token(String token) { this.token = token; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder user(UserDto user) { this.user = user; return this; }

            public AuthResponse build() {
                return new AuthResponse(token, type, user);
            }
        }
    }

    public static class UserDto {
        private UUID id;
        private String username;
        private UserRole role;
        private StationName station;

        public UserDto() {}
        public UserDto(UUID id, String username, UserRole role, StationName station) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.station = station;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        public StationName getStation() { return station; }
        public void setStation(StationName station) { this.station = station; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String username;
            private UserRole role;
            private StationName station;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder username(String username) { this.username = username; return this; }
            public Builder role(UserRole role) { this.role = role; return this; }
            public Builder station(StationName station) { this.station = station; return this; }

            public UserDto build() {
                return new UserDto(id, username, role, station);
            }
        }
    }
}
