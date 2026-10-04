package io.github.bazquezm.ticketing.event;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        Objects.requireNonNull(value , "id value is required");
    }

    public static EventId newId(){
        return new EventId(UUID.randomUUID());
    }

}
