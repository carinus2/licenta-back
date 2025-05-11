package com.start.pawpal_finder.representation;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InterestReservationFilterRepresentation {

    private String status;
    private String dateOrder = "desc";
    private String sitterName;
    private int page = 0;
    private int size = 10;
}