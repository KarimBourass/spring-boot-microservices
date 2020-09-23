package com.learning.moviecatalogservice.resources;

import com.learning.moviecatalogservice.models.CatalogItem;
import com.learning.moviecatalogservice.models.Movie;
import com.learning.moviecatalogservice.models.Rating;
import com.learning.moviecatalogservice.models.UserRating;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import sun.dc.pr.PRError;

import javax.websocket.server.PathParam;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class CatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    //@Autowired
    //private WebClient.Builder webClientBuidler;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathParam("userId") String userId){


        //get All rated movie Ids
        UserRating ratings = restTemplate.getForObject("http://rating-data-service/ratingsdata/users/"+userId,UserRating.class);

        return ratings.getUserRating().stream().map( rating -> {
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(),Movie.class);

//            Movie movie = webClientBuidler.build()
//                    .get()
//                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
//                    .retrieve()
//                    .bodyToMono(Movie.class)    //Convert the data we get to a movie class , Mono = a promise that we will return the data
//                    .block(); // wait till we return a data (we did that because of mono asynchronus)
            //put them all together
            return new CatalogItem(movie.getName(), movie.getDescription(),rating.getRating());
        })

                .collect(Collectors.toList());


    }
}
