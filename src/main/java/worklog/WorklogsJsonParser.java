package worklog;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.WorklogJsonParserV5;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author ymolodkov on 18.07.16.
 */
public class WorklogsJsonParser implements JsonObjectParser<List<Worklog>> {
    private BasicIssue basicIssue;

    public void setIssue(BasicIssue issue) {
        this.basicIssue = issue;
    }

    @Nullable
    private <T> Collection<T> parseOptionalArray(final JSONObject json, final JsonWeakParser<T> jsonParser, final String... path)
            throws JSONException {
        JSONArray jsonArray = JsonParseUtil.getNestedOptionalArray(json, path);
        if (jsonArray == null) {
            return null;
        }
        Collection<T> res = new ArrayList<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            res.add(jsonParser.parse(jsonArray.get(i)));
        }
        return res;
    }

    public List<Worklog> parse(JSONObject s) throws JSONException {
        Collection<Worklog> worklogs;
        URI selfUri = basicIssue.getSelf();
        worklogs = parseOptionalArray(s, new JsonWeakParserForJsonObject<>(new WorklogJsonParserV5(selfUri)), IssueFieldId.WORKLOGS_FIELD.id);

        if (worklogs == null) {
            return Collections.<Worklog>emptyList();
        }
        return new ArrayList<>(worklogs);
    }

    private static class JsonWeakParserForJsonObject<T> implements JsonWeakParser<T> {
        private JsonObjectParser<T> jsonParser;

        public JsonWeakParserForJsonObject(JsonObjectParser<T> jsonParser) {
            this.jsonParser = jsonParser;
        }

        private <T> T convert(Object o, Class<T> clazz) throws JSONException {
            try {
                return clazz.cast(o);
            } catch (ClassCastException e) {
                throw new JSONException("Expected [" + clazz.getSimpleName() + "], but found [" + o.getClass().getSimpleName() + "]");
            }
        }

        public T parse(Object o) throws JSONException {
            return jsonParser.parse(convert(o, JSONObject.class));
        }
    }

    private interface JsonWeakParser<T> {
        T parse(Object o) throws JSONException;
    }
}