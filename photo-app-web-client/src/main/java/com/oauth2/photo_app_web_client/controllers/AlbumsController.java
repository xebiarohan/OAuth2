package com.oauth2.photo_app_web_client.controllers;

import com.oauth2.photo_app_web_client.response.AlbumsDto;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class AlbumsController {

    @GetMapping("/albums")
    public String getAlbums(Model model) {
        AlbumsDto albumsDto1 = new AlbumsDto();
        albumsDto1.setUserId("1");
        albumsDto1.setAlbumTitle("Random");
        albumsDto1.setAlbumId("123");

        AlbumsDto albumsDto = new AlbumsDto();
        albumsDto.setUserId("2");
        albumsDto.setAlbumTitle("Random 2");
        albumsDto.setAlbumId("123");

        model.addAttribute("albums", Arrays.asList(albumsDto, albumsDto1));

        return "albums";
    }
}
