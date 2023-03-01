/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp5_servidor;

import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author MarcosGM
 */
public class MainServidor {
    private SSLServerSocketFactory sSLServerSocketFactory;
    private SSLServerSocket sSLServerSocket;
    private SSLSocket sSLSocket_Cliente;
    private ConnectionThreadServer clienteAceptado;
    @SuppressWarnings("ProtectedField")
    protected static int ordenadores = 544;
    @SuppressWarnings("ProtectedField")
    protected static int NUM_CONEXIONES = 0;
    private final int MAX_CONEXIONES = 5;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "serverKey.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "claveSecreta");
        try {
            final String firmaAlumno = " MARCOS GOMEZ MEDINA \n";
            System.out.println("SERVIDOR: \n" + firmaAlumno);
            MainServidor app = new MainServidor();
            app.iniciar();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
   
    public void iniciar() {
        try {
            sSLServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            sSLServerSocket = (SSLServerSocket) sSLServerSocketFactory.createServerSocket(9090);
            while(true) {
                System.out.println("SERVIDOR: Esperando conexion..");
                sSLSocket_Cliente = (SSLSocket)sSLServerSocket.accept();
                if(NUM_CONEXIONES < MAX_CONEXIONES) { 
                    clienteAceptado = new ConnectionThreadServer(sSLSocket_Cliente);
                    clienteAceptado.start();
                    NUM_CONEXIONES++;
                } else {
                    System.out.println("SERVIDOR: Maximo de conexiones alcanzada.");
                }
            }
        } catch (IOException ex) {
            System.out.println("SERVIDOR: Exception final" + ex.getMessage());
        } finally {
            System.out.println("SERVIDOR: Servidor cerrado.");
        }
    }
}
