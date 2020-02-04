package es.jesusgarce.duckhunt.models;

public class User {
    private String nick;
    private int ducks;
    private int gamesPlayed;

    public User(){

    }

    public User(String nick, int ducks, int gamesPlayed) {
        this.nick = nick;
        this.ducks = ducks;
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
}
