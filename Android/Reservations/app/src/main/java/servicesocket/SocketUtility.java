package servicesocket;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import network.request.ROMPResponse;
import network.request.Request;
import network.request.interfaces.Requete;

public class SocketUtility {
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket soc;
    private Requete req;
    private boolean auth = false;
    private String errorMessage = "NOT AUTH REALIZED";
    public static String addressSer = "192.168.0.25";
    public static Integer portSer = 6996;
    private static SocketUtility instance;
    public static String LOGIN;
    public static String PASSWORD;
    private ROMPResponse reponse;


    public SocketUtility(String login, String password) throws InterruptedException {
        SocketCreation sc = new SocketCreation();

        sc.execute("CONNECTION", login, password);

        while(true){
            synchronized (sc) {
                if (errorMessage != null) {
                    return;
                }
            }
            Thread.sleep(500);
        }

    }

    public synchronized static SocketUtility getInstance() throws InterruptedException { // Singleton
        if (instance == null)
            instance = new SocketUtility(LOGIN, PASSWORD);
        else if(!instance.IsAuthenticated())
            instance = new SocketUtility(LOGIN, PASSWORD);

        return instance;
    }

    public boolean IsAuthenticated(){
        return auth;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void sendRequest(String arg, String requestType) throws Exception {
        if(!IsAuthenticated())
            throw new Exception("Not Authenticated: " + errorMessage);

        SocketCreation sc = new SocketCreation();

        sc.execute("SEND", arg, requestType);
        //while(errorMessage == null);
        while(soc.isConnected()){
            synchronized (sc) {
                if (errorMessage != null) {
                    return;
                }
            }
            Thread.sleep(500);
        }


    }

    public ROMPResponse getResponse() throws Exception {
        if(!IsAuthenticated())
            throw new Exception("Not Authenticated: " + errorMessage);

        SocketCreation sc = new SocketCreation();

        sc.execute("RECEIVE");

        //while(errorMessage == null);
        while(soc.isConnected()){
            synchronized (sc) {
                if (errorMessage != null) {
                    return reponse;
                }
            }
            Thread.sleep(500);
        }
        return null;
    }

    public void disconnect() throws IOException {
        auth = false;
        soc.close();
    }

    private class SocketCreation extends AsyncTask<String, Void, String> {

        public SocketCreation() {
            errorMessage = null;
        }

        @Override
        protected String doInBackground(String... strings) {

                ROMPResponse rep;

                try {

                    switch(strings[0])
                    {
                        case "CONNECTION":
                            if (soc != null && soc.isConnected())
                                soc.close();

                            soc = new Socket(addressSer, portSer);




                            req = new Request(Request.REQUEST_ROMP, strings[1] + "#" + strings[2], "LoginMobile", soc);

                            oos = new ObjectOutputStream(soc.getOutputStream());
                            ois = new ObjectInputStream(soc.getInputStream());

                            oos.writeObject(req);

                            oos.flush();

                            rep = (ROMPResponse) ois.readObject();

                            switch (rep.getCode()) {
                                case ROMPResponse.OK:
                                    auth = true;
                                    synchronized (this) {
                                        errorMessage = "OK";
                                    }
                                    break;
                                case ROMPResponse.NOK:
                                    auth = false;
                                    synchronized (this) {
                                        errorMessage = rep.getChargeUtile();
                                    }
                                    break;
                                case ROMPResponse.ERROR:
                                    auth = false;
                                    synchronized (this) {
                                        errorMessage = rep.getChargeUtile();
                                    }
                                    break;
                            }
                            break;
                        case "SEND":
                            rep = new ROMPResponse(ROMPResponse.OK, strings[1], strings[2]);
                            oos.writeObject(rep);
                            oos.flush();
                            synchronized (this) {
                                errorMessage = "OK";
                            }
                            //notify();
                            break;
                        case "RECEIVE":
                            reponse = (ROMPResponse) ois.readObject();
                            synchronized (this) {
                                errorMessage = "OK";
                            }
                            //notify();
                            break;
                    }
                } catch (UnknownHostException e) {
                    synchronized (this) {
                        errorMessage = e.getMessage();
                    }
                    return e.getMessage();
                } catch (IOException e) {
                    synchronized (this) {
                        errorMessage = e.getMessage();
                    }
                    return e.getMessage();
                } catch (ClassNotFoundException e) {
                    synchronized (this) {
                        errorMessage = e.getMessage();
                    }
                    return e.getMessage();
                } catch (Exception e) {
                    synchronized (this) {
                        errorMessage = e.getMessage();
                    }
                    return e.getMessage();
                }


            synchronized (this) {
                if (errorMessage == null)
                    errorMessage = "ERREUR INDEFINIE";
            }

            return errorMessage;
        }
    }

}
