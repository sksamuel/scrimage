package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.ImmutableImage;

import java.io.IOException;
import java.util.Collection;

/**
 * A filter that just applies one or more filters in sequence.
 */
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
