package eclipselink.example.moxy.twitter.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class SearchResults {

    private String query;
    private float completedIn;
    List<Result> results;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @XmlElement(name = "completed_in")
    public float getCompletedIn() {
        return completedIn;
    }

    public void setCompletedIn(float completedIn) {
        this.completedIn = completedIn;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}