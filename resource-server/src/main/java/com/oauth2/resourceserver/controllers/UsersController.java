package com.oauth2.resourceserver.controllers;

import com.oauth2.resourceserver.response.UserResponse;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @GetMapping("/status/check")
    public String status() {
        return "working";
    }

    @PreAuthorize("hasAuthority('ROLE_developer') or #id == #jwt.subject")
    //@Secured("ROLE_developer")
    @DeleteMapping(path = "/{id}")
    public String deleteUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return "Deleted user with id " + id + " and JWT subject " + jwt.getSubject();
    }

    @PostAuthorize("returnObject.id == #jwt.subject")
    @GetMapping(path = "/{id}")
    public UserResponse getUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return new UserResponse("Rohan", "Aggarwal","60df8722-f5ab-44c6-b568-86d353caa53f");
    }
}
