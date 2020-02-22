package com.sksamuel.scrimage;

import java.io.IOException;
import java.util.Collection;

public class PipelineFilter implements Filter {

    private final Collection<Filter> filters;

    public PipelineFilter(Collection<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void apply(ImmutableImage image) throws IOException {
        for (Filter filter : filters) {
            filter.apply(image);
        }
    }
}
