package com.ncrypted.stockutils;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class PeLevelsBuySell2 {
//    private static List<Float> levels;
//    private static HashMap<Float, Integer> levelPercentMap;
//    private static int currentShareCount = 0;
//    private static double cash = 1000000.00;
//    private static double capital = 0;
//    private static float previousLevel = 0;
//    private static int currentPercent = 0;
//    private static int futurePercent = 0;
//    private static int previousLevelPercent = 0;
//    private static int currentInvestedPercent = 0;
//
//    private static List<PeIndexMapping> indexList;
//    private static List<LevelMapping> buyLevelList;
//    private static List<LevelMapping> sellLevelList;
//
//    private static float maxBuyPe = 0;
//    private static float minSellPe = 99;
//
//    private static boolean changeFlag = true;
//
//    private static DecimalFormat df2 = new DecimalFormat("#.##");
//
//    private static Logger logger = LoggerFactory.getLogger(PeLevelsBuySell2.class);
//
//    public static void main(String[] args) {
//
//        try {
//            setIndex("src/main/resources/pe_history_big.txt");
//            setLevels("src/main/resources/new_levels.txt");
//
//            for (PeIndexMapping currentPeIndex : indexList) {
//
//                float currentPe = currentPeIndex.getPe();
//                float currentIndex = currentPeIndex.getIndex();
//
//                logger.info("--------------------------------");
//                logger.info("Current PE: {}", currentPe);
//                logger.info("Current Index: {}", currentIndex);
//
//                if (currentPe < maxBuyPe) {
//                    buyLevel(currentPe, currentIndex);
//
//                } else if (currentIndex > minSellPe) {
//                    sell(currentPe, currentIndex);
//
//                } else {
//
//                }
//
//                //findFuturePercent(currentPe);
//
//                //applyFuturePercent(currentIndex);
//
//                //logger.info("currentInvestedPercent: {}", currentInvestedPercent);
//
//                //applyFuturePercent(currentIndex);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//    }
//
//    private static void buyLevel(float currentPe, float currentIndex) {
//        for (LevelMapping levelMapping : buyLevelList) {
//            if (currentPe <= levelMapping.getPe()) {
//
//                if (levelMapping.getPercent() >= currentPercent) {
//                    buy(levelMapping.getPercent());
//                }
//            }
//
//        }
//
//        private static void findCurrentInvestedPercent ( float currentIndex){
//
//            double shareValue = currentShareCount * currentIndex;
//
//            currentInvestedPercent = (int) ((shareValue * 100) / (shareValue + cash));
//
//        }
//
//        private static void applyFuturePercent ( float currentIndex){
//            capital = cash + currentShareCount * currentIndex;
//            logger.info("Current Capital : {}", df2.format(capital));
//
//            if (changeFlag == true) {
//                int desiredShareCount = (int) ((capital * currentPercent / 100) / currentIndex);
//                logger.info("desiredShareCount : {}", desiredShareCount);
//                logger.info("Share to buy: {}", (desiredShareCount - currentShareCount));
//
//                cash = cash - (desiredShareCount - currentShareCount) * currentIndex;
//                currentShareCount = desiredShareCount;
//            }
//
//            logger.info("shareCount : {}", currentShareCount);
//
//            logger.info("cash : {}", df2.format(cash));
//
//            //cash = cash + ((cash * 5) / (100 * 365));
//
//        }
//
//        private static void findFuturePercent ( float currentPe) throws Exception {
//
//            if (currentPe <= maxBuyPe) {
//                for (LevelMapping levelMapping : buyLevelList) {
//
//                    if (currentPe == levelMapping.getPe()) {
//                        futurePercent = levelMapping.getPercent();
//                        currentPercent = futurePercent;
//                        logger.info("future percent 1: {}", futurePercent);
//                        break;
//                    }
//
//                    if (currentPe < levelMapping.getPe()) {
//
//                        if (currentPercent < levelMapping.getPercent()) {
//                            futurePercent = levelMapping.getPercent();
//                            currentPercent = futurePercent;
//                            logger.info("future percent 2: {}", futurePercent);
//                        }
//
//                        if (levelMapping.getPercent() >= currentPercent) {
//                            futurePercent = levelMapping.getPercent();
//                            currentPercent = futurePercent;
//                            logger.info("future percent 2: {}", futurePercent);
//                        } else {
//
//                            futurePercent = previousLevelPercent;
//                            currentPercent = futurePercent;
//                            logger.info("future percent 3: {}", futurePercent);
//                        }
//                        break;
//                    }
//
//                    previousLevelPercent = levelMapping.getPercent();
//                }
//                if (futurePercent < currentInvestedPercent) {
//                    changeFlag = false;
//                } else {
//                    changeFlag = true;
//                }
//
//            } else if (currentPe >= minSellPe) {
//                for (LevelMapping levelMapping : sellLevelList) {
//
//                    if (currentPe == levelMapping.getPe()) {
//                        futurePercent = levelMapping.getPercent();
//                        currentPercent = futurePercent;
//                        logger.info("future percent 4: {}", futurePercent);
//                        break;
//                    }
//
//                    if (currentPe < levelMapping.getPe()) {
//
//                        if (levelMapping.getPercent() >= currentInvestedPercent) {
//                            changeFlag = false;
//                            logger.info("in block 5");
//                        } else {
//                            futurePercent = previousLevelPercent;
//                            currentPercent = futurePercent;
//                            logger.info("future percent 6: {}", futurePercent);
//                        }
//                        break;
//                    }
//                    previousLevelPercent = levelMapping.getPercent();
//                }
//
//                if (futurePercent > currentInvestedPercent) {
//                    changeFlag = false;
//                } else {
//                    changeFlag = true;
//                }
//
//            } else {
//                changeFlag = false;
//                logger.info("No change required in investment");
//            }
//
//        }
//
//        private static void setLevels (String filePath) throws Exception {
//
//            buyLevelList = new ArrayList<>();
//            sellLevelList = new ArrayList<>();
//            //levelPercentMap = new HashMap<>();
//            //levels = new ArrayList<>();
//
//            Path path = Paths.get(filePath);
//
//            try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//                String line;
//                while ((line = br.readLine()) != null) {
//
//                    LevelMapping levelMapping = new LevelMapping();
//
//                    levelMapping.setLevelType(line.split(",")[0]);
//                    levelMapping.setPe(Float.parseFloat(line.split(",")[1]));
//                    levelMapping.setPercent(Integer.parseInt(line.split(",")[2]));
//
//                    //logger.info("Level Type: {}", levelMapping.getLevelType());
//
//                    if (levelMapping.getLevelType().equalsIgnoreCase("B")) {
//                        buyLevelList.add(levelMapping);
//                    } else if (levelMapping.getLevelType().equalsIgnoreCase("S")) {
//                        sellLevelList.add(levelMapping);
//                    } else {
//                        throw new Exception("Wrong level type defined");
//                    }
//
//                    //levelPercentMap.put(Float.parseFloat(line.split(",")[0]), Integer.parseInt(line.split(",")[1]));
//                    //levels.add(Float.parseFloat(line.split(",")[0]));
//                }
//            } catch (IOException e) {
//
//                logger.error(e.getMessage());
//            }
//
//            for (LevelMapping levelMapping : buyLevelList) {
//
//                if (levelMapping.getPe() > maxBuyPe) {
//                    maxBuyPe = levelMapping.getPe();
//                }
//            }
//
//            for (LevelMapping levelMapping : sellLevelList) {
//
//                if (levelMapping.getPe() < minSellPe) {
//                    minSellPe = levelMapping.getPe();
//                }
//            }
//
//            logger.info("Max Buy PE Level: {}", maxBuyPe);
//            logger.info("Min Sell PE Level: {}", minSellPe);
//        }
//
//        private static void setIndex (String filePath){
//
//            indexList = new ArrayList<>();
//
//            Path path = Paths.get(filePath);
//
//            try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//                String line;
//                while ((line = br.readLine()) != null) {
//
//                    PeIndexMapping peIndexMapping = new PeIndexMapping();
//
//                    peIndexMapping.setIndex(Float.parseFloat(line.split(":")[1]));
//                    peIndexMapping.setPe(Float.parseFloat(line.split(":")[0]));
//
//                    indexList.add(peIndexMapping);
//                }
//            } catch (IOException e) {
//                logger.error(e.getMessage());
//
//            }
//        }
//    }
//}