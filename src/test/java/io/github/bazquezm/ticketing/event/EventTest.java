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
    private static final ZonedDateTime FUTURE_START = ZonedDateTime.of(2026, 12, 5, 20, 0, 0, 0, MEXICO_CITY);
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
    void rejectsBlankName() {

        assertThatThrownBy(() -> Event.create("       ", FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void acceptsNameOf100Characters() {
        Event event = Event.create("a".repeat(100), FUTURE_START, 500, FIXED_CLOCK);
        assertThat(event.name().length()).isEqualTo(100);

    }

    @Test
    void rejectsNameOf101Characters() {
        assertThatThrownBy(() -> Event.create("a".repeat(101) , FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name must be at most 100 characters, but was: 101");
    }

    @Test
    void rejectsNullName() {
        assertThatThrownBy(() -> Event.create(null , FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
    }

    @Test
    void rejectsNullStart() {
        assertThatThrownBy(() -> Event.create("Rock Night" , null, 500, FIXED_CLOCK))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("start");
    }

    @Test
    void rejectsEmptyName() {
        assertThatThrownBy(() -> Event.create("" , FUTURE_START, 500, FIXED_CLOCK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void createsEventWithAnId(){

        Event event = Event.create("Rock Night", FUTURE_START, 500, FIXED_CLOCK);
        assertThat(event.id()).isNotNull();

    }

    @Test
    void restoresAnEventWhoseStartIsInThePast(){
        ZonedDateTime PAST_START = ZonedDateTime.of(2025, 12, 5, 20, 0, 0, 0, MEXICO_CITY);
        EventId id = EventId.newId();
        Event event = Event.restore(id, "Rock Night", EventStatus.DRAFT, PAST_START, 500);
        assertThat(event.id()).isNotNull();

    }

    @Test
    void eventsWithTheSameIdAreEqual(){

        EventId id = EventId.newId();
        Event event = Event.restore(id,"Rock Night",EventStatus.DRAFT, FUTURE_START, 500);
        Event event2 = Event.restore(id,"Jazz Night",EventStatus.DRAFT, FUTURE_START, 500);

        assertThat(event).isEqualTo(event2).hasSameHashCodeAs(event2);

    }

    @Test
    void eventsWithTheSameDataButDifferentIdsAreNotEqual(){

        Event event = Event.restore(EventId.newId(),"Rock Night",EventStatus.DRAFT, FUTURE_START, 500);
        Event event2 = Event.restore(EventId.newId(),"Jazz Night",EventStatus.DRAFT, FUTURE_START, 500);


        assertThat(event).isNotEqualTo(event2);

    }

    @Test
    void rejectsNullId(){

        assertThatThrownBy(() -> Event.restore(null,"a".repeat(101) , EventStatus.DRAFT, FUTURE_START, 500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id is required");
    }

}
