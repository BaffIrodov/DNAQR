package net.dnaqr.repositories;

import net.dnaqr.entities.DefaultChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultChildRepository extends JpaRepository<DefaultChild, Long> {
}
