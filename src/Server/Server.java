package Server;

import org.apache.xmlrpc.*;
import java.util.*;

public class Server {

	private String[] serverList;


	// server status
	private  ServerStatus status = ServerStatus.FOLLOWER;

	// server term
	private int term;

	// total nodes
	private int totalNodes;

	// server index
	private int nodeInd;

	// consensus module
	private ConsensusModule consensusModule;



	// A simple ping, simply returns True
	public boolean ping() {
		System.out.println("Ping()");
		return true;
	}

	// Given a hash value, return the associated block
	public byte[] getblock(String hashvalue) {
		System.out.println("GetBlock(" + hashvalue + ")");

		byte[] blockData = new byte[16];
		for (int i = 0; i < blockData.length; i++) {
			blockData[i] = (byte) i;
		}

		return blockData;
	}

	// Store the provided block
	public boolean putblock(byte[] blockData) {
		System.out.println("PutBlock()");

		return true;
	}

	// Determine which of the provided blocks are on this server
	public Vector hasblocks(Vector hashlist) {
		System.out.println("HasBlocks()");

		return hashlist;
	}

	// Returns the server's FileInfoMap
	public Hashtable getfileinfomap() {
		System.out.println("GetFileInfoMap()");

		// file1.dat
		Integer ver1 = new Integer(3); // file1.dat's version

		Vector<String> hashlist = new Vector<String>(); // file1.dat's hashlist
		hashlist.add("h0");
		hashlist.add("h1");
		hashlist.add("h2");

		Vector fileinfo1 = new Vector();
		fileinfo1.add(ver1);
		fileinfo1.add(hashlist);

		// file2.dat
		Integer ver2 = new Integer(5); // file2.dat's version

		Vector fileinfo2 = new Vector();
		fileinfo2.add(ver2);
		fileinfo2.add(hashlist); // use the same hashlist

		Hashtable<String, Object> result = new Hashtable<String, Object>();
		result.put("file1.dat", fileinfo1);
		result.put("file2.dat", fileinfo2);

		return result;
	}

	// Update's the given entry in the fileinfomap
	public boolean updatefile(String filename, int version, Vector hashlist) {
		System.out.println("UpdateFile(" + filename + ")");

		return true;
	}

	// PROJECT 3 APIs below

	// Queries whether this metadata store is a leader
	// Note that this call should work even when the server is "crashed"
	public boolean isLeader() {
		System.out.println("IsLeader()");
		return true;
	}

	// "Crashes" this metadata store
	// Until Restore() is called, the server should reply to all RPCs
	// with an error (unless indicated otherwise), and shouldn't send
	// RPCs to other servers
	public boolean crash() {
		System.out.println("Crash()");
		return true;
	}

	// "Restores" this metadata store, allowing it to start responding
	// to and sending RPCs to other nodes
	public boolean restore() {
		System.out.println("Restore()");
		return true;
	}

	// "IsCrashed" returns the status of this metadata node (crashed or not)
	// This method should always work, even when the node is crashed
	public boolean isCrashed() {
		System.out.println("IsCrashed()");
		return true;
	}

	public static void main (String [] args) {

		try {

			System.out.println("Attempting to start XML-RPC Server.Server...");

			WebServer rpcserver = new WebServer(8080);
			Server baseServer = new Server();
			rpcserver.addHandler("surfstore", baseServer);
			rpcserver.start();

			System.out.println("Started successfully.");

			new Thread(new ElectionModule(baseServer)).start();


			System.out.println("Accepting requests. (Halt program to stop.)");

		} catch (Exception exception){
			System.err.println("Server.Server: " + exception);
		}
	}

	public ServerStatus getStatus() {
		return status;
	}

	public void setStatus(ServerStatus status) {
		this.status = status;
	}

	public int getTerm() {
		return term;
	}

	public void increaseTerm() {
		term++;
	}

	public void updateTerm(int term) {
		this.term = term;
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public int getNodeInd() {
		return nodeInd;
	}

	public String[] getServerList() {
		return serverList;
	}

	public ConsensusModule getConsensusModule() {
		return consensusModule;
	}
}
