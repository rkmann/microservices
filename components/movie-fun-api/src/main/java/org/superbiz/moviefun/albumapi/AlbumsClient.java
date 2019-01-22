package org.superbiz.moviefun.albumapi;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import org.superbiz.moviefun.moviesapi.MovieInfo;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;

public class AlbumsClient {

    private String albumsUrl;
    private RestOperations restOperations;


    private static ParameterizedTypeReference<List<Album>> albumListType = new ParameterizedTypeReference<List<Album>>() {
    };

    public AlbumsClient(String albumsUrl, RestOperations restOperations) {
        this.albumsUrl = albumsUrl;
        this.restOperations = restOperations;
    }

    public void setUp(){
        AlbumFixtures albumFixtures = new AlbumFixtures();
        List<Album> albums = albumFixtures.load();
        for(Album album : albums){
            this.addAlbum(album);
        }
    }

    public void addAlbum(Album album) {
        restOperations.postForEntity(albumsUrl, album, Album.class);
    }

    public void deleteAlbumId(Long albumId) {
        restOperations.delete(albumsUrl + "/" + albumId);
    }

    public int countAll() {
        return restOperations.getForObject(albumsUrl + "/count", Integer.class);
    }


    public int count(String field, String key) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl + "/count")
                .queryParam("field", field)
                .queryParam("key", key);

        return restOperations.getForObject(builder.toUriString(), Integer.class);
    }


    public List<Album> findAll(int start, int pageSize) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl)
                .queryParam("start", start)
                .queryParam("pageSize", pageSize);

        return restOperations.exchange(builder.toUriString(), GET, null, albumListType).getBody();
    }

    public List<Album> findRange(String field, String key, int start, int pageSize) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(albumsUrl)
                .queryParam("field", field)
                .queryParam("key", key)
                .queryParam("start", start)
                .queryParam("pageSize", pageSize);

        return restOperations.exchange(builder.toUriString(), GET, null, albumListType).getBody();
    }

    public List<Album> getAlbums() {
        return restOperations.exchange(albumsUrl, GET, null, albumListType).getBody();
    }
}