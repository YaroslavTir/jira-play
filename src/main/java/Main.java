import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import org.joda.time.DateTime;
import worklog.AsynchronousJiraRestClientFactoryPlus;
import worklog.IssueWorklogsRestClient;
import worklog.JiraRestClientPlus;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author yaroslavTir.
 */
public class Main {

    private static final String URL = "";
    private static final String ADMIN_USERNAME = "";
    private static final String ADMIN_PASSWORD = "";
    public static final String START_DATE = "2016/07/11";
    public static final String END_DATE   = "2016/07/15";

    public static void main(String[] args) throws Exception {
        JiraRestClientPlus client = getJiraRestClient();
        client.close();
    }


    private static JiraRestClientPlus getJiraRestClient() throws URISyntaxException {
        AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
        JiraRestClientPlus client = factory.createWithBasicHttpAuthentication(new URI(URL), ADMIN_USERNAME, ADMIN_PASSWORD);
        getWorkItems(client);

        return client;
    }

    private static DateTime stringToDateTime (String timeString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date start = null;
        try {
            start = sdf.parse(timeString);
        } catch (ParseException e) {
            System.out.println("ooops");
            return null;
        }
        return new DateTime(start.getTime());
    }


    private static void getWorkItems(JiraRestClientPlus client){
        SearchRestClient searchClient = client.getSearchClient();
        SearchResult result = searchClient
                .searchJql("project = 'MYLSCR' and issueFunction in workLogged('after "+ START_DATE +" before "+ END_DATE +" by yaroslav.molodkov@firstlinesoftware.com')").claim();
        IssueWorklogsRestClient issueWorklogRestClient = client.getIssueWorklogRestClient();
        int sum = 0;
        for (Issue issue : result.getIssues()) {
            List<Worklog> worklogs = issueWorklogRestClient.getIssueWorklogs(issue).claim();
            int workLogSum = worklogs.stream()
                    .filter(worklog -> worklog.getAuthor()
                            .getName().equals(ADMIN_USERNAME))
                    .filter(worklog -> worklog.getStartDate().isAfter(stringToDateTime(START_DATE).getMillis()) &&
                                    worklog.getStartDate().isBefore(stringToDateTime(END_DATE).toInstant()))
                    .mapToInt(Worklog::getMinutesSpent)
                    .sum();
            sum += workLogSum;
        }
        System.out.println(sum);
        System.out.println(sum/60);
    }

}
