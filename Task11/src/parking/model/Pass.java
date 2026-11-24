package parking.model;

import java.time.LocalDate;

/** Abstraction for anything that can make parking free (or fixed price) on a date. */
public interface Pass {
    boolean isValidOn(LocalDate date);

    String describe();

    /** Amount charged at the gate while this pass is valid. Monthly pass: sold elsewhere, so 0. */
    default double getPrice() { return 0; }
}
