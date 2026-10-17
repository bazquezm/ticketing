package io.github.bazquezm.ticketing.event;

import java.util.Objects;

public record Seat(String row , int number ) {

    public Seat {
        Objects.requireNonNull(row,"row is required");

        if ( !row.matches("[A-Z]") ){
            throw new IllegalArgumentException("row must be one capital letter");
        }

        if ( number < 1 ){
            throw new IllegalArgumentException("number must be greater than 0 ");
        }
    }

}
