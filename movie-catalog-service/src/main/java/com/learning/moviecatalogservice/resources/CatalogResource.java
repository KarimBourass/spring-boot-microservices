package com.learning.moviecatalogservice.resources;

import com.learning.moviecatalogservice.models.CatalogItem;
import com.learning.moviecatalogservice.models.Movie;
import com.learning.moviecatalogservice.models.Rating;
import com.learning.moviecatalogservice.models.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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
    RestTemplate restTemplate;

    //@Autowired
    //private WebClient.Builder webClientBuidler;

    @RequestMapping("/{userId}")
    @HystrixCommand(
            fallbackMethod = "getFallBackCatalog",
            threadPoolKey = "movieCatalogPool",
            commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000") // time out here 3s
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize",value = "20"), // how many threads you want to be wating for response(here 20)
                    @HystrixProperty(name = "maxQueueSize",value = "10") // How many request you want wating in the Queue before they get acces
    })
    public List<CatalogItem> getCatalog(@PathParam("userId") String userId){

        //get All rated movie Ids
        UserRating ratings = getUserRating(userId);

        return ratings.getUserRating().stream().map( rating -> { return getCatalogItem(rating); })
                .collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "getFallBackCatalogItem")
    private CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+ rating.getMovieId(),Movie.class);
        return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
    }

    @HystrixCommand(fallbackMethod = "getFallBackUserRating")
    private UserRating getUserRating(String userId) {
        return restTemplate.getForObject("http://rating-data-service/ratingsdata/users/" + userId, UserRating.class);
    }

    public List<CatalogItem> getFallBackCatalog(@PathParam("userId") String userId){
        System.out.println("getFallBackCatalog --------------------");
        return Arrays.asList(new CatalogItem("No movie","No description",0));
    }
}


//            Movie movie = webClientBuidler.build()
//                    .get()
//                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
//                    .retrieve()
//                    .bodyToMono(Movie.class)    //Convert the data we get to a movie class , Mono = a promise that we will return the data
//                    .block(); // wait till we return a data (we did that because of mono asynchronus)
