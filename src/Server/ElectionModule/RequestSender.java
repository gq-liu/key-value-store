package Server.ElectionModule;

import Server.Server;
import Server.ServerStatus;
import Server.utils.Utils;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

class RequestSender {
    private Server server;

    // Timeout to trigger a new leader election
    private Timer electionTimer = null;
    // Timeout to trigger a new RequestVoteRPC
    private Timer[] requestVoteTimers = new Timer[server.getTotalNodes()];

    // VoteInfo
    private enum VoteInfo {VOTE_ME, VOTE_OTHERS}
    private VoteInfo[] voteInfos;

    public RequestSender(Server server) {
        this.server = server;
    }

    public void registerElection() {
        // schedule a timer task
        // execute election process when timeout
        electionTimer = new Timer();
        electionTimer.schedule(new StartElectionTask(), Utils.getRandomTimeoutVal());
    }

    // Election Init Task
    class StartElectionTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Starting a new round of election at term" + server.getTerm() + 1);
            // register next round timer task
            electionTimer= new Timer();
            long timeout = Utils.getRandomTimeoutVal();
            electionTimer.schedule(new StartElectionTask(), Utils.getRandomTimeoutVal());

            // start this round of election
            // 1. reset VoteInfo
            voteInfos = new VoteInfo[server.getTotalNodes()];
            // 2. UpdateTerm
            server.increaseTerm();
            // 3. Become Candidate
            server.setStatus(ServerStatus.CANDIDATE);
            // 4. Vote itself
            voteInfos[server.getNodeInd()] = VoteInfo.VOTE_ME;
            // 5. Send RequestVoteRPC task
            System.out.println("Starting sending RequestVoteRPC...");
            // send RequestVoteRPC in parallel
            for (int i = 0; i < server.getTotalNodes(); i++) {
                if (voteInfos[i] == null) {
                    // periodically send request util receive response
                    requestVoteTimers[i] = new Timer();
                    requestVoteTimers[i].schedule(new SendRequestVoteTask(i, server.getServerList()[i]), 0, timeout / 3);
                }
            }
        }
    }

    class SendRequestVoteTask extends TimerTask {
        private int targetNodeID;
        private String url;
        public SendRequestVoteTask(int targetNodeID, String url) {
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
                System.out.println(e.getMessage());
                return;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }

            if (response.equals("YES")) {
                System.out.println("Node " + targetNodeID + "Vote Me!");
                voteInfos[targetNodeID] = VoteInfo.VOTE_ME;
                requestVoteTimers[targetNodeID].cancel();
            } else if (response.equals("NO")) {
                System.out.println("Node " + targetNodeID + "Vote Others.");
                voteInfos[targetNodeID] = VoteInfo.VOTE_OTHERS;
                requestVoteTimers[targetNodeID].cancel();
            }

            // if got majority votes
            if (countVotes(voteInfos) > server.getTotalNodes() / 2) {
                System.out.println("Node " + server.getNodeInd() + " become a leader of term " + server.getTerm());
                // cancel electionTimer Task
                electionTimer.cancel();
                server.setStatus(ServerStatus.LEADER);
                // Began Leadership
                server.getConsensusModule().startLeadership();
            }
        }
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