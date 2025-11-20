package main;

public class Storage {
    private static int totalScore;
    private int[] levelHighscores;

    public Storage() {
        totalScore = 0;
        levelHighscores = new int[8];
    }

    public static int getTotalScore() {
        return totalScore;
    }
    public static void setTotalScore(int totalScore) {
        Storage.totalScore = totalScore;
    }

    public int[] getLevelHighscores() {
        return levelHighscores;
    }
    public void setLevelHighscores(int levelIndex, int levelScore) {
        levelHighscores[levelIndex] = levelScore;
    }
}
