package ideaLab.api.operations;

import ideaLab.api.dto.GenericResponse;
import ideaLab.api.dto.PrintJobUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class PrintJobOperations {

    public GenericResponse updatePrintJob(PrintJobUpdateRequest dto)
    {
        GenericResponse response = new GenericResponse();

        if(dto.isValidStatus())
        {
            //check if employee id is valid
            //check if print id is valid
            response.setSuccess(true);
            response.setMessage("Print Job Updated");
        }
        else
        {
            response.setSuccess(false);
            response.setMessage("Invalid Status");
        }
        return response;
    }

}
