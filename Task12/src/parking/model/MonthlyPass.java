package parking.model;

import java.time.LocalDate;

/** Monthly pass for students and staff only. */
public class MonthlyPass implements Pass {
    private final String holderId;
    private final UserCategory category;
    private final LocalDate validFrom;
    private final LocalDate validTo;

    public MonthlyPass(String holderId, UserCategory category, LocalDate validFrom, LocalDate validTo) {
        if (holderId == null || holderId.isBlank())
            throw new IllegalArgumentException("Pass holder id is required");
        if (category == null || category == UserCategory.VISITOR)
            throw new IllegalArgumentException("Monthly pass is only for students and staff");
        if (validFrom == null || validTo == null || validTo.isBefore(validFrom))
            throw new IllegalArgumentException("Invalid pass dates");
        this.holderId = holderId;
        this.category = category;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public UserCategory getCategory() { return category; }

    @Override public boolean isValidOn(LocalDate date) {
        return !date.isBefore(validFrom) && !date.isAfter(validTo);
    }

    @Override public String describe() {
        return "MonthlyPass[" + holderId + ", " + validFrom + " to " + validTo + "]";
    }
}
