package model;

import java.util.ArrayList;

public class RandomSelector<T> {

    private final ArrayList<T> objects = new ArrayList<>();
    private final ArrayList<Double> scores = new ArrayList<>();

    private double totalScore = 0;

    public void add(T element, double score){
        objects.add(element);
        scores.add(score);
        totalScore +=score;
    }

    public T random() {
        double v = Math.random() * totalScore;
        double c = 0;
        for(int i = 0; i < objects.size(); i++){
            c += scores.get(i);
            if(c >= v){
                return objects.get(i);
            }
        }
        return null;
    }

}
