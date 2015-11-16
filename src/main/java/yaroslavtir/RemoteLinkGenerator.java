package yaroslavtir;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RemoteLinkGenerator implements JsonGenerator<RemoteLink> {


    public JSONObject generate(RemoteLink remoteLink) throws JSONException {
        JSONObject res = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("url", remoteLink.getUrl().toString());
        object.put("title", remoteLink.getTitle());
        res.put("object", object);
        return res;
    }
}
