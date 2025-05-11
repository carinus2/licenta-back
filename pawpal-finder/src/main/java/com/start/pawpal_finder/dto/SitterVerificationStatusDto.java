package com.start.pawpal_finder.dto;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
public class SitterVerificationStatusDto {
    private final boolean idVerified;
    private final boolean callScheduled;
    private final boolean isFullyVerified;

}