package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 *  <li>Todos los vértices son NEGROS o ROJOS.</li>
 *  <li>La raíz es NEGRA.</li>
 *  <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la raíz).</li>
 *  <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 *  <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 *      mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>>
    extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
            color = Color.NINGUNO;
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
            if (color == Color.NEGRO)
                return String.format("N{%s}", elemento);
            else if (color == Color.ROJO)
                return String.format("R{%s}", elemento);
            return "";
        }

        /**
         * Compara el vértice con otro objeto. La comparación es
         * <em>recursiva</em>.
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de
         *         éste vértice, los descendientes de ambos son recursivamente
         *         iguales, y los colores son iguales; <code>false</code> en
         *         otro caso.
         */
        @Override public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
                VerticeRojinegro vertice = (VerticeRojinegro)objeto;
            return (color == vertice.color && super.equals(vertice));
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros
     * de {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() { super(); }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol
     * rojinegro tiene los mismos elementos que la colección recibida.
     * @param coleccion la colección a partir de la cual creamos el árbol
     *        rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link
     * VerticeRojinegro}.
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de {@link
     *         VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        return verticeRojinegro(vertice).color;
    }

    private VerticeRojinegro verticeRojinegro(VerticeArbolBinario<T> vertice) {
        return (VerticeRojinegro) vertice;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método {@link
     * ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * @param elemento el elemento a agregar.
     */
    @Override public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro v = verticeRojinegro(ultimoAgregado);
        v.color = Color.ROJO;
        agrega(v);
    }

    private void agrega(VerticeRojinegro v) {
        if (v.padre == null)
            v.color = Color.NEGRO;
        else {
            VerticeRojinegro p = verticeRojinegro(v.padre);
            if (!esRojo(p))
                return;
            VerticeRojinegro a = verticeRojinegro(p.padre);
            VerticeRojinegro t = verticeRojinegro(tio(v));
            if (esRojo(t)) {
                t.color = p.color = Color.NEGRO;
                a.color = Color.ROJO;
                agrega(a);
            } else {
               if ((!esIzquierdo(p) && esIzquierdo(v))
                   || (esIzquierdo(p) && !esIzquierdo(v))) {
                   if (esIzquierdo(p))
                       super.giraIzquierda(p);
                   else
                       super.giraDerecha(p);
                   VerticeRojinegro u = v;
                   v = p;
                   p = u;
               }
               p.color = Color.NEGRO;
               a.color = Color.ROJO;
               if (esIzquierdo(v))
                   super.giraDerecha(a);
               else
                   super.giraIzquierda(a);
            }
        }
    }

    private boolean esRojo(VerticeRojinegro vertice) {
        return (vertice != null && vertice.color == Color.ROJO);
    }

    private Vertice tio (Vertice vertice) {
        Vertice abuelo = vertice.padre.padre;
        return (abuelo.izquierdo == vertice.padre) ? abuelo.derecho : abuelo.izquierdo;
    }

    private boolean esIzquierdo(Vertice vertice) {
        return vertice.padre.izquierdo == vertice;
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene
     * el elemento, y recolorea y gira el árbol como sea necesario para
     * rebalancearlo.
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override public void elimina(T elemento) {
        VerticeRojinegro v = verticeRojinegro(busca(elemento));
        if (v == null)
            return;
        elementos--;
        if (v.izquierdo != null && v.derecho != null)
            v = verticeRojinegro(intercambiaEliminable(v));
        VerticeRojinegro f = null;
        if (v.izquierdo == null && v.derecho == null) {
            f = verticeRojinegro(nuevoVertice(null));
            f.color = Color.NEGRO;
            v.izquierdo = f;
            f.padre = v;
        }
        VerticeRojinegro h = verticeRojinegro(hijo(v));
        eliminaVertice(v);
        if (esRojo(h))
            h.color = Color.NEGRO;
        else if (!esRojo(v) && !esRojo(h))
            elimina(h);
        if (f != null)
            eliminaVertice(f);
    }

    private Vertice hijo(Vertice vertice) {
        return (vertice.izquierdo == null) ? vertice.derecho : vertice.izquierdo;
    }

    private void elimina(VerticeRojinegro v) {
        if (v.padre == null)
            return;
        VerticeRojinegro p = verticeRojinegro(v.padre);
        VerticeRojinegro h = verticeRojinegro(hermano(v));
        if (esRojo(h)) {
            p.color = Color.ROJO;
            h.color = Color.NEGRO;
            if (esIzquierdo(v))
                super.giraIzquierda(p);
            else
                super.giraDerecha(p);
            h = verticeRojinegro(hermano(v));
        }
        VerticeRojinegro hi = verticeRojinegro(h.izquierdo);
        VerticeRojinegro hd = verticeRojinegro(h.derecho);
        if (!esRojo(p) && !esRojo(h) && !esRojo(hi) && !esRojo(hd)) {
            h.color = Color.ROJO;
            elimina(p);
        } else {
            if (esRojo(p) && !esRojo(h) && !esRojo(hi) && !esRojo(hd)) {
                h.color = Color.ROJO;
                p.color = Color.NEGRO;
            } else {
                if ((esIzquierdo(v) && esRojo(hi) && !esRojo(hd))
                    || (!esIzquierdo(v) && !esRojo(hi) && esRojo(hd))) {
                    h.color = Color.ROJO;
                    if (esRojo(hi))
                        hi.color = Color.NEGRO;
                    else
                        hd.color = Color.NEGRO;
                    if (esIzquierdo(v))
                        super.giraDerecha(h);
                    else
                        super.giraIzquierda(h);
                    h = verticeRojinegro(hermano(v));
                    hi = verticeRojinegro(h.izquierdo);
                    hd = verticeRojinegro(h.derecho);
                }
                h.color = p.color;
                p.color = Color.NEGRO;
                if (esIzquierdo(v)) {
                    hd.color = Color.NEGRO;
                    super.giraIzquierda(p);
                } else {
                    hi.color = Color.NEGRO;
                    super.giraDerecha(p);
                }
            }
        }
    }

    private Vertice hermano(Vertice vertice) {
        Vertice p = vertice.padre;
        return (p.izquierdo == vertice) ? p.derecho : p.izquierdo;
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la izquierda " +
                                                "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la
     * clase, porque se desbalancean.
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException("Los árboles rojinegros no " +
                                                "pueden girar a la derecha " +
                                                "por el usuario.");
    }
}
