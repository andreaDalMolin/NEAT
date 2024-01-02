package model;

import model.genes.Genome;
import model.genes.NodeGene;

import java.util.*;

public class Species {

    private Map<Double, Client> clients = new TreeMap<>();

    private Client representative;
    private double score;

    public Species(Client representative) {
        this.representative = representative;
        this.representative.setSpecies(this);
        clients.put(representative.getScore(), representative);
        score = 0;
    }

    public boolean putClient(Client client) {
        if (client.distance(representative) < Neat.CP) {
            this.representative.setSpecies(this);
            clients.put(representative.getScore(), representative);
            return true;
        }
        return false;
    }

    public void forcePut () {
        this.representative.setSpecies(this);
        clients.put(representative.getScore(), representative);
    }

    public void goExtinct () {
        for(Client client : clients.values()) {
            client.setSpecies(null);
        }
    }

    public void evaluateScore () {
        for (Client client : clients.values()) {
            score += client.getScore();
        }
        this.score = score / clients.size();
    }

    public void reset () {
        representative = getRandomClient();

        for (Client client : clients.values()) {
            client.setSpecies(null);
        }

        clients.clear();

        clients.put(representative.getScore(), representative);
        representative.setSpecies(this);
        score = 0;
    }

    public void kill(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        int totalClients = clients.size();
        int clientsToRemove = (int) Math.ceil(totalClients * (percentage / 100.0));

        Iterator<Double> iterator = clients.keySet().iterator();
        while (iterator.hasNext() && clientsToRemove > 0) {
            iterator.next();
            iterator.remove();
            clientsToRemove--;
        }
    }

    public Genome breed () {

        Client client1 = getRandomClient();
        Client client2 = getRandomClient();

        if (client1.getScore() > client2.getScore()) return Genome.crossover(client1.getGenome(), client2.getGenome());
        return Genome.crossover(client2.getGenome(), client1.getGenome());
    }

    public int size () {
        return clients.size();
    }

    private Client getRandomClient() {
        // Convert the TreeMap entries to a List
        List<Map.Entry<Double, Client>> entryList = new ArrayList<>(clients.entrySet());

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(entryList.size());

        // Retrieve the random entry and extract the model.genes.NodeGene object
        Map.Entry<Double, Client> randomEntry = entryList.get(randomIndex);
        return randomEntry.getValue();
    }

    public Map<Double, Client> getClients() {
        return clients;
    }

    public Client getRepresentative() {
        return representative;
    }

    public double getScore() {
        return score;
    }
}
