package periskal.academy.backend.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepository extends CrudRepository<Billing, Long> {

}
