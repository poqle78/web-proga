package funn.j2k.web.student.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateStudentRequest(
        @Min(100000) @Max(999999) Integer isu,
        @Size(min=2) String name,
        @Size(min=5) @Pattern(regexp="[A-Z][1-9][1-4]\\d{2}") String group,
        @Min(1) @Max(99) Integer dormitory,
        @Min(1) @Max(9999) Integer room,
        @JsonbProperty("check_in") @JsonbDateFormat("yyyy-MM-dd") @PastOrPresent LocalDate checkIn,
        @JsonbProperty("is_foreigner") @NotNull Boolean isForeigner,
        String notes
) {}
