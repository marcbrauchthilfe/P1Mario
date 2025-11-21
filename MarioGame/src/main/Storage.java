package main;

public class Storage {
    private static final int totalNumberOfLevels = 8;
    private static int totalScore;
    private int[] levelHighscores;

    public Storage() {
        totalScore = getTotalScore();
        levelHighscores = new int[totalNumberOfLevels];
    }

    public static int getTotalScore() {
        return totalScore;
    }

    public static void setTotalScore(int totalScore) {
        Storage.totalScore = totalScore;
    }

    public int getLevelHighscores(int currentLevelIndex) {
        return levelHighscores[currentLevelIndex];
    }

    public void setLevelHighscores(int levelIndex, int levelScore) {
        this.levelHighscores[levelIndex] = levelScore;
    }

    public int getTotalNumberOfLevels() {
        return totalNumberOfLevels;
    }

    public void updateTotalScore() {
        this.totalScore = 0;
        for (int i = totalNumberOfLevels -1; i >= 0; i--) {
            this.totalScore = this.totalScore + getLevelHighscores(i);
        }
    }
}
