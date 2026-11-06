package model;

import java.util.HashMap;

public class CajaFuerte {

    private String numero1;
    private String numero2;
    private String numero3;

    private int progreso;

    private boolean abierta;

    private HashMap<String, String> secretillos;

    public CajaFuerte(String numero1, String numero2, String numero3) {
        this.numero1 = numero1;
        this.numero2 = numero2;
        this.numero3 = numero3;
        this.secretillos = new HashMap<>();
    }

    public String getNumero1() {
        return numero1;
    }

    public void setNumero1(String numero1) {
        this.numero1 = numero1;
    }

    public String getNumero2() {
        return numero2;
    }

    public void setNumero2(String numero2) {
        this.numero2 = numero2;
    }

    public String getNumero3() {
        return numero3;
    }

    public void setNumero3(String numero3) {
        this.numero3 = numero3;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public boolean isAbierta() {
        return abierta;
    }

    public void abrir(){
        if (progreso==3){
            abierta=true;//Mejor que un setter, porque así no puede cambiar el estado de la caja cualquier clase
        }
    }

    public void guardarSecretillo(String nombre, String valor){
        secretillos.put(nombre,valor);
    }

    public String consultarSecretillo(String nombre){
        return secretillos.get(nombre);
    }


}
