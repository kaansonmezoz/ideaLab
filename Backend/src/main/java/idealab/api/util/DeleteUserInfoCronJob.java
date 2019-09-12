package idealab.api.util;

import idealab.api.model.CustomerInfo;
import idealab.api.repositories.CustomerInfoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DeleteUserInfoCronJob {

    private CustomerInfoRepo customerInfoRepo;

    private static final Integer numDaysRetention = 30;

    public DeleteUserInfoCronJob(CustomerInfoRepo customerInfoRepo) {
        this.customerInfoRepo = customerInfoRepo;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUserInfoCronJob.class);

    //Run everyday at 6am - Delete users older than 30 days
    @Scheduled(cron = "0 0 6 * * *")
    public void reportCurrentTime() {
        LOGGER.info("Running cron job to delete users");
        Iterable<CustomerInfo> customerInfoList = customerInfoRepo.findAll();
        for(CustomerInfo c : customerInfoList) {
            LocalDate d = c.getCreatedDate().toLocalDate();
            if(d.isBefore(LocalDate.now().minusDays(30))) {
                customerInfoRepo.delete(c);
            }
        }

    }

}
