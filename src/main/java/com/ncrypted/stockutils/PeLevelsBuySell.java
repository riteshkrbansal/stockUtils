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
import java.util.List;
public class PeLevelsBuySell {
    private static int currentShareCount = 0;
    private static double cash = 1000000.00;
    private static double capital = 0;
    private static int currentPercent = 0;
    private static List<PeIndexMapping> indexList;
    private static List<LevelMapping> buyLevelList;
    private static List<LevelMapping> sellLevelList;
    private static float maxBuyPe = 0;
    private static float minSellPe = 99;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static Logger logger = LoggerFactory.getLogger(PeLevelsBuySell.class);

    public static void main(String[] args) {

        try {
            setIndex("src/main/resources/pe_history_20");
            setLevels("src/main/resources/new_levels.txt");

            for (PeIndexMapping currentPeIndex : indexList) {

                float currentPe = currentPeIndex.getPe();
                float currentIndex = currentPeIndex.getIndex();

                logger.debug("--------------------------------");
                logger.debug("Current PE: {}", currentPe);
                logger.debug("Current Index: {}", currentIndex);
                logger.debug("Current Share Count: {}", currentShareCount);
                logger.debug("Current percent: {}", currentPercent);
                logger.debug("Current Cash in hand: {}", df2.format(cash));
                capital = cash + currentShareCount * currentIndex;
                logger.debug("Current Capital: {}", df2.format(capital));

                if (currentPe < maxBuyPe) {
                    buyLevel(currentPe, currentIndex);
                } else if (currentPe > minSellPe) {
                    sellLevel(currentPe, currentIndex);
                } else {
                    logger.debug("No Transaction 1");
                }
                cash = cash + (cash * 5) / (100 * 365);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void buyLevel(float currentPe, float currentIndex) {
        for (LevelMapping levelMapping : buyLevelList) {
            if (currentPe <= levelMapping.getPe()) {

                if (levelMapping.getPercent() > currentPercent) {

                    logger.debug("Buy - New percent: {}", levelMapping.getPercent());
                    buy(levelMapping.getPercent(), currentIndex);
                    currentPercent = levelMapping.getPercent();
                    //logger.info("{}:{}", currentPe, currentIndex);
                } else {
                    logger.debug("No Transaction 2");
                }
                break;
            }
        }
    }

    private static void sellLevel(float currentPe, float currentIndex) {
        for (LevelMapping levelMapping : sellLevelList) {
            if (currentPe >= levelMapping.getPe()) {

                if (levelMapping.getPercent() < currentPercent) {

                    logger.debug("Sell - New percent: {}", levelMapping.getPercent());
                    sell(levelMapping.getPercent(), currentIndex);
                    currentPercent = levelMapping.getPercent();
                    //logger.info("{}:{}", currentPe, currentIndex);
                } else {
                    logger.debug("No Transaction 3");
                }
                break;
            }
        }
    }

    private static void buy(int newPercent, float currentIndex) {

        int desiredShareCount = (int) ((capital * newPercent / 100) / currentIndex);

        if (desiredShareCount > currentShareCount) {
            logger.debug("Share Count to buy: {}", (desiredShareCount - currentShareCount));

            cash = cash - (desiredShareCount - currentShareCount) * currentIndex;
            currentShareCount = desiredShareCount;
        }
        logger.debug("New Share Count: {}", currentShareCount);
        logger.debug("Cash Available after buy: {}", df2.format(cash));
    }

    private static void sell(int newPercent, float currentIndex) {
        int desiredShareCount = (int) ((capital * newPercent / 100) / currentIndex);

        if (desiredShareCount < currentShareCount) {
            logger.debug("Share Count to sell: {}", (currentShareCount - desiredShareCount));

            cash = cash + (currentShareCount - desiredShareCount) * currentIndex;
            currentShareCount = desiredShareCount;
        }
        logger.debug("New Share Count: {}", currentShareCount);
        logger.debug("Cash Available after sell: {}", df2.format(cash));
    }

    private static void setLevels(String filePath) throws Exception {

        buyLevelList = new ArrayList<>();
        sellLevelList = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {

                LevelMapping levelMapping = new LevelMapping();

                levelMapping.setLevelType(line.split(",")[0]);
                levelMapping.setPe(Float.parseFloat(line.split(",")[1]));
                levelMapping.setPercent(Integer.parseInt(line.split(",")[2]));

                if (levelMapping.getLevelType().equalsIgnoreCase("B")) {
                    buyLevelList.add(levelMapping);
                } else if (levelMapping.getLevelType().equalsIgnoreCase("S")) {
                    sellLevelList.add(levelMapping);
                } else {
                    throw new Exception("Wrong level type defined");
                }
            }
        } catch (IOException e) {

            logger.error(e.getMessage());
        }

        for (LevelMapping levelMapping : buyLevelList) {

            if (levelMapping.getPe() > maxBuyPe) {
                maxBuyPe = levelMapping.getPe();
            }
        }

        for (LevelMapping levelMapping : sellLevelList) {

            if (levelMapping.getPe() < minSellPe) {
                minSellPe = levelMapping.getPe();
            }
        }

        logger.debug("Max Buy PE Level: {}", maxBuyPe);
        logger.debug("Min Sell PE Level: {}", minSellPe);
    }

    private static void setIndex(String filePath) {

        indexList = new ArrayList<>();
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
