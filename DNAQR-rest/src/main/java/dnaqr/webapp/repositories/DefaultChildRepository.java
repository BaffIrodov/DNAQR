package dnaqr.webapp.repositories;

import dnaqr.webapp.entities.DefaultChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultChildRepository extends JpaRepository<DefaultChild, Long> {
}
