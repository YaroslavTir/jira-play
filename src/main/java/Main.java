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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yaroslavTir.
 */
public class Main {

    private static final String URL = "https://jira.tfe.nl";
    private static final String ADMIN_USERNAME = "email";
    private static final String ADMIN_PASSWORD = "pass";
    public static final String START_DATE = "2016/07/25";
    public static final String END_DATE = "2016/08/31";

    public static void main(String[] args) throws Exception {
        JiraRestClientPlus client = getJiraRestClient();
        getWorkItems(client);
        client.close();
    }

    private static void getWorkItems(JiraRestClientPlus client) {
        SearchRestClient searchClient = client.getSearchClient();
        SearchResult result = searchClient
//               for jira 6
//                .searchJql("project = 'MYLSCR' and issueFunction in workLogged('after "+ START_DATE +" by "+ADMIN_USERNAME+"')").claim();
//               for jira 7
                .searchJql("project = 'MYLSCR' AND worklogAuthor='" + ADMIN_USERNAME + "' AND worklogDate > '"+ START_DATE +"'").claim();
        IssueWorklogsRestClient issueWorklogRestClient = client.getIssueWorklogRestClient();
        List<Info> allWorklogs = new ArrayList<>();
        for (Issue issue : result.getIssues()) {
            List<Worklog> worklogs = issueWorklogRestClient.getIssueWorklogs(issue).claim();
            List<Worklog> worklogsByUser = worklogs.stream()
                    .filter(worklog -> worklog.getAuthor().getName().equals(ADMIN_USERNAME))
                    .filter(worklog -> worklog.getStartDate().isAfter(stringToDateTime(START_DATE).getMillis()) &&
                            worklog.getStartDate().isBefore(stringToDateTime(END_DATE).toInstant()))
                    .collect(Collectors.toList());
            allWorklogs.addAll(worklogsByUser.stream().map(w -> new Info(w, issue)).collect(Collectors.toList()));
        }
        Map<DateTime, List<Info>> groupByDate = allWorklogs.stream()
                .collect(Collectors.groupingBy(w -> w.getWorklog().getStartDate().dayOfMonth().roundFloorCopy()));
        Map<DateTime, List<Info>> groupByDateAndSorted = new TreeMap<>(groupByDate);
        groupByDateAndSorted.entrySet().forEach(e -> {
            System.out.println(e.getKey());
            e.getValue()
                    .forEach(w -> System.out.println(String.format("%s: %s h", w.getIssue().getKey(), w.getWorklog().getMinutesSpent() / 60)));
        });

    }

    private static JiraRestClientPlus getJiraRestClient() throws URISyntaxException {
        AsynchronousJiraRestClientFactoryPlus factory = new AsynchronousJiraRestClientFactoryPlus();
        JiraRestClientPlus client = factory.createWithBasicHttpAuthentication(new URI(URL), ADMIN_USERNAME, ADMIN_PASSWORD);
        return client;
    }

    private static DateTime stringToDateTime(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date start;
        try {
            start = sdf.parse(timeString);
        } catch (ParseException e) {
            System.out.println("ooops");
            return null;
        }
        return new DateTime(start.getTime());
    }

    static class Info {
        final Worklog worklog;
        final Issue issue;

        public Info(Worklog worklog, Issue issue) {
            this.worklog = worklog;
            this.issue = issue;
        }

        public Worklog getWorklog() {
            return worklog;
        }

        public Issue getIssue() {
            return issue;
        }
    }

}
