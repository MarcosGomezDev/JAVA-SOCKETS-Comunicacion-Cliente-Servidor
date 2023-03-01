/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package psp5_cliente;

/**
 *
 * @author MarcosGM
 */
public class MainCliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "clientTrustedCerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "claveClienteSecreta");
        try {
            final String firmaAlumno = " MARCOS GOMEZ MEDINA \n";
            System.out.println("CLIENTE: \n" + firmaAlumno);
            MainCliente app = new MainCliente();
            app.iniciar();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }    
    
    public void iniciar() {
        ConnectionThreadCliente connectionThreadCliente = new ConnectionThreadCliente();
        connectionThreadCliente.start();
    }
}
