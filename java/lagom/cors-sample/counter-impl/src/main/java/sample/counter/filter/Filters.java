package sample.counter.filter;

import com.google.inject.Inject;
import play.filters.cors.CORSFilter;
import play.http.DefaultHttpFilters;

public class Filters extends DefaultHttpFilters {
    @Inject
    public Filters(CORSFilter corsFilter) {
        super(corsFilter);
    }
}
