package io.github.bazquezm.ticketing.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SeatTest {

    @Test
    void createsASeatWithRowAndNumber(){

        Seat seat = new Seat("A",1);

        assertThat(seat.row()).isEqualTo("A");
        assertThat(seat.number()).isEqualTo(1);
    }

    @Test
    void rejectsNullRow(){

        assertThatThrownBy(() -> new Seat(null, 1 ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("row");
    }

    @Test
    void rejectsARowThatIsNotOneCapitalLetter(){
        assertThatThrownBy(() -> new Seat("AB", 1 ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("row");
    }

    @Test
    void rejectsARowThatIsNotOneCapitalLetterAndReceiveNumber(){
        assertThatThrownBy(() -> new Seat("1", 1 ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("row");
    }

    @Test
    void rejectsARowThatIsNotOneCapitalLetterAndReceiveStringLowerCase(){
        assertThatThrownBy(() -> new Seat("a", 1 ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("row");
    }

    @Test
    void rejectsSeatNumberZero(){

        assertThatThrownBy(() -> new Seat("A", 0 ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("number must be");
    }

    @Test
    void acceptsSeatNumberOne (){
        Seat seat = new Seat("B",1);

        assertThat(seat.number()).isEqualTo(1);
    }
}
