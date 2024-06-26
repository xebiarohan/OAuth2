package com.oauth2.photo_app_web_client.controllers;

import com.oauth2.photo_app_web_client.response.AlbumsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Controller
public class AlbumsController {

    @Autowired
    OAuth2AuthorizedClientService oauth2ClientService;

    @Autowired
    WebClient webClient;


    @GetMapping("/albums")
    public String getAlbums(Model model, @AuthenticationPrincipal OidcUser principal) {

        String url = "http://localhost:8099/albums";

        List<AlbumsDto> albums = webClient.get().uri(url).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AlbumsDto>>(){}).block();

        model.addAttribute("albums", albums);
        return "albums";
    }
}
