package com.learning.ratingdataservice.resources;

import com.learning.ratingdataservice.model.Rating;
import com.learning.ratingdataservice.model.UserRating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/ratingsdata")
public class RatingsResource {

    @RequestMapping("/{movieId}")
    public Rating getRating(@PathVariable("movieId") String movieId) {
        return new Rating(movieId, 4);
    }


    @RequestMapping("users/{movieId}")
    public UserRating getUserRating(@PathVariable("movieId") String movieId) {

        List<Rating> ratings= Arrays.asList(
                new Rating("12",4),
                new Rating("34",5)
        );
        UserRating userRating = new UserRating();
        userRating.setUserRating(ratings);

        return userRating;
    }
}
