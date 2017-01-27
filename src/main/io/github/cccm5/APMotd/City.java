package io.github.cccm5.APMotd;

public class City implements Comparable<City>{

    private SiegeTime time;
    private String name;

    public City(String name, SiegeTime time){
        this.name = name;
        this.time = time;
    }

    /**
     * @param o The city to compare
     * @return a negative int if the input siege time is greater than the current, 0 if equals, or a positive int if lesser
     */
    @Override
    public int compareTo(City o) {
        return time.compareTo(o.getTime());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SiegeTime getTime() {
        return time;
    }

    public void setTime(SiegeTime time) {
        this.time = time;
    }
}
