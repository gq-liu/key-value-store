package Server.ElectionModule;

import Server.Server;


class ElectionModule implements Runnable {


    // Server
    private Server server;
    private int voteFor = -1;
    private boolean voted = false;

    // Request Sender
    private RequestSender requestSender;
    private RequestHandler requestHandler;

    public ElectionModule(Server server) {
        this.server = server;
        this.requestSender = new RequestSender(server);
        this.requestHandler = new RequestHandler();
    }

    @Override
    public void run() {
        System.out.println("Election Module Start Working");
        this.requestSender.registerElection();

    }

    public String handleVoteReq(int candidateNodeID, int candidateTerm, int lastLogIndex, int lastLogTerm) {
        if (candidateTerm < server.getTerm()) {
            return "NO";
        }
        if (candidateTerm == server.getTerm()) {
            if (!voted || voteFor == candidateNodeID) {
                if (candidateLogUpToDate(lastLogIndex, lastLogTerm)) {
                    voted = true;
                    voteFor = candidateNodeID;
                    return "YES";
                }
                return "NO";
            }
            return "NO";
        }
        if (candidateTerm > server.getTerm()) {
            server.updateTerm(candidateTerm);
            voted = true;
            voteFor = candidateNodeID;
            return "YES";
        }
        return "NO";
    }

    // TODO: Implement
    private boolean candidateLogUpToDate(int lastLogIndex, int lastLogTerm) {
        return true;
    }

}
