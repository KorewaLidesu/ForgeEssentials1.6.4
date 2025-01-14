package com.forgeessentials.snooper;

import com.forgeessentials.api.json.JSONException;
import com.forgeessentials.api.json.JSONObject;
import com.forgeessentials.api.snooper.Response;
import com.forgeessentials.util.OutputHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketHandler extends Thread {
    private SocketListner listner;
    public Socket socket;
    private OutputStream os;
    private InputStream is;

    public SocketHandler(Socket socket, SocketListner socketListner)
    {
        listner = socketListner;
        this.socket = socket;
        setName("ForgeEssentials - Snooper - SocketHandler #" + ModuleSnooper.id());
        start();
    }

    @Override
    public void run()
    {
        OutputHandler.debug("Snooper connection: " + socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());

        try
        {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            int i = is.read();
            byte[] inBuffer = new byte[is.available()];
            is.read(inBuffer);
            String inString = new String(inBuffer);
            String inDecr = Security.decrypt(inString, ModuleSnooper.key);

            String out;
            try
            {
                out = Security.encrypt(getResponce((byte) i, new JSONObject(inDecr)), ModuleSnooper.key);
            }
            catch (Exception e)
            {
                out = Security.encrypt(getResponce((byte) i, new JSONObject()), ModuleSnooper.key);
            }

            os.write(out.getBytes());
            os.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        close();
    }

    private String getResponce(byte i, JSONObject input)
    {
        try
        {
            Response responce = ResponseRegistry.getResponse(i);
            if (responce.allowed)
            {
                return responce.getResponce(input).toString();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void close()
    {
        try
        {
            socket.close();
            listner.socketList.remove(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.gc();
    }
}
