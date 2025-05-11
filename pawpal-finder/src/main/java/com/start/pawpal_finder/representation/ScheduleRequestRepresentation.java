package com.start.pawpal_finder.representation;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleRequestRepresentation {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public String dateTime;

    public Integer sitterId;
}
