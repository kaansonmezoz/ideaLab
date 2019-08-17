package idealab.api.repositories;

import idealab.api.models.EmployeeList;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeListRepo extends CrudRepository <EmployeeList, Integer> {
    EmployeeList getEmployeeListById(Integer id);
}
