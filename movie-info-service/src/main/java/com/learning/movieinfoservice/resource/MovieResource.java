package com.learning.movieinfoservice.resource;

import com.learning.movieinfoservice.models.Movie;
import com.learning.movieinfoservice.models.MovieSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.Collections;
import java.util.List;

@RestController
@PropertySource("classpath:application.properties")
@RequestMapping("/movies")
public class MovieResource {

   // @Value("${api.key}")
    //private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/{movieId}")
    public Movie getMovie(@PathVariable("movieId") String movieId){


        MovieSummary movieSummary = restTemplate.getForObject("https://api.themoviedb.org/3/movie/"+movieId+"?api_key=3f5229f92696793e42f768fab2bc91f7",
                MovieSummary.class);

        return new Movie(movieId,movieSummary.getTitle(),movieSummary.getOverview());

        }
}
