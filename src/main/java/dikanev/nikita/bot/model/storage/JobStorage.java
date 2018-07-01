package dikanev.nikita.bot.model.storage;

import dikanev.nikita.bot.model.jobs.Job;
import dikanev.nikita.bot.model.jobs.SendNotificationJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobStorage {

    private static final Logger LOG = LoggerFactory.getLogger(JobStorage.class);

    private static JobStorage ourInstance = new JobStorage();

    public static JobStorage getInstance() {
        return ourInstance;
    }

    private List<Job> jobs = new ArrayList<>();

    private volatile boolean hasWork = false;

    private JobStorage(){
        initJob();
    }

    private void initJob() {
//        jobs.add(new SendNotificationJob());
    }

    public void start() throws Exception{
        if (jobs.isEmpty()) {
            LOG.warn("No jobs configured. Exist");
            return;
        } else if (hasWork) {
            LOG.warn("Multiple start job");
        }

        hasWork = true;

        while (hasWork) {
            for (Job job : jobs) {
                try {
                    job.doJob();
                } catch (Exception e) {
                    LOG.error("Something wrong", e);
                }
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void stop(boolean hasWork) {
        this.hasWork = hasWork;
    }

    private List<Job> getJobs(){
        return jobs;
    }
}
