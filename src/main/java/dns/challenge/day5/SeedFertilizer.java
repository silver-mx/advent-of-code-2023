package dns.challenge.day5;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
        Map.Entry<List<SeedRange>, Map<DataMap, List<SourceDestRange>>> seedsToDataMaps2 = parseFilePart2(allLines);
        long minLocation2 = seedsToDataMaps2.getKey().stream()
                .peek(seedRange -> System.out.println("=========> SeedRange=" + seedRange))
                .map(seedRange -> LongStream.range(0, seedRange.rangeLength())
                        .map(i -> seedRange.start() + i).boxed())
                .map(longStream ->
                        longStream
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SEED_TO_SOIL)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.SOIL_TO_FERTILIZER)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.FERTILIZER_TO_WATER)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.WATER_TO_LIGHT)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.LIGHT_TO_TEMP)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.TEMP_TO_HUMIDITY)))
                                .map(value -> mapSourceToDest(value, dataMaps.get(DataMap.HUMIDITY_TO_LOCATION)))
                                .min(Long::compareTo)
                                .orElseThrow()
                )
                .peek(min -> System.out.println("     calculated min = " + min))
                .min(Long::compareTo).orElseThrow();

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
                    long fromInclusive = data.sourceStart();
                    long toExclusive = data.sourceStart() + data.rangeLength();
                    return value >= fromInclusive && value < toExclusive;
                })
                .map(data -> {
                    long increment = value - data.sourceStart();
                    return data.destStart() + increment;
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

    private static Map.Entry<List<SeedRange>, Map<DataMap, List<SourceDestRange>>> parseFilePart2(List<String> allLines) {
        Pattern patternMapDataLine = Pattern.compile("(.*)\\s+map:");
        Pattern patternSeeds = Pattern.compile("(\\d+\\s+\\d+)+");
        List<SeedRange> seeds = new ArrayList<>();
        Map<DataMap, List<SourceDestRange>> allMaps = new HashMap<>();
        List<SourceDestRange> currentDataGroup = null;

        for (String line : allLines) {

            if (line.startsWith("seeds")) {
                Matcher matcherSeeds = patternSeeds.matcher(line);
                while (matcherSeeds.find()) {
                    String[] seedRangeData = matcherSeeds.group(1).trim().split("\\s");
                    seeds.add(new SeedRange(Long.parseLong(seedRangeData[0]), Long.parseLong(seedRangeData[1])));
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
}
