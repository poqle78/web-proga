package funn.j2k.web.student.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record StudentSearchRequest (
        @Min(100000) @Max(999999) Integer isu,
        @Size(min=2) String name,
        @Pattern(regexp="[A-Z][1-9][1-4]\\d{2}") String group,
        @Min(1) @Max(99) Integer dormitory,
        @Min(1) @Max(9999) Integer room,
        @JsonbDateFormat("yyyy-MM-dd") @JsonbProperty("check_in") @PastOrPresent LocalDate checkIn,
        @JsonbProperty("is_foreigner") Boolean is_foreigner,
        String notes,
        @Pattern(regexp="isu|name|group|dormitory|room|checkIn") String sortBy,
        @Pattern(regexp="asc|desc") String sortDirection,
        @Min(0) @Max(100_000) Integer page,
        @Min(1) @Max(100) Integer size
) {
    public StudentSearchRequest {
        name = blankToNull(name);
        group = blankToNull(group);
        notes = blankToNull(notes);
        sortBy = blankToNull(sortBy);
        sortDirection = blankToNull(sortDirection);
        if (page == null) page = 0;
        if (size == null) size = 20;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
