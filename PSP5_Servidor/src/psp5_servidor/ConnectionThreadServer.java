/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp5_servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.net.ssl.SSLSocket;
/**
 *
 * @author MarcosGM
 */
public class ConnectionThreadServer extends Thread{
    private SSLSocket cliente;
    private PrintWriter enviar;
    private BufferedReader recibir;
    private String[] arr;
    private String inConsoleString = " ";
    private boolean salir;

    /**
     * Constructor que recibe el Socket con la conexion aceptada.
     * @param cliente 
     */
    public ConnectionThreadServer(SSLSocket cliente) {
        this.cliente = cliente;
    }
    
    @Override
    public void run (){
        System.out.println("SERVIDOR: Conexión aceptada");
        try {
            enviar = new PrintWriter(cliente.getOutputStream(), true);
            recibir = new BufferedReader (new InputStreamReader(cliente.getInputStream()));
            salir = true;
            while (salir) {   
                System.out.println("SERVIDOR: Recibiendo comando cliente...");
                inConsoleString = desencriptarString(recibir.readLine());
                arr = inConsoleString.split(",");
                System.out.println("SERVIDOR: Procesando comando...");
                switch(arr[0]) {
                    case "1" :
                        enviar.println(encriptarString("Stock de ordenadores: " + consultarAlmacen()));
                        break;
                    case "2" :
                        enviar.println(encriptarString("Stock de ordenadores: " + añadirOrdenadores(Integer.parseInt(arr[1]))));
                        break;
                    case "3" :
                        enviar.println(encriptarString("Stock de ordenadores: " + retirarOrdenadores(Integer.parseInt(arr[1]))));
                        break;
                    case "salir" :
                        System.out.println("Salir");
                        salir = false;
                        break;
                }
            }
            System.out.println("SERVIDOR: Conexión terminada");
            this.cliente.close();
            recibir.close();
            enviar.close();
        } catch (IOException ex) {
            System.out.println("SERVIDOR : Se ha perdido la conexion repentinamente con un cliente");
        } finally {
            MainServidor.NUM_CONEXIONES--;
        }
    }
    
    /**
     * Metodo que permite consultar el stock de ordenadores.
     * @return 
     */
    public String consultarAlmacen(){
        return String.valueOf(MainServidor.ordenadores);
    }
    
    /**
     * Metodo que permite añadir stock de ordenadores. Sincronizado para que
     * no haya conflicto entre clientes.
     * @param añadir
     * @return 
     */
    public synchronized String añadirOrdenadores(int añadir){
        MainServidor.ordenadores += añadir;
        return String.valueOf(MainServidor.ordenadores);
    }
    
    /**
     * Metodo que permite quitar stock de ordenadores. Sincronizado para que
     * no haya conflicto entre clientes.
     * @param restar
     * @return 
     */
    public synchronized String retirarOrdenadores(int restar){
        MainServidor.ordenadores -= restar;
        return String.valueOf(MainServidor.ordenadores);
    }
    
    /**
     * Metodo para encriptar la informacion enviada a los clientes.
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
     * Metodo para desencriptar la informacion enviada a los clientes.
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