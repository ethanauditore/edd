package mx.unam.ciencias.edd;

/**
 * Clase para colas genéricas.
 */
public class Cola<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la cola.
     * @return una representación en cadena de la cola.
     */
    @Override public String toString() {
        String s = "";
        Nodo nodo = cabeza;
        while (nodo != null) {
            s += String.format("%s,", nodo.elemento);
            nodo = nodo.siguiente;
        }
        return s;
    }

    /**
     * Agrega un elemento al final de la cola.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException();
        Nodo nodo = new Nodo(elemento);
        if (rabo == null)
            cabeza = rabo = nodo;
        else {
            rabo.siguiente = nodo;
            rabo = nodo;
        }
    }
}
