package main;

public class Storage {
    private static final int totalNumberOfLevels = 8;
    private static int totalScore;
    private final int[] levelHighscores;

    public Storage() {
        levelHighscores = new int[totalNumberOfLevels];
    }

    public static int getTotalScore() {
        return totalScore;
    }

    public static void setTotalScore(int totalScore) {
        if (totalScore < 0) {
            throw new IllegalArgumentException("totalScore darf nicht negativ sein: " + totalScore);
        }
        Storage.totalScore = totalScore;
    }

    public int getLevelHighscores(int currentLevelIndex) {
        return levelHighscores[currentLevelIndex];
    }

    public void setLevelHighscores(int levelIndex, int levelScore) {
        if (levelScore < 0) {
            throw new IllegalArgumentException("Levelscore darf nicht negativ sein: " + levelScore);
        }

        levelHighscores[levelIndex] = levelScore;
    }

    public int getTotalNumberOfLevels() {
        return totalNumberOfLevels;
    }

    public void updateTotalScore() {
        totalScore = 0;
        for (int i = totalNumberOfLevels -1; i >= 0; i--) {
            totalScore = totalScore + getLevelHighscores(i);
        }
    }
}
