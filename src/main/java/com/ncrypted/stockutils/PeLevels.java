package com.ncrypted.stockutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class PeLevels {
    private static List<Float> levels;
    private static HashMap<Float, Integer> levelPercentMap;
    private static int shareCount = 0;
    private static double cash = 1000000.00;
    private static double capital = 0;
    private static float previousLevel = 0;
    private static int newPercent = 0;
    private static int existingPercent = 0;
    private static List<PeIndexMapping> indexList;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static Logger logger = LoggerFactory.getLogger(PeLevels.class);

    public static void main(String[] args) {

        System.out.println("Test ");

        setIndex("src/main/resources/pe_history_20");
        setLevels("src/main/resources/pe_levels.txt");

        for (PeIndexMapping currentPeIndex : indexList) {

            float currentPe = currentPeIndex.getPe();
            float currentIndex = currentPeIndex.getIndex();

            logger.info("--------------------------------");
            logger.info("Current PE: {}", currentPe);
            logger.info("Current Index: {}", currentIndex);

            findFuturePercent(currentPe);

            applyFuturePercent(currentIndex);
        }
    }

    private static void applyFuturePercent(float currentIndex) {
        capital = cash + shareCount * currentIndex;
        logger.info("Current Capital : {}", df2.format(capital));

        int desiredShareCount = (int) ((capital * newPercent / 100) / currentIndex);
        logger.info("desiredShareCount : {}", desiredShareCount);
        logger.info("Share to buy: {}", (desiredShareCount - shareCount));

        cash = cash - (desiredShareCount - shareCount) * currentIndex;
        shareCount = desiredShareCount;

        logger.info("shareCount : {}", shareCount);

        logger.info("cash : {}", df2.format(cash));

        cash = cash + ((cash * 5) / (100 * 365));
    }

    private static void findFuturePercent(float currentPe) {

        int levelPercent = 0;
        for (float level : levels) {

            levelPercent = levelPercentMap.get(level);

            if (currentPe < level) {

                if (levelPercent >= existingPercent) {
                    newPercent = levelPercentMap.get(level);
                    existingPercent = newPercent;
                    logger.info("future percent 1: {}", newPercent);
                } else {
                    newPercent = levelPercentMap.get(previousLevel);
                    existingPercent = newPercent;
                    logger.info("future percent 3: {}", newPercent);
                }
                break;
            }

            if (currentPe == level) {
                newPercent = levelPercentMap.get(level);
                logger.info("future percent 4: {}", newPercent);
                break;
            }

            previousLevel = level;
        }
    }

    private static void setLevels(String filePath) {

        levelPercentMap = new HashMap<Float, Integer>();
        levels = new ArrayList<Float>();

        Path path = Paths.get(filePath);

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {

                levelPercentMap.put(Float.parseFloat(line.split(",")[0]), Integer.parseInt(line.split(",")[1]));
                levels.add(Float.parseFloat(line.split(",")[0]));
            }
        } catch (IOException e) {

            logger.error(e.getMessage());
        }
    }

    private static void setIndex(String filePath) {

        indexList = new ArrayList<PeIndexMapping>();

        Path path = Paths.get(filePath);

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {

                PeIndexMapping peIndexMapping = new PeIndexMapping();

                peIndexMapping.setIndex(Float.parseFloat(line.split(":")[1]));
                peIndexMapping.setPe(Float.parseFloat(line.split(":")[0]));

                indexList.add(peIndexMapping);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
