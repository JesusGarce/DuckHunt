package es.jesusgarce.duckhunt.models;

public class User {
    private String nick;
    private int ducks;
    private int ducksHard;
    private int ducksMedium;
    private int ducksEasy;
    private int gamesPlayed;

    public User(){

    }

    public User(String nick, int duckshard, int ducksmedium, int duckseasy, int gamesPlayed) {
        this.nick = nick;
        this.ducksHard = duckshard;
        this.ducksMedium = ducksmedium;
        this.ducksEasy = duckseasy;
        this.gamesPlayed = gamesPlayed;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getDucks() {
        return ducks;
    }

    public void setDucks(int ducks) {
        this.ducks = ducks;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int bestGame) {
        this.gamesPlayed = bestGame;
    }

    public int getDucksHard() {
        return ducksHard;
    }

    public void setDucksHard(int duckshard) {
        this.ducksHard = duckshard;
    }

    public int getDucksMedium() {
        return ducksMedium;
    }

    public void setDucksMedium(int ducksmedium) {
        this.ducksMedium = ducksmedium;
    }

    public int getDucksEasy() {
        return ducksEasy;
    }

    public void setDucksEasy(int duckseasy) {
        this.ducksEasy = duckseasy;
    }

}
