/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp5_cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.net.ssl.*;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author MarcosGM
 */
public class ConnectionThreadCliente extends Thread {
    private String puerto;
    private String ip;
    private String resultadoString;
    private SSLSocket SSLSocketCliente;
    private SSLSocketFactory sSLSocketFactory;
    private PrintWriter enviar;
    private BufferedReader recibir;
    
    @Override
    public void run (){
        try {
            BufferedReader inConsoleBufferedReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader inConsoleBufferedReader2 = new BufferedReader(new InputStreamReader(System.in));
            String inConsoleString;
            String inConsoleString2;
            boolean continueBool = true;
            boolean continueOrOut;
            System.out.println("Selecciona el puerto de conexión y la IP: \n"
                    + "1. 9090 / localhost\n"
                    + "2. 9191 / 127.0.0.1\n"
                    + "3. 9393 / 192.168.0.1");
            inConsoleString = inConsoleBufferedReader.readLine();
            switch(inConsoleString) {
                case "1" :
                    System.out.println("Conectando al puerto 9090...");
                    ip = "localhost";
                    puerto = "9090";
                    break;
                case "2" :
                    System.out.println("Conectando al puerto 9191...");
                    ip = "127.0.0.1";
                    puerto = "9191";
                    break;
                case "3" :
                    System.out.println("Conectando al puerto 9393...");
                    ip = "192.168.0.1";
                    puerto = "9393";
                    break;
                case "Salir" :
                    continueBool = false;
                    break;   
                default :
                    System.out.println("Debe introducir el número "
                            + "correspondiente a la operación deseada.");
                    break;
            }
            System.out.println("Conectando..");
            sSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocketCliente = (SSLSocket)sSLSocketFactory.createSocket(this.ip, Integer.parseInt(this.puerto));
            SSLSocketCliente.setSoTimeout(5*1000);
            System.out.println("Conectado..");
            while (continueBool) {
                enviar = new PrintWriter (SSLSocketCliente.getOutputStream(),true);
                recibir = new BufferedReader (new InputStreamReader (SSLSocketCliente.getInputStream()));
                mainMenu();
                inConsoleString = inConsoleBufferedReader.readLine();
                enviar.flush();
                switch(inConsoleString) {
                    case "1" :
                        System.out.println("Enviando comando..");
                        enviar.println(encriptarString("1"));
                        System.out.println("Recibiendo respuesta..");
                        resultadoString = desencriptarString(recibir.readLine());
                        respuestaServidor();
                        break;
                    case "2" :
                        System.out.println("Cuantos ordenadores quieres añadir al stock?");
                        inConsoleString = inConsoleBufferedReader.readLine();
                        String añadir = inConsoleString;
                        System.out.println("Enviando comando..");
                        enviar.println(encriptarString("2," + añadir));
                        System.out.println("Recibiendo respuesta..");
                        resultadoString = desencriptarString(recibir.readLine());
                        respuestaServidor();
                        break;
                    case "3" :
                        System.out.println("Cuantos ordenadores quieres añadir al stock?");
                        inConsoleString = inConsoleBufferedReader.readLine();
                        String retirar = inConsoleString;
                        System.out.println("Enviando comando..");
                        enviar.println(encriptarString("3," + retirar));
                        System.out.println("Recibiendo respuesta..");
                        resultadoString = desencriptarString(recibir.readLine());
                        respuestaServidor();
                        break;
                    default :
                        System.out.println("Debe introducir el número "
                                + "correspondiente a la operación deseada.");
                        break;
                }
                enviar.flush();
                continueOrOut = true;
                System.out.println("Desea realizar otra operación? s/n");
                while(continueOrOut){
                    inConsoleString2 = inConsoleBufferedReader2.readLine();
                    switch (inConsoleString2) {
                        case "s" :
                            continueBool = true;
                            continueOrOut = false;
                            break;
                        case "n" :
                            continueBool = false;
                            continueOrOut = false;
                            enviar.println(encriptarString("salir,0"));
                            break;
                        default:
                            System.out.println("Seleccione --s-- para Si hacer "
                                    + "una nueva consulta o --n-- para "
                                    + "No realizar mas consultas.");
                            break;
                    }
                }  
            }
            SSLSocketCliente.close();
        } catch (IOException ex) {
            System.out.println("Error ----> " + ex.getMessage());
        }    
    }
    
    /**
     * Muestra el menu  por consola.
     */
    private void mainMenu(){
        System.out.println("Seleccione una operación:\n"
                         + "1. Consultar stock del almacén.\n"
                         + "2. Añadir ordenadores al almacén.\n"
                         + "3. Retirar ordenadores del almacén."
        );
    }
    
    /**
     * Imprime la respuesta del servidor.
     */
    public void respuestaServidor(){
        System.out.println(resultadoString);
    }
    
    /**
     * Metodo que encripta los datos que se envian.
     * @param msg
     * @return 
     */
    public String encriptarString (String msg){
        String cadenaEncriptada = "";
        try {
            cadenaEncriptada = Base64.getEncoder().encodeToString(msg.getBytes("utf-8"));
        } catch (UnsupportedEncodingException  ex) {
            System.out.println("Error ----> " + ex.getMessage());
        }
        return cadenaEncriptada;
    }
    
    /**
     * Metodo que desencripta los datos que se reciben.
     * @param msg
     * @return 
     */
    public String desencriptarString (String msg) {
        String cadenaDesencriptada = "";
        try {
            byte[] decode = Base64.getDecoder().decode(msg.getBytes());
            cadenaDesencriptada = new String(decode, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error ----> " + ex.getMessage());
        }
        return cadenaDesencriptada;
    }
}
