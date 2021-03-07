package Server;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class ElectionModule implements Runnable {

    // Server
    private Server server;

    // Timeout to trigger a new leader election
    private Timer electionTimer;

    // Timeout to resend RequestVoteRPC
    private Timer requestVoteTimer;

    // VoteInfo
    private enum VoteInfo {VOTE_ME, VOTE_OTHERS}
    private VoteInfo[] voteInfos;


    // Timeout upper/lower bound
    private int timeoutMIN = 200;
    private int timeoutMAX = 300;

    public ElectionModule(Server server) {
        this.server = server;
    }

    // Election Init Task
    private class ElectionTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Starting a new round of election at term" + server.getTerm() + 1);
            // register next round timer task
            electionTimer = new Timer();
            electionTimer.schedule(new ElectionTask(), getRandomTimeoutVal());

            // start this round of election
            // 1. reset VoteInfo
            voteInfos = new VoteInfo[server.getTotalNodes()];
            // 2. UpdateTerm
            server.increaseTerm();
            // 3. Become Candidate
            server.setStatus(ServerStatus.CANDIDATE);
            // 4. Vote itself
            voteInfos[server.getNodeInd()] = VoteInfo.VOTE_ME;
            // 5. Execute RequestVoteRPC task
            new Thread(new SendRequestVoteTask()).start();
        }
    }

    // Send RequestVote Task
    private class SendRequestVoteTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Starting sending RequestVoteRPC...");
            // setup timer
            requestVoteTimer = new Timer();
            requestVoteTimer.schedule(new SendRequestVoteTask(), (getRandomTimeoutVal() / 3));
            // send RequestVoteRPC in parallel
            for (int i = 0; i < server.getTotalNodes(); i++) {
                if (voteInfos[i] == null) {
                    new Thread(new SendVoteReq(i, server.getServerList()[i])).start();
                }
            }
        }
    }

    private class SendVoteReq implements Runnable {
        private int targetNodeID;
        private String url;
        public SendVoteReq(int targetNodeID, String url) {
            this.targetNodeID = targetNodeID;
            this.url = url;
        }

        @Override
        public void run() {
            System.out.println("Sending Vote Request to Node " + targetNodeID);
            XmlRpcClient client = null;
            try {
                client = new XmlRpcClient(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // Build Req parameters
            Vector params = new Vector<>();
            params.add(server.getNodeInd());
            params.add(server.getTerm());
            params.add(server.getConsensusModule().getLastEntryTerm());
            params.add(server.getConsensusModule().getLastEntryIndex());

            String response = "";
            try {
                response = (String) client.execute("surfstore.requestVote", params);
            } catch (XmlRpcException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response.equals("YES")) {
                System.out.println("Node " + targetNodeID + "Vote Me!");
                voteInfos[targetNodeID] = VoteInfo.VOTE_ME;
            } else if (response.equals("NO")) {
                System.out.println("Node " + targetNodeID + "Vote Others.");
                voteInfos[targetNodeID] = VoteInfo.VOTE_OTHERS;
            }

            // if got majority votes
            if (countVotes(voteInfos) > server.getTotalNodes() / 2) {
                System.out.println("Node " + server.getNodeInd() + " become a leader of term " + server.getTerm());
                // cancel requestVoteTimer Task
                requestVoteTimer.cancel();
                // cancel electionTimer Task
                electionTimer.cancel();
                server.setStatus(ServerStatus.LEADER);
                // Began Leadership
                server.getConsensusModule().startLeadership();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Election Module Start Working");
        startElectionModule();
    }

    private void startElectionModule() {
        // schedule a timer task
        // execute election process when timeout
        electionTimer = new Timer();
        electionTimer.schedule(new ElectionTask(), getRandomTimeoutVal());
    }

    private int getRandomTimeoutVal() {
        Random timeoutVal = new Random();
        int timeout = timeoutVal.nextInt(timeoutMAX) % (timeoutMAX - timeoutMIN + 1) + timeoutMIN;
        return timeout;
    }

    private int countVotes(VoteInfo[] voteInfos) {
        int counts = 0;
        for (VoteInfo voteInfo : voteInfos) {
            if (voteInfo == VoteInfo.VOTE_ME) {
                counts++;
            }
        }
        return counts;
    }
}
