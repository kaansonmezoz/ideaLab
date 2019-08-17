package idealab.api.repositories;

import idealab.api.models.PrintStatus;
import org.springframework.data.repository.CrudRepository;

public interface PrintStatusRepo extends CrudRepository<PrintStatus, Integer> {
    PrintStatus getPrintStatusById(Integer id);
}
