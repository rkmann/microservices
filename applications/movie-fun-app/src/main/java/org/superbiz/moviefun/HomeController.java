package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestOperations;
import org.superbiz.moviefun.albumapi.AlbumsClient;
import org.superbiz.moviefun.moviesapi.MoviesClient;

import java.util.Map;

@Controller
public class HomeController {

    private MoviesClient moviesClient;

    private AlbumsClient albumsClient;

    private RestOperations restOperations;

    @Value("${movies.url}")
    private String moviesUrl;

    @Value("${albums.url}")
    private String albumsUrl;

    public HomeController(RestOperations restOperations) {
        this.restOperations = restOperations;
        this.moviesClient = new MoviesClient(moviesUrl, restOperations);
        this.albumsClient = new AlbumsClient(albumsUrl,restOperations);

    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        this.moviesClient.setUp();
        this.albumsClient.setUp();

        model.put("movies", moviesClient.getMovies());
        model.put("albums", albumsClient.getAlbums());

        return "setup";
    }
}
