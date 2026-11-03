package funn.j2k.web.student.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;

import java.util.Date;

public record CreateStudentRequest(
        @Min(100000) @Max(999999) Integer isu,
        @Size(min=2) String name,
        @Size(min=5) @Pattern(regexp="[A-Z][1-9][1-4]\\d{2}") String group,
        @Min(1) @Max(99) Integer dormitory,
        @Min(1) @Max(9999) Integer room,
        @JsonbDateFormat("dd-MM-yyyy") @Past Date checkIn,
        @NotNull Boolean isForeigner,
        String notes
) {}
