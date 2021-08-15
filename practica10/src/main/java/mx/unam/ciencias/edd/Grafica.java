package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>,
                          ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* La lista de vecinos del vértice. */
        public Lista<Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            color = Color.NINGUNO;
            vecinos = new Lista<Vecino>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override public int getIndice() {
            return indice;
        }

        /* Compara dos vértices por distancia. */
        @Override public int compareTo(Vertice vertice) {
            if (distancia < vertice.distancia)
                return -1;
            else if (distancia == vertice.distancia)
                return 0;
            return 1;
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /* Construye un nuevo vecino con el vértice recibido como vecino y el
         * peso especificado. */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override public T get() {
            return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override public int getGrado() {
            return vecino.getGrado();
        }

        /* Regresa el color del vecino. */
        @Override public Color getColor() {
            return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /* Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino. */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Lista<Vertice>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
        if (elemento == null || contiene(elemento))
            throw new IllegalArgumentException();
        vertices.agrega(new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es
     *         igual a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (a.equals(b) || sonVecinos(a, b) || peso <= 0)
            throw new IllegalArgumentException();
        Vertice v = buscaVertice(a);
        Vertice u = buscaVertice(b);
        v.vecinos.agrega(new Vecino(u, peso));
        u.vecinos.agrega(new Vecino(v, peso));
        aristas++;
    }

    private Vertice buscaVertice(T elemento) {
        for (Vertice v : vertices)
            if (v.elemento.equals(elemento))
                return v;
        throw new NoSuchElementException();
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException();
        Vertice v = buscaVertice(a);
        Vertice u = buscaVertice(b);
        v.vecinos.elimina(buscaVecino(v, u));
        u.vecinos.elimina(buscaVecino(u, v));
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        for (Vertice v : vertices)
            if (v.elemento.equals(elemento))
                return true;
        return false;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        Vertice v = buscaVertice(elemento);
        vertices.elimina(v);
        for (Vecino u : v.vecinos) {
            u.vecino.vecinos.elimina(buscaVecino(u.vecino, v));
            aristas--;
        }
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        Vertice v = buscaVertice(a);
        Vertice u = buscaVertice(b);
        return buscaVecino(v, u) != null && buscaVecino(u, v) != null;
    }

    private Vecino buscaVecino(Vertice v, Vertice u) {
        for (Vecino w : v.vecinos) {
            if (w.vecino == u)
                return w;
        }
        return null;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen v
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a
     *         los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        if (!sonVecinos(a, b))
            throw new IllegalArgumentException();
        return buscaVecino(buscaVertice(a), buscaVertice(b)).peso;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a
     * los elementos recibidos.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *        contienen a los elementos recibidos.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso
     *         es menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        if (!sonVecinos(a, b) || peso <= 0)
            throw new IllegalArgumentException();
        Vertice v = buscaVertice(a);
        Vertice u = buscaVertice(b);
        buscaVecino(v, u).peso = peso;
        buscaVecino(u, v).peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        return buscaVertice(elemento);
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null ||
            (vertice.getClass() != Vertice.class &&
             vertice.getClass() != Vecino.class))
            throw new IllegalArgumentException("Vértice inválido");
        if (vertice.getClass() == Vertice.class) {
            Vertice v = (Vertice) vertice;
            v.color = color;
        } if (vertice.getClass() == Vecino.class) {
            Vecino u = (Vecino) vertice;
            u.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
        bdfs(vertices.getPrimero(), (v) -> {}, new Pila<Vertice>());
        for (Vertice v : vertices)
            if (v.color != Color.NEGRO)
                return false;
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice v : vertices)
            accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        bdfs(buscaVertice(elemento), accion, new Cola<Vertice>());
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        bdfs(buscaVertice(elemento), accion, new Pila<Vertice>());
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
    }

    private void bdfs(Vertice w, AccionVerticeGrafica<T> accion, MeteSaca<Vertice> m) {
        paraCadaVertice((v) -> setColor(v, Color.ROJO));
        w.color = Color.NEGRO;
        m.mete(w);
        while (!m.esVacia()) {
            Vertice u = m.saca();
            accion.actua(u);
            for (Vecino v : u.vecinos) {
                if (v.getColor() == Color.ROJO) {
                    v.vecino.color = Color.NEGRO;
                    m.mete(v.vecino);
                }
            }
        }
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
        String s = "";
        for (Vertice v : vertices)
            s += v.elemento + ", ";
        String r = "";
        for (Vertice v : vertices) {
            setColor(v, Color.ROJO);
            for (Vecino u : v.vecinos) {
                if (u.getColor() != Color.ROJO)
                    r += String.format("(%s, %s), ", v.get(), u.get());
            }
        }
        paraCadaVertice((v) -> setColor(v, Color.NINGUNO));
        return String.format("{%s}, {%s}", s, r);
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if ((getElementos() != grafica.getElementos())
            || (aristas != grafica.aristas))
            return false;
        for (Vertice v : vertices) {
            if (!grafica.contiene(v.elemento))
                return false;
            for (Vecino u : v.vecinos) {
                if (!grafica.sonVecinos(v.get(), u.get()))
                    return false;
            }
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * @param origen el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una
     *         trayectoria de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        Vertice s = buscaVertice(origen);
        Vertice t = buscaVertice(destino);
        Lista<VerticeGrafica<T>> l = new Lista<VerticeGrafica<T>>();
        if (s == t) {
            l.agrega(s);
            return l;
        }
        paraCadaVertice((v) -> setDistancia(vertice(v), -1));
        s.distancia = 0;
        Cola<Vertice> cola = new Cola<Vertice>();
        cola.mete(s);
        while (!cola.esVacia()) {
            Vertice u = cola.saca();
            for (Vecino v : u.vecinos) {
                if (getDistancia(v) == -1) {
                    setDistancia(v.vecino, u.distancia + 1);
                    cola.mete(v.vecino);
                }
            }
        }
        if (t.distancia == -1)
            return l;
        BuscadorCamino buscador = (v, u) -> (v.distancia == u.vecino.distancia + 1);
        return reconstruyeTrayectorias(buscador, l, t, s);
    }

    private void setDistancia(Vertice v, double distancia) {
        v.distancia = distancia;
    }

    private double getDistancia(Vecino v) {
        return v.vecino.distancia;
    }

    private Vertice vertice(VerticeGrafica<T> vertice) {
        return (Vertice) vertice;
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento
     * de destino.
     * @param origen el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en
     *         la gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        Vertice s = buscaVertice(origen);
        Vertice t = buscaVertice(destino);
        Lista<VerticeGrafica<T>> l = new Lista<VerticeGrafica<T>>();
        if (s == t) {
            l.agrega(s);
            return l;
        }
        paraCadaVertice((v) -> setDistancia(vertice(v), Double.MAX_VALUE));
        s.distancia = 0;
        int n = getElementos();
        MonticuloDijkstra<Vertice> m;
        if (aristas > ((n * (n - 1)) / 2) - n)
            m = new MonticuloArreglo<Vertice>(vertices);
        else
            m = new MonticuloMinimo<Vertice>(vertices);
        while (!m.esVacia()) {
            Vertice u = m.elimina();
            for (Vecino v : u.vecinos) {
                if (getDistancia(v) > u.distancia + v.peso) {
                    setDistancia(v.vecino, u.distancia + v.peso);
                    m.reordena(v.vecino);
                }
            }
        }
        if (t.distancia == Double.MAX_VALUE)
            return l;
        BuscadorCamino buscador = (v, u) -> (v.distancia == u.vecino.distancia + u.peso);
        return reconstruyeTrayectorias(buscador, l, t, s);
    }

    private Lista<VerticeGrafica<T>>
    reconstruyeTrayectorias(BuscadorCamino buscador,
                            Lista<VerticeGrafica<T>> l, Vertice t, Vertice s) {
        l.agrega(t);
        while (t != s) {
            for (Vecino v : t.vecinos) {
                if (buscador.seSiguen(t, v)) {
                    l.agrega(v.vecino);
                    t = v.vecino;
                }
            }
        }
        return l.reversa();
    }
}
