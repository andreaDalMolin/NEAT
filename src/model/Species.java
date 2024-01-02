package model;

import model.genes.Genome;
import model.genes.NodeGene;

import java.util.*;

public class Species {

//    private Map<Double, Client> clients = new TreeMap<>();
    private HashSet<Client> clients = new HashSet<>();

    private Client representative;
    private double score;

    public Species(Client representative) {
        this.representative = representative;
        this.representative.setSpecies(this);
        clients.add(representative);
        score = 0;
    }

    public boolean putClient(Client client) {
        if (client.distance(representative) < Neat.CP) {
            client.setSpecies(this);
            clients.add(client);
            return true;
        }
        return false;
    }

    public void forcePut (Client client) {
        client.setSpecies(this);
        clients.add(client);
    }

    public void goExtinct() {
        for (Client client : clients) {
            client.setSpecies(null);
        }
    }

    public void evaluateScore() {
        score = 0; // Reset score to 0 before evaluation
        for (Client client : clients) {
            score += client.getScore();
        }
        this.score = score / clients.size();
    }

    public void reset() {
        representative = getRandomClient();
        for (Client client : clients) {
            client.setSpecies(null);
        }
        clients.clear();
        clients.add(representative);
        representative.setSpecies(this);
        score = 0;
    }

    public void kill(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        int clientsToRemove = (int) Math.ceil(clients.size() * (percentage / 100.0));
        List<Client> sortedClients = new ArrayList<>(clients);
        sortedClients.sort(Comparator.comparingDouble(Client::getScore)); // Assuming Client has a getScore method

        for (int i = 0; i < clientsToRemove; i++) {
            clients.remove(sortedClients.get(i));
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
        Random random = new Random();
        int randomIndex = random.nextInt(clients.size());
        return new ArrayList<>(clients).get(randomIndex);
    }

    public double getScore() {
        return score;
    }
}
