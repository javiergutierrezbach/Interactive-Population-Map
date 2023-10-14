import java.awt.*;
import java.io.*;
import java.util.*;

public class Project {
    //data gotten from https://simplemaps.com/data/world-cities
    //last updated March 2022
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("The program needs a pathname given to it as one argument.");
            System.exit(1);
        }
        String pathname = args[0];
        //reads file with the data for all the cities
        Scanner fScan = null;
        City[] cities = new City[5000];
        try {
            fScan = new Scanner(new File(pathname));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        fScan.nextLine();
        //capital counter
        int capitals = 0;

        //creates an array of strings for each cell in a row of the csv file and then sets the
        //instance variables for each city from their corresponding columns
        for (int i = 1; i < 5000; i++) {
            String city = fScan.nextLine();
            String[] data = city.split(";");
            cities[i] = new City();
            cities[i].setName(data[0]);
            cities[i].setLatitude(data[2]);
            cities[i].setLongitude(data[3]);
            cities[i].setCountry(data[4]);
            cities[i].setCapital(data[8]);
            //counts the capitals
            if (cities[i].isCapital()) capitals++;
            cities[i].setPopulation(data[9]);
        }
        fScan.close();

        System.out.println("Hello! Welcome to The Cities of the World!");

        //sorts the cities in descending order
        sort(cities);
        //ranks the cities by population
        setRanks(cities);

        //sets up StdDraw, the scales and the coordinates for cities, so it all works
        //true when the user reveals all
        boolean revealed = false;
        setStdDraw(revealed);
        //the following function scales the radius of the circles from 0.4 to 4 based on their population
        setRadii(cities);

        //sets up counters for the input the user has gave.
        int cityCount = 0;
        long populationCount = 0;
        int capitalCount = 0;
        //starts the guessing name where the user can type the cities they know until they give up and type Reveal All.
        //if the city is not found, it will not be added and it will let you know.
        System.out.println("Test your knowledge! Type in the cities that you know.");
        while(true) {
            String userCity = StdIn.readLine();
            boolean found = false;
            for (int i = 1; i < 4999; i++) {
                if (cities[i].getName().equals(userCity)) {
                    found = true;

                    //prevents double counting
                    if (cities[i].isFound()) {
                        System.out.println("City was already named.");
                        continue;
                    }
                    //declares the city as found and draws it on the map
                    cities[i].setFound(true);
                    StdDraw.filledCircle(cities[i].getLongitude(), cities[i].getLatitude(), cities[i].getRadius());
                    if (cities[i].isCapital()) {
                        drawStar(cities[i].getLongitude(), cities[i].getLatitude());
                        capitalCount++;
                        //updates the counters in the drawing
                        StdDraw.setPenColor(250, 226, 160);
                        StdDraw.filledRectangle(216, -10,33, 6);
                        StdDraw.setPenColor(StdDraw.BOOK_RED);
                        StdDraw.textLeft(185, -10, capitalCount + " / " + capitals);
                    }
                    cities[i].drawCard();
                    //updates the counters
                    cityCount++;
                    StdDraw.setPenColor(250, 226, 160);
                    StdDraw.filledRectangle(216, 70,33, 6);
                    StdDraw.setPenColor(StdDraw.BOOK_RED);
                    StdDraw.textLeft(185, 70, "" + cityCount);

                    populationCount += cities[i].getPopulation();
                    StdDraw.setPenColor(250, 226, 160);
                    StdDraw.filledRectangle(216, 30,33, 6);
                    StdDraw.setPenColor(StdDraw.BOOK_RED);
                    String pop = populationToString(populationCount);
                    StdDraw.textLeft(185, 30, pop);
                    break;
                }
            }
            //breaks the infinite loop awaiting input when user types this
            if (userCity.equals("Reveal All")) {
                revealed = true;
                break;
            }
            if (!found) {
                System.out.println("City was not found");
            }
            StdDraw.setPenColor(StdDraw.ORANGE);
        }
        //starts the map again but without the instructions and shows all cities.
        StdDraw.clear();
        setStdDraw(revealed);
        revealCities(cities);

        //prints all the cities and their data in a table.
        printCities(cities);

        //makes it so once all the cities all revealed (the stars show), you can click on the cities and their
        //city card will appear with the information. It will show the smallest city that has a radius under
        //you clicked so if you want to see the largest city with smaller cities next to it (under the big radius),
        //try to click on the outside of the big circle for that city to show
        while(true) {
            if(StdDraw.isMousePressed()) {
                for (int i = 1; i < 5000; i++) {
                    //uses pythagoras to find the distance between the mouse click and the city
                    double xDistanceSQ = Math.pow(StdDraw.mouseX() - cities[i].getLongitude(), 2);
                    double yDistanceSQ = Math.pow(StdDraw.mouseY() - cities[i].getLatitude(), 2);
                    double distance = Math.sqrt(xDistanceSQ + yDistanceSQ);

                    //checks if the mouse click is within the city's circle radius
                    if (distance <= cities[i].getRadius()) {
                        cities[i].drawCard();
                    }
                }
            }
        }
    }

    //the data is divided into two groups and even though it looks sorted it has some values that are not ordered correctly
    public static void sort(City[] cities) {
        City[] aux = new City[cities.length];
        mergeSort(cities, aux, 1, cities.length);
    }

    //sorts the cities by population
    public static void mergeSort(City[] cities, City[] aux, int lo, int hi) {
        if (hi - lo <= 1) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(cities, aux, lo, mid);
        mergeSort(cities, aux, mid, hi);
        int i = lo, j = mid;
        for (int k = lo; k < hi; k++) {
            if (i == mid) aux[k] = cities[j++];
            else if (j == hi) aux[k] = cities[i++];
            else if (cities[j].compareTo(cities[i]) > 0) aux[k] = cities[j++];
            else aux[k] = cities[i++];
        }
        for (int k = lo; k < hi; k++) {
            cities[k] = aux[k];
        }
    }
    //gives the cities their numerical rank based on their population
    public static void setRanks(City[] cities) {
        for (int i = 1; i < cities.length; i++) {
            cities[i].setRank(i);
        }
    }
    //draws the background, map, instructions if it's the first time called and sets up counters.
    public static void setStdDraw(boolean revealed) {
        StdDraw.setCanvasSize(1440, 660);
        //scales place the map in the bottom left corner
        StdDraw.setXscale(-180,252);
        StdDraw.setYscale(-90, 108);
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledSquare(0, 0,1000);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        if (!revealed) {
            Font title = new Font("Sans Serif", Font.BOLD, 70);
            StdDraw.setFont(title);
            StdDraw.text(36, 39, "Cities of the World");
            Font instructions = new Font("Sans Serif", Font.PLAIN, 40);
            StdDraw.setFont(instructions);
            StdDraw.text(36, -21, "Would you like to see the instructions? Type Yes/No");
            System.out.println("Would you like to see the instructions? Type Yes/No");
            while(true) {
                String wantInstructions = StdIn.readString().toLowerCase();
                if (wantInstructions.equals("yes")) {
                    playIntro();
                } else if (!wantInstructions.equals("no")) {
                    System.out.println("Try again.");
                    continue;
                }
                StdDraw.clear();
                StdDraw.setPenColor(250, 226, 160);
                StdDraw.filledSquare(0, 0,1000);
                StdDraw.setPenColor(StdDraw.BOOK_RED);
                break;
            }
        }
        StdDraw.picture(0,0, "WorldMap.jpeg");
        StdDraw.setPenRadius(0.01);
        StdDraw.rectangle(0, 0, 180.75, 90.75);
        Font title = new Font("Sans Serif", Font.BOLD,25);
        StdDraw.setFont(title);
        StdDraw.text(36, 99, "The Cities of the World");
        Font normal = new Font("Sans Serif", Font.BOLD,18);
        StdDraw.setFont(normal);
        StdDraw.textLeft(185, 80, "Cities named:");
        StdDraw.textLeft(185, 40, "Aggregate Population:");
        StdDraw.textLeft(185, 0, "Capitals named:");
        StdDraw.setPenColor(StdDraw.ORANGE);
    }
    //sets up the radius of the circle of each city based on their population with range 0.4 to 4
    public static void setRadii(City[] cities){
        double coordinateRange = 4 - 0.4;
        double populationRange = cities[1].getPopulation() - 500000;
        //the ratio is the amount added to the radius per person
        double ratio = coordinateRange / populationRange;
        for (int i = 1; i < 5000; i++) {
            //to reduce the range, if a city has less than 500,000 people, they get set the minimum radius of 0.4
            if (cities[i].getPopulation() < 500000) {
                cities[i].setRadius(0.4);
            }
            double radius = cities[i].getPopulation() * ratio;
            cities[i].setRadius(radius + 0.4);
        }
    }
    //it first draws the circles for all the cities in their corresponding coordinates with the radius
    //proportional to its population.
    //it then draws a red star on top of the city if it happens to be the capital of a country or territory
    public static void revealCities(City[] cities) {
        int totalCities = 0;
        long totalPop = 0;
        int totalCapitals = 0;

        for (int i = 1; i < 5000; i++) {
            StdDraw.filledCircle(cities[i].getLongitude(), cities[i].getLatitude(), cities[i].getRadius());

            totalCities++;
            totalPop += cities[i].getPopulation();
            if (cities[i].isCapital()) totalCapitals++;
        }
        for (int i = 1; i < 5000; i++) {
            if (cities[i].isCapital()) drawStar(cities[i].getLongitude(), cities[i].getLatitude());
        }
        //updates the counts in the drawing
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.textLeft(185, 70, "" + totalCities);
        StdDraw.textLeft(185, 30, "" + populationToString(totalPop));
        StdDraw.textLeft(185, -10, totalCapitals + " / " + totalCapitals);
    }
    //draws a star in a 2 by 2 square centered at the city's coordinates
    public static void drawStar(double x, double y) {
        StdDraw.setPenColor(StdDraw.RED);
        double[] X = {x, x + 0.3, x + 1.5, x + 0.6, x + 0.9, x, x - 0.9, x - 0.6, x - 1.5, x - 0.3};
        double[] Y = {y + 1.5, y + 0.3, y + 0.3, y - 0.3, y - 1.5, y - 0.9, y - 1.5,  y - 0.3, y + 0.3, y + 0.3};
        StdDraw.filledPolygon(X, Y);
        StdDraw.setPenColor(StdDraw.ORANGE);
    }
    //shows theinstructions to the user
    public static void playIntro() {
        StdDraw.pause(3000);
        StdDraw.clear();
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledSquare(0, 0,1000);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(36, 90, "Instructions:");
        StdDraw.text(36, 60, "Type all the cities that you know on the command line.");
        StdDraw.text(36, 30, "The cities will show up on the map, with circles proportional");
        StdDraw.text(36, 10, "to their population.");
        StdDraw.text(36, -20, "They will have red stars on them if they are capital cities.");
        StdDraw.text(36, -50, "This includes both countries and territories.");
        StdDraw.pause(9000);
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledSquare(0, 0,1000);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(36, 90, "The game is case and special-character sensitive.");
        StdDraw.text(-126, 50, "tokyo");
        drawX(-18, 50);
        StdDraw.text(90, 50, "Tokyo");
        drawCheck(198, 50);
        StdDraw.text(-126, 10, "Sao Paulo");
        drawX(-18, 10);
        StdDraw.text(90, 10, "São Paulo");
        drawCheck(198, 10);
        StdDraw.text(36, -25, "The statistics on the right will show you your progress.");
        StdDraw.text(36, -55, "The city card on the bottom right will give you information like");
        StdDraw.text(36, -75, "the country they belong to, their population and respective rank.");
        StdDraw.pause(10000);
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledSquare(0, 0,1000);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.text(36, 80, "If you give up and want to reveal all the cities, type");
        StdDraw.text(36, 60, "'Reveal All'");
        StdDraw.text(36, 30, "The complete progress will show on the right.");
        StdDraw.text(36, 0, "After all the cities pop up, make sure to click on");
        StdDraw.text(36, -20, "the circles to see each city's information.");
        StdDraw.text(36, -60, "Have fun!");

        StdDraw.pause(8000);
        Font normal = new Font("Sans Serif", Font.PLAIN,20);
        StdDraw.setFont(normal);
        StdDraw.clear();
        StdDraw.setPenColor(250, 226, 160);
        StdDraw.filledSquare(0, 0,1000);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
    }
    //this functions makes the population numbers more readable by adding commas to it
    public static String populationToString(long population) {
        String popString = String.valueOf(population);
        int length = popString.length();
        char[] charString = new char[length + length / 3];
        for (int i = 0; i < length % 3; i++){
            charString[i] = popString.charAt(i);
        }
        for (int i = 1; i <= length / 3; i++) {
            if (length % 3 + (3 * (i - 1)) + i - 1 != 0) {
                charString[length % 3 + (3 * (i - 1)) + i - 1] = ',';
            }
            for(int j = 1; j <= 3; j++) {
                charString[length % 3 + i + (3 * (i - 1)) + j - 1] = popString.charAt((length % 3) + (j * i) - 1);
            }
        }
        return String.valueOf(charString);
    }
    public static void drawX(double x, double y) {
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(x + 10, y - 10, x - 10, y + 10);
        StdDraw.line(x - 10, y - 10, x + 10, y + 10);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
    }
    public static void drawCheck(double x, double y) {
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(x - 10, y - 2, x - 2, y - 10);
        StdDraw.line(x - 2, y - 10, x + 10, y + 10);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
    }
    //creates a table of the information of the cities with spaces and dashes to make it look organized
    public static void printCities(City[] cities) {
        System.out.println("\nRank  | Name                            |  Latitude   |  Longitude       |  Country                                         |  Capital      |  Population");
        for (int i = 0; i < 155; i++) {
            System.out.print("-");
        }
        System.out.println();
        for (int i = 1; i < cities.length; i++) {
            System.out.print(cities[i].getRank());
            for (int j = 0; j < (6 - String.valueOf(cities[i].getRank()).length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.print(cities[i].getName() + "   ");
            for (int j = 0; j < (28 - cities[i].getName().length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.print(cities[i].getLatitude() + "   ");
            for (int j = 0; j < (8 - String.valueOf(cities[i].getLatitude()).length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.print(cities[i].getLongitude() + "   ");
            for (int j = 0; j < (13 - String.valueOf(cities[i].getLongitude()).length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.print(cities[i].getCountry() + "   ");
            for (int j = 0; j < (45 - cities[i].getCountry().length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.print(cities[i].isCapital() + "   ");
            for (int j = 0; j < (10 - String.valueOf(cities[i].isCapital()).length()); j++) {
                System.out.print(" ");
            }
            System.out.print("|  ");
            System.out.println(cities[i].getPopulation() + "   ");
        }

    }
}