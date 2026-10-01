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
    public static final ZonedDateTime FUTURE_START = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-01T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsADraftEventWithValidData() {

        Event event = Event.create("Rock Night", FUTURE_START, 500, FIXED_CLOCK);

        assertThat(event.name()).isEqualTo("Rock Night");
        assertThat(event.start()).isEqualTo(FUTURE_START);
        assertThat(event.capacity()).isEqualTo(500);
        assertThat(event.status()).isEqualTo(EventStatus.DRAFT);
    }

    @Test
    void rejectsZeroCapacity() {

        assertThatThrownBy(() -> Event.create("Rock Night", FUTURE_START, 0, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");
    }

    @Test
    void acceptsMinimumCapacityOfOne() {

        Event event = Event.create("Rock Night", FUTURE_START, 1, FIXED_CLOCK);
        assertThat(event.capacity()).isEqualTo(1);

    }

    @Test
    void acceptsMaximumCapacityOf100000() {

        Event event = Event.create("Rock Night", FUTURE_START, 100_000, FIXED_CLOCK);
        assertThat(event.capacity()).isEqualTo(100_000);

    }

    @Test
    void rejectsCapacityAboveMaximum() {

        assertThatThrownBy(() -> Event.create("Rock Night", FUTURE_START, 100_001, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");

    }

    @Test
    void rejectsStartEqualToNow() {
        ZonedDateTime start = ZonedDateTime.now(FIXED_CLOCK);

        assertThatThrownBy(() -> Event.create("Rock Night", start, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("start");
    }

    @Test
    void acceptsStartOneSecondAfterNow() {
        ZonedDateTime start = ZonedDateTime.now(FIXED_CLOCK).plusSeconds(1);

        Event event = Event.create("Rock Night", start, 500, FIXED_CLOCK);
        assertThat(event.start()).isEqualTo(start);
    }

    @Test
    void eventBlankNameIsRejected() {

        assertThatThrownBy(() -> Event.create("", FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void eventWithEventNameValidLengthIsAccepted() {
        Event event = Event.create("a".repeat(Event.VALID_EVENT_NAME_LENGTH), FUTURE_START, 500, FIXED_CLOCK);
        assertThat(event.name().length()).isEqualTo(Event.VALID_EVENT_NAME_LENGTH);

    }

    @Test
    void rejectMaximumEventNameLength() {
        assertThatThrownBy(() -> Event.create("a".repeat(Event.VALID_EVENT_NAME_LENGTH + 1) , FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum event name");
    }
}
