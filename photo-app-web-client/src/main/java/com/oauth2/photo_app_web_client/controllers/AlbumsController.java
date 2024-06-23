package com.oauth2.photo_app_web_client.controllers;

import com.oauth2.photo_app_web_client.response.AlbumsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Controller
public class AlbumsController {

    @Autowired
    OAuth2AuthorizedClientService oauth2ClientService;

    	@Autowired
        RestTemplate restTemplate;

    @GetMapping("/albums")
    public String getAlbums(Model model, @AuthenticationPrincipal OidcUser principal) {

        OidcIdToken idToken = principal.getIdToken();
        String tokenValue = idToken.getTokenValue();

        String url = "http://localhost:8099/albums";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");

        restTemplate.exchange(url, HttpMethod.GET,)

//        AlbumsDto albumsDto1 = new AlbumsDto();
//        albumsDto1.setUserId("1");
//        albumsDto1.setAlbumTitle("Random");
//        albumsDto1.setAlbumId("123");

//
//        AlbumsDto albumsDto = new AlbumsDto();
//        albumsDto.setUserId("2");
//        albumsDto.setAlbumTitle("Random 2");
//        albumsDto.setAlbumId("123");

        model.addAttribute("albums", Arrays.asList(albumsDto, albumsDto1));

        return "albums";
    }
}
