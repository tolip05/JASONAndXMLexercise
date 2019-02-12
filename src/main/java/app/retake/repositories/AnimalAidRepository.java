package app.retake.repositories;

import app.retake.domain.models.AnimalAid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalAidRepository extends JpaRepository<AnimalAid, Integer> {
    AnimalAid findByName(String name);

}
