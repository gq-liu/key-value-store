package Server;

public class ConsensusModule {
    private Server server;
    private int lastEntryTerm;
    private int lastEntryIndex;

    public void startLeadership() {
        System.out.printf("Node " + server.getNodeInd() + " start leadership now ...");
    }

    public int getLastEntryIndex() {
        return lastEntryIndex;
    }

    public int getLastEntryTerm() {
        return lastEntryTerm;
    }
}
