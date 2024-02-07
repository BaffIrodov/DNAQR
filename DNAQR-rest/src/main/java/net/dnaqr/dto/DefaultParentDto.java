package net.dnaqr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dnaqr.entities.DefaultParent;

@Data
@NoArgsConstructor
public class DefaultParentDto {
    private Long id;
    private String name;
    private boolean archive;

    public DefaultParentDto(DefaultParent defaultParent) {
        this.id = defaultParent.getId();
        this.name = defaultParent.getName();
        this.archive = defaultParent.isArchive();
    }
}
