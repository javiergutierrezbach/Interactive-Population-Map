import java.awt.*;

public class City {
    private String name;
    private double latitude;
    private double longitude;
    private String country;
    private boolean capital;
    private int population;

    private int rank;
    private double radius;
    private boolean found;

    public void setName(String name) {
        this.name = name;
    }

    public City(){
        this.setRadius(0);
        this.setPopulation("0");
        this.setCountry("-");
        this.setLongitude("0.0");
        this.setLatitude("0.0");
        this.setName("-");
        this.setRank(-1);
        this.setCapital("-");
        this.setFound(false);
    }
    public void setLatitude(String lat) {
        this.latitude = Double.parseDouble(lat);
    }

    public void setLongitude(String lng) {
        this.longitude = Double.parseDouble(lng);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCapital(String capital) {
        this.capital = capital.equals("primary");
    }

    public void setPopulation(String population) {
        //population is not really 0 but the population of some capitals of smaller territories is not
        //reported so the population for these will not show
        if (population.equals("")) this.population = 0;
        else if (population.contains(".")){
            int index = population.indexOf(".");
            this.population = Integer.parseInt(population.substring(0, index));
        }
        else this.population = Integer.parseInt(population);
    }

    public void setRank(int i) {
        this.rank = i;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    public void setFound(boolean found) {
        this.found = found;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return country;
    }

    public boolean isCapital() {
        return capital;
    }

    public int getPopulation() {
        return population;
    }

    public int compareTo(City other) {
        if (this.population < other.population) {
            return -1;
        } else if (this.population > other.population) {
            return 1;
        } else {
            return 0;
        }
    }
    public int getRank(){
        return rank;
    }

    public double getRadius() {
        return radius;
    }
    public boolean isFound() {
        return found;
    }

    //draws the informational card for the city, revealing information as the rank,
    //name, country, population and a star if it is a capital
    public void drawCard() {
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledRectangle(217, - 60, 35, 20);
        StdDraw.setPenColor(201, 162, 229);
        StdDraw.filledRectangle(216, - 60, 30, 20);
        StdDraw.setPenColor(97, 38, 140);
        StdDraw.rectangle(216, - 60, 30, 20);
        Font name = new Font("Sans Serif", Font.BOLD,22);
        StdDraw.setFont(name);
        StdDraw.textLeft(189, - 47, getName());
        Font normal = new Font("Sans Serif", Font.PLAIN,15);
        StdDraw.setFont(normal);
        StdDraw.textLeft(190, - 57, "Rank: #" + this.rank);
        StdDraw.textLeft(190, - 65, "Country: " + this.country);
        String pop = Project.populationToString(this.population);
        StdDraw.textLeft(190, - 73, "Population: " + pop);
        if (this.capital) {
            int x = 238;
            int y = -48;
            StdDraw.setPenColor(StdDraw.RED);
            double[] X = {x, x + 1, x + 5, x + 2, x + 3, x, x - 3, x - 2, x - 5, x - 1};
            double[] Y = {y + 5, y + 1, y + 1, y - 1, y - 5, y - 3, y - 5,  y - 1, y + 1, y + 1};
            StdDraw.filledPolygon(X, Y);
        }
    }
}

