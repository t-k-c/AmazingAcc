package com.tkctechnologies.codename_tkc.amazingacc;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


class Server {
    private Context context;
    private ServerSocket serverSocket;
    private int port;
    private static float[] accelerometer_values = new float[3]; /* accelerometer values will be placed here, just initializing with dummy stuff*/

    /* Pass the configuration throught the constructor*/
    Server(Context context, int port) {
        this.context = context;
        this.port = port;
        // context.equals(context);
    }

    void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    while (true) {
                        Log.i("AmazingAcc", "Waiting for client");
                        try {
                            final Socket socket = serverSocket.accept();/* Accept connections from clients (browsers) */
                            /*treating the socket on a different thread to avoid conflicts between different clients*/
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        /* Here you will have to make sure you remember the structure of the http request, because we will need the
                        *  requested file path requested for example 'index.html' will get the main page returned and 'acc.val'
                         *  will get the values
                        *  of the accelerometer readings */
                                        String request_first_line = "";
                                        while (request_first_line == "") {
                                            if (bufferedReader.ready()) {
                                                request_first_line = bufferedReader.readLine();
                                            }
                                        }
                        /* Since we are interested only with the first line of the http request
                        and the file path requested is the 2nd 'part' of the first line
                        (if we divided by a simple space ' ' eg GET / HTTP/1.1)*/
                                        String requested_path = request_first_line.split(" ")[1];
                                        String resp = study(requested_path);
                                        Log.i("AmazingAcc", "path: " + requested_path);
                                        OutputStream outputStream = socket.getOutputStream();
                                        outputStream.write(resp.getBytes());
                                        bufferedReader.close();
                                        outputStream.flush();
                                        outputStream.close();
                                        socket.close();

                                    } catch (IOException e) {
                                    }
                                }

                            }).start();

                        } catch (IOException e) {
                            Log.e("AmazingAcc", "Oops, that didn't work");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private String study(String path) {
        /* just a dummy header , you can create a full one */
        String page;

        if (path.equals("/") || path.equals("/index.html")) { /* Client requested the main interface */
            page = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>AmazingAcc</title>\n" +
                    "<style>\n" +
                    "    body,html{\n" +
                    "        height: 100%;\n" +
                    "        width: 100%;\n" +
                    "    }\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<center><h1 style=\"font-size: 7em;margin-top: 23%;\" id=\"aa\">AmazzingAcc</h1></center>\n" +
                    "<div style=\"position: absolute;top: 10px;left: 10px;\">\n" +
                    "    <form name=\"axes\">\n" +
                    "    <select name=\"param\">\n" +
                    "        <option>X axis</option>\n" +
                    "        <option>Y axis</option>\n" +
                    "        <option>Z axis</option>\n" +
                    "    </select></form>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "<script>\n" +
                    "    window.onload= function(){\n" +
                    "      setInterval(function(){\n" +
                    "          var xmlHtttpRequest = new XMLHttpRequest();\n" +
                    "          xmlHtttpRequest.onreadystatechange = function(){\n" +
                    "              if(this.readyState===4 && this.status === 200){\n" +
                    "                  " +
                    "                  var h1 = document.getElementById('aa');\n" +
                    "                  /*    -ms-transform: rotate(7deg); /* IE 9\n" +
                    "                   -webkit-transform: rotate(7deg); /* Chrome, Safari, Opera\n" +
                    "                   transform: rotate(7deg); */\n" +
                    "                  if(axes.param.selectedIndex===0){\n" +
                    "                      h1.style.transform = 'rotateX('+this.responseText.split(\":\")[2]+'deg)'\n" +
                    "                  }\n" +
                    "                  if(axes.param.selectedIndex ===1){\n" +
                    "                      h1.style.transform = 'rotateY('+this.responseText.split(\":\")[1]+'deg)'\n" +
                    "                      console.log(this.responseText.split(\":\")[1]+'deg')" +
                    "                  }\n" +
                    "                  if(axes.param.selectedIndex === 2){\n" +
                    "                      h1.style.transform = 'rotateZ('+this.responseText.split(\":\")[0]+'deg)'\n" +
                    "                       console.log(this.responseText.split(\":\")[0]+'deg')" +
                    "                  }\n" +
                    "              }\n" +
                    "          };\n" +
                    "          xmlHtttpRequest.open('POST','amazing.acc',true);\n" +
                    "          xmlHtttpRequest.send();\n" +
                    "      },1);\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</html>";
        } else {
            int ax = (int) (Math.atan2(accelerometer_values[0], accelerometer_values[1]) / (Math.PI / 180));
            int ay = (int) (Math.atan2(accelerometer_values[1], accelerometer_values[2]) / (Math.PI / 180));
            int az = (int) (Math.atan2(accelerometer_values[0], accelerometer_values[2]) / (Math.PI / 180));
            page = (90 - ax) + ":" + (90 - ay) + ":" + (90 - az);
        }
        String header = "HTTP/1.1 200 OK\n" +
                "Date: Mon, 30 Oct 2017 19:15:56 GMT\n" +
                "Server: Programmers' Band \n" +
                "Last-Modified: Mon, 30 Oct 2017 19:15:56 GMT\n" +
                "Content-Length: " + page.getBytes().length + "\n" +
                "Content-Type: text/html\n" +
                "Connection: Closed";
        return header + "\n\n" + page;

    }

    static void setValues(float[] accValues) {
        accelerometer_values = accValues;
    }
}
