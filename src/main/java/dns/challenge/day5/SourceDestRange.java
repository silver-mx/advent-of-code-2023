package dns.challenge.day5;

import java.util.ArrayList;
import java.util.List;

public class SourceDestRange {

    final long sourceStart;
    final long destStart;
    final long rangeLength;
    final List<SourceDestRange> downstreamRanges = new ArrayList<>();

    SourceDestRange(long sourceStart, long destStart, long rangeLength) {
        this.sourceStart = sourceStart;
        this.destStart = destStart;
        this.rangeLength = rangeLength;
    }

    public boolean hasInSourceRange(long value) {
        long toExclusiveInclusive = sourceStart + rangeLength;
        return sourceStart <= value && value < toExclusiveInclusive;
    }

    public boolean hasInDestRange(long value) {
        long toExclusiveInclusive = destStart + rangeLength;
        return destStart <= value && value < toExclusiveInclusive;
    }

    public long getDestValueBySrcValue(long srcValue) {
        long delta = srcValue - sourceStart;
        return destStart + delta;
    }

    public long getSrcValueByDestValue(long destValue) {
        long delta = destValue - destStart;
        return sourceStart + delta;
    }

    public void addDownstreamRange(SourceDestRange range) {
        downstreamRanges.add(range);
    }

    public long getMinLocation() {
        //return downstreamRanges.stream().filter()
        return 0;
    }
}
