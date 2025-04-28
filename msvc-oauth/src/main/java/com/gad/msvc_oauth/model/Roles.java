package com.gad.msvc_oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class Roles {
    @JsonProperty("role_name")
    private String roleName;
    Set<String> permissions;
}
