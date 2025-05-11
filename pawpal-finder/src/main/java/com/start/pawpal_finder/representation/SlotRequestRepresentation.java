package com.start.pawpal_finder.representation;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SlotRequestRepresentation {
    public Long slotId;
    public Integer sitterId;
}
