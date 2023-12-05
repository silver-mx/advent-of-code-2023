package dns.challenge.day5;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class SeedFertilizer {

    private enum DataMap {
        SEED_TO_SOIL("seed-to-soil"),
        SOIL_TO_FERTILIZER("soil-to-fertilizer"),
        FERTILIZER_TO_WATER("fertilizer-to-water"),
        WATER_TO_LIGHT("water-to-light"),
        LIGHT_TO_TEMP("light-to-temperature"),
        TEMP_TO_HUMIDITY("temperature-to-humidity"),
        HUMIDITY_TO_LOCATION("humidity-to-location");

        final String name;

        DataMap(String name) {
            this.name = name;
        }

        static DataMap from(String name) {
            return Arrays.stream(DataMap.values())
                    .filter(value -> value.name.equals(name)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal value[" + name + "]"));
        }
    }

    public static SeedFertilizerResult execute(String inputPath) throws IOException {
        SeedFertilizerResult result = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + result);

        return result;
    }

    private static SeedFertilizerResult processInput(List<String> allLines) {

        // Part 1
        Map.Entry<List<Long>, Map<DataMap, List<SourceDestRange>>> seedsToDataMaps = parseFilePart1(allLines);
        List<Long> seeds = seedsToDataMaps.getKey();
        Map<DataMap, List<SourceDestRange>> dataMaps = seedsToDataMaps.getValue();

        long minLocation = getMinLocation(seeds, dataMaps);

        // Part 2

        Map.Entry<List<SourceDestRange>, Map<DataMap, List<SourceDestRange>>> seedsToDataMaps2 = parseFilePart2(allLines);
        List<SourceDestRange> optimizedSeeds = optimizeSeeds(seedsToDataMaps2.getKey(), seedsToDataMaps2.getValue());
        long minLocation2 = optimizedSeeds.stream()
                .peek(seedRange -> System.out.println("=========> SeedRange=" + seedRange))
                .map(seedRange -> seedRange.destStart)
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SEED_TO_SOIL)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SOIL_TO_FERTILIZER)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.FERTILIZER_TO_WATER)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.WATER_TO_LIGHT)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.LIGHT_TO_TEMP)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.TEMP_TO_HUMIDITY)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.HUMIDITY_TO_LOCATION)))
                .min(Long::compareTo)
                .orElseThrow();


        return new SeedFertilizerResult(minLocation, minLocation2);
    }

    private static Long getMinLocation(List<Long> seeds, Map<DataMap, List<SourceDestRange>> dataMaps) {
        return seeds.stream()
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SEED_TO_SOIL)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SOIL_TO_FERTILIZER)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.FERTILIZER_TO_WATER)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.WATER_TO_LIGHT)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.LIGHT_TO_TEMP)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.TEMP_TO_HUMIDITY)))
                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.HUMIDITY_TO_LOCATION)))
                .min(Long::compareTo).orElseThrow();
    }

    private static long mapSourceToDest(long value, List<SourceDestRange> dataLst) {
        return dataLst.stream().filter(data -> {
                    long fromInclusive = data.sourceStart;
                    long toExclusive = data.sourceStart + data.rangeLength;
                    return value >= fromInclusive && value < toExclusive;
                })
                .map(data -> {
                    long increment = value - data.sourceStart;
                    return data.destStart + increment;
                })
                .findFirst().orElse(value);
    }

    private static Map.Entry<List<Long>, Map<DataMap, List<SourceDestRange>>> parseFilePart1(List<String> allLines) {
        Pattern patternMapDataLine = Pattern.compile("(.*)\\s+map:");
        Pattern patternSeeds = Pattern.compile("seeds:\\s+(.*)");
        List<Long> seeds = new ArrayList<>();
        Map<DataMap, List<SourceDestRange>> allMaps = new HashMap<>();
        List<SourceDestRange> currentDataGroup = null;

        for (String line : allLines) {

            if (line.startsWith("seeds")) {
                Matcher matcherSeeds = patternSeeds.matcher(line);
                if (matcherSeeds.matches()) {
                    seeds.addAll(Arrays.stream(matcherSeeds.group(1).trim().split("\\s")).map(Long::parseLong).toList());
                } else {
                    throw new IllegalStateException("There was an error parsing seeds in line[" + line + "]");
                }
            } else if (line.contains("map:")) {
                Matcher matcher = patternMapDataLine.matcher(line);
                if (matcher.matches()) {
                    DataMap dataMap = DataMap.from(matcher.group(1));
                    currentDataGroup = new ArrayList<>();
                    allMaps.put(dataMap, currentDataGroup);
                } else {
                    throw new IllegalStateException("There was an error parsing a map in line[" + line + "]");
                }
            } else if (!line.isBlank()) {
                String[] elements = line.trim().split("\\s");
                long destStart = Long.parseLong(elements[0]);
                long sourceStart = Long.parseLong(elements[1]);
                int rangeLength = Integer.parseInt(elements[2]);
                requireNonNull(currentDataGroup).add(new SourceDestRange(sourceStart, destStart, rangeLength));
            }
        }

        return Map.entry(seeds, allMaps);
    }

    private static Map.Entry<List<SourceDestRange>, Map<DataMap, List<SourceDestRange>>> parseFilePart2(List<String> allLines) {
        Pattern patternMapDataLine = Pattern.compile("(.*)\\s+map:");
        Pattern patternSeeds = Pattern.compile("(\\d+\\s+\\d+)+");
        List<SourceDestRange> seeds = new ArrayList<>();
        Map<DataMap, List<SourceDestRange>> allMaps = new HashMap<>();
        List<SourceDestRange> currentDataGroup = null;

        for (String line : allLines) {

            if (line.startsWith("seeds")) {
                Matcher matcherSeeds = patternSeeds.matcher(line);
                while (matcherSeeds.find()) {
                    String[] seedRangeData = matcherSeeds.group(1).trim().split("\\s");
                    seeds.add(new SourceDestRange(0L, Long.parseLong(seedRangeData[0]), Long.parseLong(seedRangeData[1])));
                }

                if (seeds.isEmpty()) {
                    throw new IllegalStateException("There was an error parsing seeds in line[" + line + "]");
                }
            } else if (line.contains("map:")) {
                Matcher matcher = patternMapDataLine.matcher(line);
                if (matcher.matches()) {
                    DataMap dataMap = DataMap.from(matcher.group(1));
                    currentDataGroup = new ArrayList<>();
                    allMaps.put(dataMap, currentDataGroup);
                } else {
                    throw new IllegalStateException("There was an error parsing a map in line[" + line + "]");
                }
            } else if (!line.isBlank()) {
                String[] elements = line.trim().split("\\s");
                long destStart = Long.parseLong(elements[0]);
                long sourceStart = Long.parseLong(elements[1]);
                int rangeLength = Integer.parseInt(elements[2]);
                requireNonNull(currentDataGroup).add(new SourceDestRange(sourceStart, destStart, rangeLength));
            }
        }

        return Map.entry(seeds, allMaps);
    }

    private static List<SourceDestRange> optimizeSeeds(List<SourceDestRange> seedsRange, Map<DataMap, List<SourceDestRange>> dataMaps) {
        List<SourceDestRange> humidityToLocationRanges = dataMaps.get(DataMap.HUMIDITY_TO_LOCATION);
        List<SourceDestRange> tempToHumidity = generateSmartDownstreamRanges(humidityToLocationRanges, dataMaps.get(DataMap.TEMP_TO_HUMIDITY));
        List<SourceDestRange> lightToTemp = generateSmartDownstreamRanges(tempToHumidity, dataMaps.get(DataMap.LIGHT_TO_TEMP));
        List<SourceDestRange> waterToLight = generateSmartDownstreamRanges(lightToTemp, dataMaps.get(DataMap.WATER_TO_LIGHT));
        List<SourceDestRange> fertilizerToWater = generateSmartDownstreamRanges(waterToLight, dataMaps.get(DataMap.FERTILIZER_TO_WATER));
        List<SourceDestRange> soilToFertilizer = generateSmartDownstreamRanges(fertilizerToWater, dataMaps.get(DataMap.SOIL_TO_FERTILIZER));
        List<SourceDestRange> seedToSoil = generateSmartDownstreamRanges(soilToFertilizer, dataMaps.get(DataMap.SEED_TO_SOIL));
        List<SourceDestRange> seeds = generateSmartDownstreamRanges(seedToSoil, seedsRange);

        System.out.println("seedToSoil = " + seeds);

        return seeds;
    }

    private static List<SourceDestRange> generateSmartDownstreamRanges(List<SourceDestRange> downstreamRanges, List<SourceDestRange> upstreamRanges) {
        return downstreamRanges.stream().map(downstreamRange -> {
                    long downSourceStart = downstreamRange.sourceStart;
                    long downSourceToInclusive = downstreamRange.sourceStart + downstreamRange.rangeLength - 1;

                    return upstreamRanges.stream().map(upstreamRange -> {
                                long upstreamDestStart = upstreamRange.destStart;
                                long upstreamDestToInclusive = upstreamDestStart + upstreamRange.rangeLength - 1;
                                SourceDestRange subRange = null;

                                if (downstreamRange.hasInSourceRange(upstreamDestStart) && downstreamRange.hasInSourceRange(upstreamDestToInclusive)) {
                                    // 1. The upstream range is in parent range
                                    subRange = new SourceDestRange(upstreamRange.getSrcValueByDestValue(upstreamDestStart),
                                            upstreamDestStart, upstreamRange.rangeLength);
                                } else if (downstreamRange.hasInSourceRange(upstreamDestStart) && !downstreamRange.hasInSourceRange(upstreamDestToInclusive)) {
                                    // 2. The upstream's start range is in the downstream's range but not the upstream's end range
                                    long rangeLength = upstreamDestToInclusive - downSourceToInclusive;
                                    subRange = new SourceDestRange(upstreamRange.getSrcValueByDestValue(upstreamDestStart),
                                            upstreamDestStart, rangeLength);
                                    return subRange;
                                } else if (!downstreamRange.hasInSourceRange(upstreamDestStart) && downstreamRange.hasInSourceRange(upstreamDestToInclusive)) {
                                    // 3. The upstream's start range is not in the downstream's range but the upstream's end range is
                                    long rangeLength = upstreamDestToInclusive - downSourceStart;
                                    subRange = new SourceDestRange(upstreamRange.getSrcValueByDestValue(downSourceStart),
                                            downSourceStart, rangeLength);
                                    return subRange;
                                } else if (!downstreamRange.hasInSourceRange(upstreamDestStart) && downstreamRange.hasInSourceRange(upstreamDestToInclusive)) {
                                    // 4. The upstream's range wraps the downstream range
                                    subRange = new SourceDestRange(upstreamRange.getSrcValueByDestValue(downSourceStart),
                                            downSourceStart, downstreamRange.rangeLength);
                                    return subRange;
                                }
                                // 5. No matching between the child's range and the parent's
                                if (nonNull(subRange)) {
                                    upstreamRange.addDownstreamRange(downstreamRange);
                                }

                                return subRange;
                            })
                            .filter(Objects::nonNull)
                            .toList();
                })
                .flatMap(List::stream)
                .toList();
    }
}
