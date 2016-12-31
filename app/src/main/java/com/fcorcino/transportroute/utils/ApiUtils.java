package com.fcorcino.transportroute.utils;

import android.content.Context;

import com.leaderapps.transport.model.PendingStop;
import com.leaderapps.transport.model.Reservation;
import com.leaderapps.transport.model.Route;
import com.leaderapps.transport.model.Stop;
import com.leaderapps.transport.model.Turn;
import com.leaderapps.transport.model.User;
import com.leaderapps.transport.transportrouteclient.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ApiUtils {

    /**
     * Makes a request to the server to create an user.
     *
     * @param user The user to be created.
     * @return Whether or not the user was created.
     */
    public static boolean createUser(Context context, User user) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_CREATE_USER);
            oos.writeObject(user);
            return dis.readBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Makes a request to log in an user.
     *
     * @param userName The user name of the user to attempt to log in.
     * @param password The password of the user to attempt to log in.
     * @return An user if the credentials were valid, otherwise null.
     */
    public static User logInUser(Context context, String userName, String password) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_LOG_IN_USER);
            dos.writeUTF(userName);
            dos.writeUTF(password);
            return (User) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the routes.
     *
     * @return The list of the routes.
     */
    public static ArrayList<Route> getRoutes(Context context) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_ROUTES);
            return (ArrayList<Route>) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the stops by route.
     *
     * @param routeId The id of the route to get the stops.
     * @return The list of stops for the route passed.
     */
    public static ArrayList<Stop> getStopsByRoute(Context context, String routeId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_STOPS_BY_ROUTE);
            dos.writeUTF(routeId);
            return (ArrayList<Stop>) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the turns by a route.
     *
     * @param routeId The id of the route to get the turns.
     * @return A list of the turn that belongs to the route passed.
     */
    public static ArrayList<Turn> getTurnsByRoute(Context context, String routeId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_TURNS_BY_ROUTE);
            return (ArrayList<Turn>) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the current reservation by an user.
     *
     * @param userId The id of the user to get the current reservation.
     * @return A reservation.
     */
    public static Reservation getCurrentReservation(Context context, String userId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_CURRENT_RESERVATION);
            dos.writeUTF(userId);
            return (Reservation) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to make a reservation.
     *
     * @param incompleteReservation The reservation to be created.
     * @return The reservation created.
     */
    public static Reservation makeReservation(Context context, Reservation incompleteReservation, double latitude, double longitude, String routeId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_MAKE_RESERVATION);
            oos.writeObject(incompleteReservation);
            dos.writeDouble(latitude);
            dos.writeDouble(longitude);
            dos.writeUTF(routeId);
            return (Reservation) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the turn by a driver.
     *
     * @param driverId The driver id to get the turn.
     * @return The turn of the driver.
     */
    public static Turn getTurnByDriver(Context context, String driverId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_TURN_BY_DRIVER);
            dos.writeUTF(driverId);
            return (Turn) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to get the pending stops by turn.
     *
     * @param turnId The turn id to get the pending stops.
     * @return A list of the pending stops by the turn.
     */
    public static ArrayList<PendingStop> getPendingStopsByTurn(Context context, String turnId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_PENDING_STOPS_BY_TURN);
            dos.writeUTF(turnId);
            return (ArrayList<PendingStop>) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to update a reservation status.
     *
     * @param reservationId The id of the reservation to be updated.
     * @return Whether or not it could be updated.
     */
    public static boolean updateReservation(Context context, String reservationId, String status) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_UPDATE_RESERVATION);
            dos.writeUTF(reservationId);
            dos.writeUTF(status);
            return dis.readBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Makes a request to update a turn location.
     *
     * @param turnId   The id of the turn to be updated.
     * @param location The location to be set.
     * @return Whether or not it could be updated.
     */
    public static boolean updateTurnLocation(Context context, String turnId, String location) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_UPDATE_TURN_LOCATION);
            dos.writeUTF(turnId);
            dos.writeUTF(location);
            return dis.readBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Makes a request to get the turn location.
     *
     * @param turnId The turn id to get the location.
     * @return The location of the turn.
     */
    public static Turn getTurn(Context context, String turnId) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_GET_TURN);
            dos.writeUTF(turnId);
            return (Turn) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Makes a request to update the turn last stop.
     *
     * @param turnId   The id of the turn to be updated.
     * @param lastStop The location to be set.
     * @return Whether or not it could be updated.
     */
    public static boolean updateTurnLastStop(Context context, String turnId, String lastStop) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_UPDATE_TURN_LAST_STOP);
            dos.writeUTF(turnId);
            dos.writeUTF(lastStop);
            return dis.readBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Makes a request to validate an username.
     *
     * @param username The username to be validated.
     * @return Whether or not the username exists.
     */
    public static boolean validateUsername(Context context, String username) {
        try {
            String serverIp = Utils.getSharedPreference(context, context.getString(R.string.settings_server_address_key));
            int serverPort = Integer.valueOf(Utils.getSharedPreference(context, context.getString(R.string.settings_server_port_key)));

            Socket s = new Socket(serverIp, serverPort);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            dos.writeInt(Constants.SERVICE_CODE_VALIDATE_USERNAME);
            dos.writeUTF(username);
            return dis.readBoolean();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
