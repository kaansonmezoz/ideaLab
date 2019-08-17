package idealab.api.operations;

import idealab.api.dto.GenericResponse;
import idealab.api.dto.PrintJobUpdateRequest;
import idealab.api.models.EmployeeList;
import idealab.api.models.PrintStatus;
import idealab.api.repositories.EmployeeListRepo;
import idealab.api.repositories.PrintStatusRepo;
import org.springframework.stereotype.Component;

@Component
public class PrintJobOperations {

    EmployeeListRepo employeeListRepo;
    PrintStatusRepo printStatusRepo;

    public PrintJobOperations(EmployeeListRepo employeeListRepo, PrintStatusRepo printStatusRepo) {
        this.employeeListRepo = employeeListRepo;
        this.printStatusRepo = printStatusRepo;
    }

    public GenericResponse updatePrintJob(PrintJobUpdateRequest dto)
    {
        GenericResponse response = new GenericResponse();
        response.setSuccess(false);
        response.setMessage("Invalid Status");

        if(dto.isValidStatus())
        {
            //check if employee id is valid
            EmployeeList employeeList = employeeListRepo.getEmployeeListById(dto.getEmployeeId());

            //check if print id is valid
            PrintStatus printStatus = printStatusRepo.getPrintStatusById(dto.getPrintStatusId());

            if(employeeList != null && printStatus != null) {
                //Update print status


                //return success message
                response.setSuccess(true);
                response.setMessage("Print Job Updated");
            }

        }

        return response;
    }

}
