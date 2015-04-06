package sample;

import org.apache.catalina.*;
import org.apache.catalina.session.ManagerBase;

import java.io.IOException;

public class SampleSessionManager extends ManagerBase {

    @Override
    public void load() throws ClassNotFoundException, IOException {
        System.out.println("*** load");
    }

    @Override
    public void unload() throws IOException {
        System.out.println("*** unload");
    }

    @Override
    public void startInternal() throws LifecycleException {
        System.out.println("*** startInternal");

        super.startInternal();
        setState(LifecycleState.STARTING);
    }

    @Override
    public void stopInternal() throws LifecycleException {
        System.out.println("*** stopInternal");

        setState(LifecycleState.STOPPING);
        super.stopInternal();
    }


    @Override
    public void add(Session session) {
        System.out.println("*** session add: " + session);
        super.add(session);
    }

    @Override
    public void changeSessionId(Session session) {
        System.out.println("*** change session id: " + session);
        super.changeSessionId(session);
    }

    @Override
    public void changeSessionId(Session session, String newId) {
        System.out.println("*** change session id: " + session + ", " + newId);
        super.changeSessionId(session, newId);
    }

    @Override
    public Session createEmptySession() {
        System.out.println("*** create empty session");
        return super.createEmptySession();
    }

    @Override
    public Session createSession(String sessionId) {
        System.out.println("*** create session: " + sessionId);
        return super.createSession(sessionId);
    }

    @Override
    public Session findSession(String id) throws IOException {
        System.out.println("*** find session: " + id);
        return super.findSession(id);
    }

    @Override
    public Session[] findSessions() {
        System.out.println("*** find sessions");
        return super.findSessions();
    }

    @Override
    public void remove(Session session) {
        System.out.println("*** remove session: " + session);
        super.remove(session);
    }

    @Override
    public void remove(Session session, boolean update) {
        System.out.println("*** remove session: " + session + ", " + update);
        super.remove(session, update);
    }
}
