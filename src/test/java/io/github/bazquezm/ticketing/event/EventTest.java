package io.github.bazquezm.ticketing.event;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class EventTest {

    private static final ZoneId MEXICO_CITY = ZoneId.of("America/Mexico_City");
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-01T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsADraftEventWithValidData(){

        ZonedDateTime start = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);

        Event event = Event.create("Rock Night", start, 500, FIXED_CLOCK);

        assertThat(event.name()).isEqualTo("Rock Night");
        assertThat(event.start()).isEqualTo(start);
        assertThat(event.capacity()).isEqualTo(500);
        assertThat(event.status()).isEqualTo(EventStatus.DRAFT);
    }

    @Test
    void createsADraftEventWithZeroCapacity(){

        ZonedDateTime start = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);

        assertThatThrownBy(() -> Event.create("Rock Night", start, 0, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("capacity");
    }

    @Test
    void createsADraftEventWithMinimumCapacity(){

        ZonedDateTime start = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);

        Event event = Event.create("Rock Night", start, 1, FIXED_CLOCK);
        assertThat(event.capacity()).isEqualTo(1);

    }

    @Test
    void createsADraftEventWithMaxCapacity(){

        ZonedDateTime start = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);

        Event event = Event.create("Rock Night", start, 100_000, FIXED_CLOCK);
        assertThat(event.capacity()).isEqualTo(100_000);

    }

    @Test
    void createsADraftEventWithOverduedCapacity(){

        ZonedDateTime start = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);

        assertThatThrownBy(() -> Event.create("Rock Night", start, 100_001, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");

    }

}
