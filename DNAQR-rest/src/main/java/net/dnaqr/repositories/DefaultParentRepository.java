package net.dnaqr.repositories;

import net.dnaqr.entities.DefaultParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultParentRepository extends JpaRepository<DefaultParent, Long> {
}
