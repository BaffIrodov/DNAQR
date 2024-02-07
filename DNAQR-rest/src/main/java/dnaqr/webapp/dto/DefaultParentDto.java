package dnaqr.webapp.dto;

import dnaqr.webapp.entities.DefaultParent;
import lombok.Data;
import lombok.NoArgsConstructor;

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
