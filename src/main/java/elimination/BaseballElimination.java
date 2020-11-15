package elimination;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private final Map<String, TeamData> teams = new HashMap<>();

    private int[][] games;
    private List<String> teamNames;

    private static class TeamData {
        private int index;
        private int wins;
        private int losses;
        private int remaining;
        private List<String> eliminationCertificate;

        public TeamData(int index, int wins, int losses, int remaining) {
            this.index = index;
            this.wins = wins;
            this.losses = losses;
            this.remaining = remaining;
        }
    }

    public BaseballElimination(String filename) {
        initializeTeams(filename);
        calculateEliminations();
    }

    private void initializeTeams(String filename) {
        In in = new In(filename);
        try {
            int teamsAmount = in.readInt();
            this.games = new int[teamsAmount][teamsAmount];
            this.teamNames = new ArrayList<>(teamsAmount);
            for (int i = 0; i < teamsAmount; i++) {
                String teamName = in.readString();
                teamNames.add(i, teamName);
                teams.put(teamName, new TeamData(i, in.readInt(), in.readInt(), in.readInt()));
                for (int j = 0; j < teamsAmount; j++) {
                    games[i][j] = in.readInt();
                }
            }
        } finally {
            in.close();
        }
    }

    private void calculateEliminations() {
        Map.Entry<String, TeamData> maxWinsTeam = teams.entrySet().stream().max(Comparator.comparingInt(team -> team.getValue().wins)).get();
        teams.forEach((teamName, teamData) -> {
            trivialElimination(teamData, maxWinsTeam.getKey(), maxWinsTeam.getValue().wins);
            if (teamData.eliminationCertificate == null) {
                nontrivialElimination(teamData);
            }
        });
    }

    private void trivialElimination(TeamData teamData, String maxWinTeamName, int maxWins) {
        if (teamData.wins + teamData.remaining < maxWins) {
            teamData.eliminationCertificate = Collections.singletonList(maxWinTeamName);
        }
    }

    private void nontrivialElimination(TeamData teamData) {
        int teamsSize = teams.size();
        FlowNetwork flowNetwork = new FlowNetwork(teamsSize * teamsSize);
        int sourceIndex = teamsSize;
        int targetIndex = sourceIndex + 1;
        int gameIndex = targetIndex + 1;
        int maxPossibleFlow = 0;
        for (int i = 0; i < teamsSize; i++) {
            if (i == teamData.index) continue;

            TeamData otherTeam = teams.get(teamNames.get(i));
            flowNetwork.addEdge(new FlowEdge(i, targetIndex, teamData.wins + teamData.remaining - otherTeam.wins));
            for (int j = i + 1; j < teamsSize; j++) {
                if (j == teamData.index) continue;

                flowNetwork.addEdge(new FlowEdge(sourceIndex, gameIndex, games[i][j]));
                maxPossibleFlow += games[i][j];
                flowNetwork.addEdge(new FlowEdge(gameIndex, i, Double.MAX_VALUE));
                flowNetwork.addEdge(new FlowEdge(gameIndex, j, Double.MAX_VALUE));
                gameIndex++;
            }
        }
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, sourceIndex, targetIndex);
        if ((int) fordFulkerson.value() != maxPossibleFlow) {
            teamData.eliminationCertificate = new ArrayList<>();
            for (int i = 0; i < teamsSize; i++) {
                if (i == teamData.index) continue;
                if (fordFulkerson.inCut(i)) {
                    teamData.eliminationCertificate.add(teamNames.get(i));
                }
            }
        }
    }

    public int numberOfTeams() {
        return teams.size();
    }

    public Iterable<String> teams() {
        return teamNames;
    }

    public int wins(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return teams.get(team).wins;
    }

    public int losses(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return teams.get(team).losses;
    }

    public int remaining(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return teams.get(team).remaining;
    }

    public int against(String team1, String team2) {
        if (!teams.containsKey(team1) || !teams.containsKey(team2)) {
            throw new IllegalArgumentException();
        }
        return games[teams.get(team1).index][teams.get(team2).index];
    }

    public boolean isEliminated(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return teams.get(team).eliminationCertificate != null;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (!teams.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return teams.get(team).eliminationCertificate;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
